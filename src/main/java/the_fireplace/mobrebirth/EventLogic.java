package the_fireplace.mobrebirth;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class EventLogic {

    public static void onDeath(LivingEntity entityLiving, DamageSource damageSource) {
        if(!entityLiving.getEntityWorld().isClient()) {
            if(checkDamageSource(entityLiving, damageSource)
            && checkGeneralEntityType(entityLiving)
            && checkSpecificEntityType(entityLiving))
                makeMobReborn(entityLiving);
        }
    }

    private static boolean checkDamageSource(LivingEntity entityLiving, DamageSource damageSource) {
        return MobRebirth.config.rebirthFromNonPlayer || damageSource.getAttacker() instanceof PlayerEntity;
    }

    private static boolean checkGeneralEntityType(LivingEntity entityLiving) {
        return entityLiving instanceof Monster || (MobRebirth.config.allowAnimals && entityLiving instanceof AnimalEntity);
    }

    private static boolean checkSpecificEntityType(LivingEntity entityLiving) {
        if (entityLiving instanceof WitherEntity || entityLiving instanceof EnderDragonEntity || entityLiving instanceof ElderGuardianEntity)
            return MobRebirth.config.allowBosses;
        if(entityLiving instanceof SlimeEntity)
            return MobRebirth.config.allowSlimes;
        return !MobRebirth.config.vanillaMobsOnly || isVanilla(entityLiving);
    }

    private static void makeMobReborn(LivingEntity entityLiving) {
        double rand = Math.random();
        if (rand <= MobRebirth.config.rebirthChance) {
            if (MobRebirth.config.rebornAsEggs && MobRebirth.spawnEggs.containsKey(entityLiving.getType())) {
                dropMobEgg(entityLiving.getType(), entityLiving);
            } else {
                createEntity(entityLiving);
                if (MobRebirth.config.multiMobCount > 0) {
                    double rand2 = Math.random();
                    switch(MobRebirth.config.multiMobMode.toLowerCase()) {
                        case "all":
                            if (rand2 <= MobRebirth.config.multiMobChance)
                                for (int i = 0; i < MobRebirth.config.multiMobCount; i++)
                                    createEntity(entityLiving);
                            break;
                        case "per-mob":
                            for (int i = 0; i < MobRebirth.config.multiMobCount; i++, rand2 = new Random().nextDouble())
                                if (rand2 <= MobRebirth.config.multiMobChance)
                                    createEntity(entityLiving);
                            break;
                        case "continuous":
                        default:
                            for (int i = 0; i < MobRebirth.config.multiMobCount; i++, rand2 = new Random().nextDouble())
                                if (rand2 <= MobRebirth.config.multiMobChance)
                                    createEntity(entityLiving);
                                else
                                    break;
                    }
                }
            }
        }
    }

    private static void dropMobEgg(EntityType<?> entityType, LivingEntity entityLiving) {
        entityLiving.dropItem(() -> MobRebirth.spawnEggs.get(entityType), 0);
    }

    private static void createEntity(LivingEntity entityLiving) {
        //Store
        LivingEntity entity;
        World worldIn = entityLiving.world;
        CompoundTag storedData = new CompoundTag();
        entityLiving.writeCustomDataToTag(storedData);
        ItemStack weapon = entityLiving.getStackInHand(Hand.MAIN_HAND);
        ItemStack offhand = entityLiving.getStackInHand(Hand.OFF_HAND);
        float health = entityLiving.getMaxHealth();
        //Read
        entity = (LivingEntity) entityLiving.getType().create(worldIn);
        if (entity == null)
            return;
        entity.headYaw = entity.yaw;
        entity.bodyYaw = entity.yaw;
        storedData.putInt("Health", (int) health);
        entity.readCustomDataFromTag(storedData);
        entity.setHealth(health);
        if (!weapon.isEmpty())
            entity.equipStack(EquipmentSlot.MAINHAND, weapon);
        if (!offhand.isEmpty())
            entity.equipStack(EquipmentSlot.OFFHAND, offhand);
        entity.setPos(entityLiving.getBlockPos().getX(), entityLiving.getBlockPos().getY(), entityLiving.getBlockPos().getZ());
        entity.setUuid(UUID.randomUUID());
        worldIn.spawnEntity(entity);
    }

    public static boolean shouldCancelEntityDamage(DamageSource source, LivingEntity livingEntity) {
        //The only time we want to cancel damage is when preventing a sunlight apocalypse
        return source.isFire()
            && !MobRebirth.config.damageFromSunlight
            && livingEntity.isUndead()
            && !livingEntity.isInLava()
            && livingEntity.world.isSkyVisible(new BlockPos(MathHelper.floor(livingEntity.getBlockPos().getX()), MathHelper.floor(livingEntity.getBlockPos().getY()), MathHelper.floor(livingEntity.getBlockPos().getZ())));
    }

    public static boolean isVanilla(LivingEntity entity) {
        return Objects.requireNonNull(entity.getType().getLootTableId()).getNamespace().toLowerCase().equals("minecraft");
    }
}

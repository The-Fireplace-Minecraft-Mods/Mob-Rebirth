package the_fireplace.mobrebirth.fabric;

import net.minecraft.entity.Entity;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class Events {
    public static void register() {

    }

    public static void onEntityLivingDeath(LivingEntity entityLiving, DamageSource damageSource) {
        if(!entityLiving.getEntityWorld().isClient())
            if(Config.rebirthFromNonPlayer)
                transition(entityLiving);
            else if(damageSource.getAttacker() instanceof PlayerEntity)
                transition(entityLiving);
    }

    private static void transition(LivingEntity entityLiving) {
        if (entityLiving instanceof Monster)
            makeMobRebornTransition(entityLiving);
        else if (Config.allowAnimals && entityLiving instanceof AnimalEntity)
            makeMobRebornTransition(entityLiving);
    }

    private static void makeMobRebornTransition(LivingEntity entityLiving) {
        /*if(!MobRebirth.clansCompat.doRebirth(event.getEntityLiving().getEntityWorld().getChunk(event.getEntityLiving().getPosition())))
            return;*/
        if (Config.allowBosses) {
            if (entityLiving instanceof WitherEntity || entityLiving instanceof EnderDragonEntity || entityLiving instanceof ElderGuardianEntity) {
                makeMobReborn(entityLiving);
                return;
            }
        } else if (entityLiving instanceof WitherEntity || entityLiving instanceof EnderDragonEntity || entityLiving instanceof ElderGuardianEntity)
            return;
        if (Config.allowSlimes) {
            if (entityLiving instanceof SlimeEntity) {
                makeMobReborn(entityLiving);
                return;
            }
        } else if (entityLiving instanceof SlimeEntity)
            return;
        if (Config.vanillaMobsOnly) {
            if (isVanilla(entityLiving))
                makeMobReborn(entityLiving);
        } else
            makeMobReborn(entityLiving);
    }

    private static void makeMobReborn(LivingEntity entityLiving) {
        double rand = Math.random();
        if (rand <= Config.rebirthChance) {
            if (Config.rebornAsEggs && MobRebirth.spawnEggs.containsKey(entityLiving.getType())) {
                dropMobEgg(entityLiving.getType(), entityLiving);
            } else {
                createEntity(entityLiving);
                if (Config.multiMobCount > 0) {
                    double rand2 = Math.random();
                    switch(Config.multiMobMode.toLowerCase()) {
                        case "all":
                            if (rand2 <= Config.multiMobChance)
                                for (int i = 0; i < Config.multiMobCount; i++)
                                    createEntity(entityLiving);
                            break;
                        case "per-mob":
                            for (int i = 0; i < Config.multiMobCount; i++, rand2 = new Random().nextDouble())
                                if (rand2 <= Config.multiMobChance)
                                    createEntity(entityLiving);
                            break;
                        case "continuous":
                        default:
                            for (int i = 0; i < Config.multiMobCount; i++, rand2 = new Random().nextDouble())
                                if (rand2 <= Config.multiMobChance)
                                    createEntity(entityLiving);
                                else
                                    break;
                    }
                }
            }
        }
    }

    private static static void dropMobEgg(EntityType<?> entityType, LivingEntity entityLiving) {
        entityLiving.dropItem(() -> MobRebirth.spawnEggs.get(entityType), 0);
    }

    private static void createEntity(LivingEntity entityLiving) {
        //Store
        LivingEntity entity;
        World worldIn = entityLiving.world;
        Identifier sid = EntityType.getId(entityLiving.getType());
        CompoundTag storedData = new CompoundTag();
        entityLiving.writeCustomDataToTag(storedData);
        ItemStack weapon = entityLiving.getStackInHand(Hand.MAIN_HAND);
        ItemStack offhand = entityLiving.getStackInHand(Hand.OFF_HAND);
        float health = entityLiving.getHealthMaximum();
        //Read
        entity = (LivingEntity) entityLiving.getType().create(worldIn);
        if (entity == null)
            return;
        entity.headYaw = entity.yaw;
        //entity.renderYawOffset = entity.yaw;
        storedData.putInt("Health", (int) health);
        entity.readCustomDataFromTag(storedData);
        entity.setHealth(health);
        if (!weapon.isEmpty())
            entity.setEquippedStack(EquipmentSlot.MAINHAND, weapon);
        if (!offhand.isEmpty())
            entity.setEquippedStack(EquipmentSlot.OFFHAND, offhand);
        entity.setPosition(entityLiving.x, entityLiving.y, entityLiving.z);
        entity.setUuid(UUID.randomUUID());
        worldIn.spawnEntity(entity);
    }

    public static boolean shouldCancelEntityDamage(DamageSource source, LivingEntity livingEntity) {
        if (source.isFire() && !Config.damageFromSunlight && livingEntity.isUndead() && !livingEntity.isInLava() && livingEntity.world.isSkyVisible(new BlockPos(MathHelper.floor(livingEntity.x), MathHelper.floor(livingEntity.y), MathHelper.floor(livingEntity.z))))
            return true;
        return false;
    }

    public static boolean isVanilla(LivingEntity entity) {
        return Objects.requireNonNull(entity.getType().getLootTableId()).getNamespace().toLowerCase().matches("minecraft");
    }
}

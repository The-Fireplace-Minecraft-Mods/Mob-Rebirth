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
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import the_fireplace.mobrebirth.config.MobSettingsManager;

import java.util.Random;
import java.util.UUID;

public class RebirthLogic {

    public static void onDeath(LivingEntity livingEntity, DamageSource damageSource) {
        if(!livingEntity.getEntityWorld().isClient()) {
            Boolean enabled = MobSettingsManager.getSettings(livingEntity).enabled;
            if(enabled == Boolean.FALSE)
                return;
            if(checkDamageSource(livingEntity, damageSource)
            && (enabled == Boolean.TRUE || (checkGeneralEntityType(livingEntity)
            && checkSpecificEntityType(livingEntity))))
                triggerRebirth(livingEntity, getRebirthCount(livingEntity));
        }
    }

    private static boolean checkDamageSource(LivingEntity livingEntity, DamageSource damageSource) {
        if(damageSource.getAttacker() instanceof PlayerEntity)
            return MobSettingsManager.getSettings(livingEntity).rebirthFromPlayer;
        else
            return MobSettingsManager.getSettings(livingEntity).rebirthFromNonPlayer;
    }

    private static boolean checkGeneralEntityType(LivingEntity livingEntity) {
        return livingEntity instanceof Monster || (MobRebirth.config.allowAnimals && livingEntity instanceof AnimalEntity);
    }

    private static boolean checkSpecificEntityType(LivingEntity livingEntity) {
        if (livingEntity instanceof WitherEntity || livingEntity instanceof EnderDragonEntity || livingEntity instanceof ElderGuardianEntity)
            return MobRebirth.config.allowBosses;
        if(livingEntity instanceof SlimeEntity)
            return MobRebirth.config.allowSlimes;
        return !MobRebirth.config.vanillaMobsOnly || isVanilla(livingEntity);
    }

    private static int getRebirthCount(LivingEntity livingEntity) {
        double rand = Math.random();
        int count=0;
        if (rand <= MobSettingsManager.getSettings(livingEntity).rebirthChance) {
            count++;
            if (MobSettingsManager.getSettings(livingEntity).multiMobCount > 0) {
                double rand2 = Math.random();
                switch(MobSettingsManager.getSettings(livingEntity).multiMobMode.toLowerCase()) {
                    case "all":
                        if (rand2 <= MobSettingsManager.getSettings(livingEntity).multiMobChance)
                            for (int i = 0; i < MobSettingsManager.getSettings(livingEntity).multiMobCount; i++)
                                count++;
                        break;
                    case "per-mob":
                        for (int i = 0; i < MobSettingsManager.getSettings(livingEntity).multiMobCount; i++, rand2 = new Random().nextDouble())
                            if (rand2 <= MobSettingsManager.getSettings(livingEntity).multiMobChance)
                                count++;
                        break;
                    case "continuous":
                    default:
                        for (int i = 0; i < MobSettingsManager.getSettings(livingEntity).multiMobCount; i++, rand2 = new Random().nextDouble())
                            if (rand2 <= MobSettingsManager.getSettings(livingEntity).multiMobChance)
                                count++;
                            else
                                break;
                }
            }
        }
        return count;
    }

    private static void triggerRebirth(LivingEntity livingEntity, int count) {
        for(int i=0;i<count;i++) {
            if (MobSettingsManager.getSettings(livingEntity).rebornAsEggs) {
                if(MobRebirth.spawnEggs.containsKey(livingEntity.getType())) {
                    dropMobEgg(livingEntity.getType(), livingEntity);
                } else {
                    //TODO log error about missing egg
                }
            } else {
                createEntity(livingEntity);
            }
        }
    }

    private static void dropMobEgg(EntityType<?> entityType, LivingEntity livingEntity) {
        livingEntity.dropItem(() -> MobRebirth.spawnEggs.get(entityType), 0);
    }

    private static void createEntity(LivingEntity livingEntity) {
        //Store
        LivingEntity newEntity;
        World worldIn = livingEntity.world;
        CompoundTag storedData = new CompoundTag();
        livingEntity.writeCustomDataToTag(storedData);
        ItemStack weapon = livingEntity.getStackInHand(Hand.MAIN_HAND);
        ItemStack offhand = livingEntity.getStackInHand(Hand.OFF_HAND);
        float health = livingEntity.getMaxHealth();
        //Read
        newEntity = (LivingEntity) livingEntity.getType().create(worldIn);
        if (newEntity == null)
            return;
        newEntity.headYaw = newEntity.yaw;
        newEntity.bodyYaw = newEntity.yaw;
        storedData.putInt("Health", (int) health);
        newEntity.readCustomDataFromTag(storedData);
        newEntity.setHealth(health);
        if (!weapon.isEmpty())
            newEntity.equipStack(EquipmentSlot.MAINHAND, weapon);
        if (!offhand.isEmpty())
            newEntity.equipStack(EquipmentSlot.OFFHAND, offhand);
        newEntity.updatePosition(livingEntity.getBlockPos().getX(), livingEntity.getBlockPos().getY(), livingEntity.getBlockPos().getZ());
        newEntity.setUuid(UUID.randomUUID());
        worldIn.spawnEntity(newEntity);
    }

    public static boolean shouldCancelEntityDamage(DamageSource source, LivingEntity livingEntity) {
        //The only time we want to cancel damage is when preventing a sunlight apocalypse
        return source.isFire()
            && !MobSettingsManager.getSettings(livingEntity).damageFromSunlight
            && livingEntity.isUndead()
            && !livingEntity.isInLava()
            && livingEntity.world.isSkyVisible(new BlockPos(MathHelper.floor(livingEntity.getBlockPos().getX()), MathHelper.floor(livingEntity.getBlockPos().getY()), MathHelper.floor(livingEntity.getBlockPos().getZ())));
    }

    public static boolean isVanilla(LivingEntity livingEntity) {
        return Registry.ENTITY_TYPE.getId(livingEntity.getType()).getNamespace().toLowerCase().equals("minecraft");
    }
}

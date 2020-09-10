package the_fireplace.mobrebirth;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import the_fireplace.mobrebirth.config.MobSettingsManager;

import java.util.*;

public class RebirthLogic {

    public static void onDeath(LivingEntity livingEntity, DamageSource damageSource) {
        if(!livingEntity.getEntityWorld().isClient()) {
            Boolean enabled = MobSettingsManager.getSettings(livingEntity).enabled;
            if(enabled == Boolean.FALSE)
                return;
            if(checkDamageSource(livingEntity, damageSource)
            && (enabled == Boolean.TRUE || (checkGeneralEntityType(livingEntity)
            && checkSpecificEntityType(livingEntity)))
            && checkBiome(livingEntity))
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

    private static boolean checkBiome(LivingEntity livingEntity) {
        List<String> biomeList = MobSettingsManager.getSettings(livingEntity).biomeList;
        boolean goodBiome = biomeList.contains("*");
        if(biomeList.contains(BuiltinRegistries.BIOME.getId(livingEntity.getEntityWorld().getBiomeAccess().getBiome(livingEntity.getBlockPos())).toString().toLowerCase()))
            goodBiome = !goodBiome;
        return goodBiome;
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
                            count += MobSettingsManager.getSettings(livingEntity).multiMobCount;
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

    public static EntityType<?> getTypeFromPool(LivingEntity livingEntity) {
        Map<String, Integer> rebornMobTypes = Maps.newHashMap(MobSettingsManager.getSettings(livingEntity).rebornMobWeights);
        if(rebornMobTypes.isEmpty() || (rebornMobTypes.size() == 1 && rebornMobTypes.containsKey("")))
            return livingEntity.getType();
        if(rebornMobTypes.containsKey("")) {
            int weight = rebornMobTypes.remove("");
            rebornMobTypes.put(Registry.ENTITY_TYPE.getId(livingEntity.getType()).toString(), weight);
        }
        if(rebornMobTypes.size() == 1)
            return Registry.ENTITY_TYPE.get(new Identifier((String) rebornMobTypes.keySet().toArray()[0]));
        int total = rebornMobTypes.values().stream().mapToInt(Integer::valueOf).sum();
        int selected = livingEntity.getRandom().nextInt(total)+1;
        List<Map.Entry<String, Integer>> entries = Lists.newArrayList(rebornMobTypes.entrySet());
        Collections.shuffle(entries);
        for(Map.Entry<String, Integer> entry: entries) {
            selected -= entry.getValue();
            if(selected <= 0)
                return Registry.ENTITY_TYPE.get(new Identifier(entry.getKey()));
        }
        throw new IllegalStateException("Ran out of entries in the weighted list.");
    }

    private static void triggerRebirth(LivingEntity livingEntity, int count) {
        for(int i=0;i<count;i++) {
            EntityType<?> type = getTypeFromPool(livingEntity);
            if (MobSettingsManager.getSettings(livingEntity).rebornAsEggs) {
                if(MobRebirth.spawnEggs.containsKey(livingEntity.getType())) {
                    dropMobEgg(type, livingEntity);
                } else {
                    MobRebirth.LOGGER.error("Missing egg for "+Registry.ENTITY_TYPE.getId(livingEntity.getType()).toString());
                }
            } else {
                createEntity(type, livingEntity);
            }
        }
    }

    private static void dropMobEgg(EntityType<?> entityType, LivingEntity livingEntity) {
        livingEntity.dropItem(() -> MobRebirth.spawnEggs.get(entityType), 0);
    }

    private static void createEntity(EntityType<?> entityType, LivingEntity livingEntity) {
        //Store
        LivingEntity newEntity;
        World worldIn = livingEntity.world;
        CompoundTag storedData = new CompoundTag();
        livingEntity.writeCustomDataToTag(storedData);
        ItemStack weapon = livingEntity.getStackInHand(Hand.MAIN_HAND);
        ItemStack offhand = livingEntity.getStackInHand(Hand.OFF_HAND);
        //Read
        newEntity = (LivingEntity) entityType.create(worldIn);
        if (newEntity == null)
            return;
        newEntity.headYaw = newEntity.yaw;
        newEntity.bodyYaw = newEntity.yaw;
        float health = newEntity.getMaxHealth();
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
            && MobSettingsManager.getSettings(livingEntity).preventSunlightDamage
            && livingEntity.isUndead()
            && !livingEntity.isInLava()
            && livingEntity.world.isSkyVisible(new BlockPos(MathHelper.floor(livingEntity.getBlockPos().getX()), MathHelper.floor(livingEntity.getBlockPos().getY()), MathHelper.floor(livingEntity.getBlockPos().getZ())));
    }

    public static boolean isVanilla(LivingEntity livingEntity) {
        return Registry.ENTITY_TYPE.getId(livingEntity.getType()).getNamespace().toLowerCase().equals("minecraft");
    }
}

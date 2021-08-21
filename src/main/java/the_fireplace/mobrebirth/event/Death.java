package the_fireplace.mobrebirth.event;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dev.the_fireplace.annotateddi.api.di.Implementation;
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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import the_fireplace.mobrebirth.MobRebirthConstants;
import the_fireplace.mobrebirth.config.MobSettingsManager;
import the_fireplace.mobrebirth.domain.config.ConfigValues;
import the_fireplace.mobrebirth.domain.event.DeathHandler;
import the_fireplace.mobrebirth.entrypoints.MainEntrypoint;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton
@Implementation
public final class Death implements DeathHandler {
    private final ConfigValues configValues;
    private final MobSettingsManager mobSettingsManager;

    @Inject
    public Death(ConfigValues configValues, MobSettingsManager mobSettingsManager) {
        this.configValues = configValues;
        this.mobSettingsManager = mobSettingsManager;
    }

    @Override
    public void onDeath(LivingEntity livingEntity, DamageSource damageSource) {
        if(!livingEntity.getEntityWorld().isClient()) {
            Boolean enabled = mobSettingsManager.getSettings(livingEntity).enabled;
            if(Boolean.FALSE.equals(enabled))
                return;
            if(checkDamageSource(livingEntity, damageSource)
                && (Boolean.TRUE.equals(enabled)
                || (checkGeneralEntityType(livingEntity)
                && checkSpecificEntityType(livingEntity)))
                && checkBiome(livingEntity))
                triggerRebirth(livingEntity, getRebirthCount(livingEntity));
        }
    }

    private boolean checkDamageSource(LivingEntity livingEntity, DamageSource damageSource) {
        if(damageSource.getAttacker() instanceof PlayerEntity)
            return mobSettingsManager.getSettings(livingEntity).rebirthFromPlayer;
        else
            return mobSettingsManager.getSettings(livingEntity).rebirthFromNonPlayer;
    }

    private boolean checkGeneralEntityType(LivingEntity livingEntity) {
        return livingEntity instanceof Monster || (configValues.getAllowAnimalRebirth() && livingEntity instanceof AnimalEntity);
    }

    private boolean checkSpecificEntityType(LivingEntity livingEntity) {
        if (livingEntity instanceof WitherEntity || livingEntity instanceof EnderDragonEntity || livingEntity instanceof ElderGuardianEntity)
            return configValues.getAllowBossRebirth();
        if(livingEntity instanceof SlimeEntity)
            return configValues.getAllowSlimeRebirth();
        return !configValues.getVanillaRebirthOnly() || isVanilla(livingEntity);
    }

    private boolean checkBiome(LivingEntity livingEntity) {
        List<String> biomeList = mobSettingsManager.getSettings(livingEntity).biomeList;
        boolean goodBiome = biomeList.contains("*");
        Biome biome = livingEntity.getEntityWorld().getBiomeAccess().getBiome(livingEntity.getBlockPos());
        Identifier biomeId = livingEntity.getEntityWorld().getRegistryManager().get(Registry.BIOME_KEY).getId(biome);
        if (biomeId != null && biomeList.contains(biomeId.toString().toLowerCase())) {
            goodBiome = !goodBiome;
        }
        return goodBiome;
    }

    private int getRebirthCount(LivingEntity livingEntity) {
        double rand = Math.random();
        int count=0;
        if (rand <= mobSettingsManager.getSettings(livingEntity).rebirthChance) {
            count++;
            if (mobSettingsManager.getSettings(livingEntity).multiMobCount > 0) {
                double rand2 = Math.random();
                switch(mobSettingsManager.getSettings(livingEntity).multiMobMode.toLowerCase()) {
                    case "all":
                        if (rand2 <= mobSettingsManager.getSettings(livingEntity).multiMobChance)
                            count += mobSettingsManager.getSettings(livingEntity).multiMobCount;
                        break;
                    case "per-mob":
                        for (int i = 0; i < mobSettingsManager.getSettings(livingEntity).multiMobCount; i++, rand2 = new Random().nextDouble())
                            if (rand2 <= mobSettingsManager.getSettings(livingEntity).multiMobChance)
                                count++;
                        break;
                    case "continuous":
                    default:
                        for (int i = 0; i < mobSettingsManager.getSettings(livingEntity).multiMobCount; i++, rand2 = new Random().nextDouble())
                            if (rand2 <= mobSettingsManager.getSettings(livingEntity).multiMobChance)
                                count++;
                            else
                                break;
                }
            }
        }
        return count;
    }

    private EntityType<?> getTypeFromPool(LivingEntity livingEntity) {
        Map<String, Integer> rebornMobTypes = Maps.newHashMap(mobSettingsManager.getSettings(livingEntity).rebornMobWeights);
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

    private void triggerRebirth(LivingEntity livingEntity, int count) {
        for(int i=0;i<count;i++) {
            EntityType<?> type = getTypeFromPool(livingEntity);
            if (mobSettingsManager.getSettings(livingEntity).rebornAsEggs) {
                if(MainEntrypoint.spawnEggs.containsKey(livingEntity.getType()))
                    dropMobEgg(type, livingEntity);
                else
                    MobRebirthConstants.LOGGER.error("Missing egg for "+ Registry.ENTITY_TYPE.getId(livingEntity.getType()));
            } else
                createEntity(type, livingEntity);
        }
    }

    private void dropMobEgg(EntityType<?> entityType, LivingEntity livingEntity) {
        livingEntity.dropItem(() -> MainEntrypoint.spawnEggs.get(entityType), 0);
    }

    private void createEntity(EntityType<?> entityType, LivingEntity livingEntity) {
        //Store
        LivingEntity newEntity;
        World worldIn = livingEntity.world;
        NbtCompound storedData = new NbtCompound();
        livingEntity.writeCustomDataToNbt(storedData);
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
        newEntity.readCustomDataFromNbt(storedData);
        newEntity.setHealth(health);
        if (!weapon.isEmpty())
            newEntity.equipStack(EquipmentSlot.MAINHAND, weapon);
        if (!offhand.isEmpty())
            newEntity.equipStack(EquipmentSlot.OFFHAND, offhand);
        newEntity.updatePosition(livingEntity.getBlockPos().getX(), livingEntity.getBlockPos().getY(), livingEntity.getBlockPos().getZ());
        newEntity.setUuid(UUID.randomUUID());
        worldIn.spawnEntity(newEntity);
    }

    private boolean isVanilla(LivingEntity livingEntity) {
        return Registry.ENTITY_TYPE.getId(livingEntity.getType()).getNamespace().equalsIgnoreCase("minecraft");
    }
}

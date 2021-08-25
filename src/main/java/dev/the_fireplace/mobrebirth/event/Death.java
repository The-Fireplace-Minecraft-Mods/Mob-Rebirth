package dev.the_fireplace.mobrebirth.event;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.mobrebirth.MobRebirthConstants;
import dev.the_fireplace.mobrebirth.config.MobSettings;
import dev.the_fireplace.mobrebirth.config.MobSettingsManager;
import dev.the_fireplace.mobrebirth.domain.config.ConfigValues;
import dev.the_fireplace.mobrebirth.domain.event.DeathHandler;
import dev.the_fireplace.mobrebirth.entrypoints.MainEntrypoint;
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
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Implementation
public final class Death implements DeathHandler {
    private static final int KILLER_RABBIT_TYPE = 99;

    private final ConfigValues configValues;
    private final MobSettingsManager mobSettingsManager;

    private LivingEntity livingEntity;
    private MobSettings mobSettings;

    @Inject
    public Death(ConfigValues configValues, MobSettingsManager mobSettingsManager) {
        this.configValues = configValues;
        this.mobSettingsManager = mobSettingsManager;
    }

    @Override
    public void onDeath(LivingEntity livingEntity, DamageSource damageSource) {
        if (!livingEntity.getEntityWorld().isClient()) {
            this.livingEntity = livingEntity;
            this.mobSettings = mobSettingsManager.getSettings(livingEntity);
            Boolean enabled = this.mobSettings.enabled;
            if (Boolean.FALSE.equals(enabled)) {
                return;
            }
            if (rebirthIsAllowedFromSource(damageSource)
                && (Boolean.TRUE.equals(enabled)
                || (rebirthIsAllowedForEntityCategory()
                && rebirthIsAllowedForEntityType()))
                && rebirthIsAllowedInBiome()) {
                triggerRebirth(getMobCountToSpawn());
            }
        }
    }

    private boolean rebirthIsAllowedFromSource(DamageSource damageSource) {
        if (damageSource.getAttacker() instanceof PlayerEntity) {
            return this.mobSettings.rebirthFromPlayer;
        } else {
            return this.mobSettings.rebirthFromNonPlayer;
        }
    }

    private boolean rebirthIsAllowedForEntityCategory() {
        return livingEntity instanceof Monster
            || (configValues.getAllowAnimalRebirth() && livingEntity instanceof AnimalEntity);
    }

    private boolean rebirthIsAllowedForEntityType() {
        if (configValues.getVanillaRebirthOnly() && !isVanilla(livingEntity)) {
            return false;
        }

        if (isBoss()) {
            return configValues.getAllowBossRebirth();
        }

        if (isSlime()) {
            return configValues.getAllowSlimeRebirth();
        }

        return true;
    }

    private boolean isSlime() {
        //TODO event for custom slimes to be registered

        return livingEntity instanceof SlimeEntity;
    }

    private boolean isBoss() {
        if (livingEntity instanceof WitherEntity
            || livingEntity instanceof EnderDragonEntity
            || livingEntity instanceof ElderGuardianEntity
        ) {
            return true;
        }

        if (livingEntity instanceof RabbitEntity) {
            return ((RabbitEntity) livingEntity).getRabbitType() == KILLER_RABBIT_TYPE;
        }

        //TODO event for custom bosses to be registered

        return false;
    }

    private boolean rebirthIsAllowedInBiome() {
        List<String> biomeList = this.mobSettings.biomeList;
        boolean biomeIsAllowed = biomeList.contains("*");

        Identifier biomeId = getEntityBiomeId();
        if (biomeId != null && biomeList.contains(biomeId.toString().toLowerCase())) {
            biomeIsAllowed = !biomeIsAllowed;
        }

        return biomeIsAllowed;
    }

    @Nullable
    private Identifier getEntityBiomeId() {
        Biome biome = livingEntity.getEntityWorld().getBiomeAccess().getBiome(livingEntity.getBlockPos());

        return livingEntity.getEntityWorld().getRegistryManager().get(Registry.BIOME_KEY).getId(biome);
    }

    private int getMobCountToSpawn() {
        double rand = Math.random();
        int count = 0;
        if (rand <= this.mobSettings.rebirthChance) {
            count++;
            count += getExtraMobCount();
        }
        return count;
    }

    private int getExtraMobCount() {
        if (this.mobSettings.extraMobCount <= 0) {
            return 0;
        }

        double rand = Math.random();
        String extraMobMode = this.mobSettings.extraMobMode.toLowerCase();
        switch (extraMobMode) {
            case "all":
                if (rand <= this.mobSettings.extraMobChance) {
                    return this.mobSettings.extraMobCount;
                }
                return 0;
            case "per-mob":
            case "continuous":
            default:
                int extraCount = 0;
                for (int i = 0; i < this.mobSettings.extraMobCount; i++, rand = Math.random()) {
                    if (rand <= this.mobSettings.extraMobChance) {
                        extraCount++;
                    } else if (!extraMobMode.equals("per-mob")) {
                        break;
                    }
                }
                return extraCount;
        }
    }

    private void triggerRebirth(int count) {
        for (int i = 0; i < count; i++) {
            EntityType<?> rebornEntityType = getEntityTypeForRebirth();
            if (this.mobSettings.rebornAsEggs) {
                if (MainEntrypoint.spawnEggs.containsKey(rebornEntityType)) {
                    dropMobEgg(rebornEntityType);
                } else {
                    MobRebirthConstants.LOGGER.error("Missing egg for " + Registry.ENTITY_TYPE.getId(rebornEntityType));
                }
            } else {
                createEntity(rebornEntityType);
            }
        }
    }

    private EntityType<?> getEntityTypeForRebirth() {
        Map<String, Integer> rebornMobTypes = Maps.newHashMap(this.mobSettings.rebornMobWeights);
        if (rebornMobTypes.isEmpty() || (rebornMobTypes.size() == 1 && rebornMobTypes.containsKey(""))) {
            return livingEntity.getType();
        }
        if (rebornMobTypes.containsKey("")) {
            int weight = rebornMobTypes.remove("");
            rebornMobTypes.put(Registry.ENTITY_TYPE.getId(livingEntity.getType()).toString(), weight);
        }
        if (rebornMobTypes.size() == 1) {
            return Registry.ENTITY_TYPE.get(new Identifier((String) rebornMobTypes.keySet().toArray()[0]));
        }
        int total = rebornMobTypes.values().stream().mapToInt(Integer::valueOf).sum();
        int selected = livingEntity.getRandom().nextInt(total) + 1;
        List<Map.Entry<String, Integer>> entries = Lists.newArrayList(rebornMobTypes.entrySet());
        Collections.shuffle(entries);
        for (Map.Entry<String, Integer> entry : entries) {
            selected -= entry.getValue();
            if (selected <= 0) {
                return Registry.ENTITY_TYPE.get(new Identifier(entry.getKey()));
            }
        }
        throw new IllegalStateException("Ran out of entries in the weighted list.");
    }

    private void dropMobEgg(EntityType<?> entityType) {
        livingEntity.dropItem(() -> MainEntrypoint.spawnEggs.get(entityType), 0);
    }

    private void createEntity(EntityType<?> entityType) {
        //Store
        LivingEntity newEntity;
        World worldIn = livingEntity.world;
        NbtCompound storedData = new NbtCompound();
        livingEntity.writeCustomDataToNbt(storedData);
        ItemStack weapon = livingEntity.getStackInHand(Hand.MAIN_HAND);
        ItemStack offhand = livingEntity.getStackInHand(Hand.OFF_HAND);
        //Read
        newEntity = (LivingEntity) entityType.create(worldIn);
        if (newEntity == null) {
            return;
        }
        newEntity.setHeadYaw(newEntity.getYaw());
        newEntity.setBodyYaw(newEntity.getYaw());
        float health = newEntity.getMaxHealth();
        storedData.putInt("Health", (int) health);
        newEntity.readCustomDataFromNbt(storedData);
        newEntity.setHealth(health);
        if (!weapon.isEmpty()) {
            newEntity.equipStack(EquipmentSlot.MAINHAND, weapon);
        }
        if (!offhand.isEmpty()) {
            newEntity.equipStack(EquipmentSlot.OFFHAND, offhand);
        }
        newEntity.updatePosition(livingEntity.getBlockPos().getX(), livingEntity.getBlockPos().getY(), livingEntity.getBlockPos().getZ());
        newEntity.setUuid(UUID.randomUUID());
        worldIn.spawnEntity(newEntity);
    }

    private boolean isVanilla(LivingEntity livingEntity) {
        return Registry.ENTITY_TYPE.getId(livingEntity.getType()).getNamespace().equalsIgnoreCase("minecraft");
    }
}

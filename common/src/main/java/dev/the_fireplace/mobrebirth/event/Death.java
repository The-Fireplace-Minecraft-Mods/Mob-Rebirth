package dev.the_fireplace.mobrebirth.event;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.mobrebirth.MobRebirthConstants;
import dev.the_fireplace.mobrebirth.config.MobSettings;
import dev.the_fireplace.mobrebirth.config.MobSettingsManager;
import dev.the_fireplace.mobrebirth.domain.config.ConfigValues;
import dev.the_fireplace.mobrebirth.domain.event.DeathHandler;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

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
        if (!livingEntity.getCommandSenderWorld().isClientSide()) {
            this.livingEntity = livingEntity;
            this.mobSettings = mobSettingsManager.getSettings(getId(livingEntity));
            boolean enabled = this.mobSettings.isEnabled();
            if (!enabled) {
                return;
            }
            if (rebirthIsAllowedFromSource(damageSource)
                && rebirthIsAllowedInBiome()
            ) {
                triggerRebirth(getMobCountToSpawn());
            }
        }
    }

    private boolean rebirthIsAllowedFromSource(DamageSource damageSource) {
        if (damageSource.getEntity() instanceof Player) {
            return this.mobSettings.isRebirthFromPlayer();
        } else {
            return this.mobSettings.isRebirthFromNonPlayer();
        }
    }

    private boolean rebirthIsAllowedForEntityCategory() {
        return livingEntity instanceof Enemy
            || (configValues.getAllowAnimalRebirth() && livingEntity instanceof Animal);
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

        return livingEntity instanceof Slime;
    }

    private boolean isBoss() {
        if (livingEntity instanceof WitherBoss
            || livingEntity instanceof EnderDragon
            || livingEntity instanceof ElderGuardian
        ) {
            return true;
        }

        if (livingEntity instanceof Rabbit) {
            return ((Rabbit) livingEntity).getRabbitType() == KILLER_RABBIT_TYPE;
        }

        //TODO event for custom bosses to be registered

        return false;
    }

    private boolean rebirthIsAllowedInBiome() {
        List<String> biomeList = this.mobSettings.getBiomeList();
        boolean biomeIsAllowed = biomeList.contains("*");

        ResourceLocation biomeId = getEntityBiomeId();
        if (biomeId != null && biomeList.contains(biomeId.toString().toLowerCase())) {
            biomeIsAllowed = !biomeIsAllowed;
        }

        return biomeIsAllowed;
    }

    private ResourceLocation getEntityBiomeId() {
        Biome biome = livingEntity.getCommandSenderWorld().getBiomeManager().getBiome(livingEntity.blockPosition());

        return livingEntity.getCommandSenderWorld().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getKey(biome);
    }

    private int getMobCountToSpawn() {
        double rand = Math.random();
        int count = 0;
        if (rand <= this.mobSettings.getRebirthChance()) {
            count++;
            count += getExtraMobCount();
        }
        return count;
    }

    private int getExtraMobCount() {
        if (this.mobSettings.getExtraMobCount() <= 0) {
            return 0;
        }

        double rand = Math.random();
        String extraMobMode = this.mobSettings.getExtraMobMode().toLowerCase();
        switch (extraMobMode) {
            case "all":
                if (rand <= this.mobSettings.getExtraMobChance()) {
                    return this.mobSettings.getExtraMobCount();
                }
                return 0;
            case "per-mob":
            case "continuous":
            default:
                int extraCount = 0;
                for (int i = 0; i < this.mobSettings.getExtraMobCount(); i++, rand = Math.random()) {
                    if (rand <= this.mobSettings.getExtraMobChance()) {
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
            if (this.mobSettings.isRebornAsEggs()) {
                if (MobRebirthConstants.getSpawnEggs().containsKey(rebornEntityType)) {
                    dropMobEgg(rebornEntityType);
                } else {
                    MobRebirthConstants.LOGGER.error("Missing egg for " + Registry.ENTITY_TYPE.getKey(rebornEntityType));
                }
            } else {
                createEntity(rebornEntityType);
            }
        }
    }

    private EntityType<?> getEntityTypeForRebirth() {
        Map<String, Integer> rebornMobTypes = Maps.newHashMap(this.mobSettings.getRebornMobWeights());
        if (rebornMobTypes.isEmpty() || (rebornMobTypes.size() == 1 && rebornMobTypes.containsKey(""))) {
            return livingEntity.getType();
        }
        if (rebornMobTypes.containsKey("")) {
            int weight = rebornMobTypes.remove("");
            rebornMobTypes.put(Registry.ENTITY_TYPE.getKey(livingEntity.getType()).toString(), weight);
        }
        if (rebornMobTypes.size() == 1) {
            return Registry.ENTITY_TYPE.get(new ResourceLocation((String) rebornMobTypes.keySet().toArray()[0]));
        }
        int total = rebornMobTypes.values().stream().mapToInt(Integer::valueOf).sum();
        int selected = livingEntity.getRandom().nextInt(total) + 1;
        List<Map.Entry<String, Integer>> entries = Lists.newArrayList(rebornMobTypes.entrySet());
        Collections.shuffle(entries);
        for (Map.Entry<String, Integer> entry : entries) {
            selected -= entry.getValue();
            if (selected <= 0) {
                return Registry.ENTITY_TYPE.get(new ResourceLocation(entry.getKey()));
            }
        }
        throw new IllegalStateException("Ran out of entries in the weighted list.");
    }

    private void dropMobEgg(EntityType<?> entityType) {
        livingEntity.spawnAtLocation(() -> MobRebirthConstants.getSpawnEggs().get(entityType), 0);
    }

    private void createEntity(EntityType<?> entityType) {
        //Store
        LivingEntity newEntity;
        Level worldIn = livingEntity.level;
        CompoundTag storedData = new CompoundTag();
        livingEntity.addAdditionalSaveData(storedData);
        ItemStack weapon = livingEntity.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack offhand = livingEntity.getItemInHand(InteractionHand.OFF_HAND);
        //Read
        newEntity = (LivingEntity) entityType.create(worldIn);
        if (newEntity == null) {
            return;
        }
        newEntity.setYHeadRot(newEntity.getYRot());
        newEntity.setYBodyRot(newEntity.getYRot());
        float health = newEntity.getMaxHealth();
        storedData.putInt("Health", (int) health);
        newEntity.readAdditionalSaveData(storedData);
        newEntity.setHealth(health);
        if (!weapon.isEmpty()) {
            newEntity.setItemSlot(EquipmentSlot.MAINHAND, weapon);
        }
        if (!offhand.isEmpty()) {
            newEntity.setItemSlot(EquipmentSlot.OFFHAND, offhand);
        }
        newEntity.absMoveTo(livingEntity.blockPosition().getX(), livingEntity.blockPosition().getY(), livingEntity.blockPosition().getZ());
        newEntity.setUUID(UUID.randomUUID());
        worldIn.addFreshEntity(newEntity);
    }

    private boolean isVanilla(LivingEntity livingEntity) {
        return getId(livingEntity).getNamespace().equalsIgnoreCase("minecraft");
    }

    private ResourceLocation getId(Entity entity) {
        return Registry.ENTITY_TYPE.getKey(entity.getType());
    }
}

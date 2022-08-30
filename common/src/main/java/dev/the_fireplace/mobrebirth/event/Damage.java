package dev.the_fireplace.mobrebirth.event;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.mobrebirth.config.MobSettingsManager;
import dev.the_fireplace.mobrebirth.domain.event.DamageHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Implementation
public final class Damage implements DamageHandler {

    private final MobSettingsManager mobSettingsManager;

    @Inject
    public Damage(MobSettingsManager mobSettingsManager) {
        this.mobSettingsManager = mobSettingsManager;
    }

    public boolean shouldCancelEntityDamage(DamageSource source, LivingEntity livingEntity) {
        return isSunlightDamage(source, livingEntity)
            && mobSettingsManager.getSettings(getId(livingEntity)).isPreventSunlightDamage();
    }

    private ResourceLocation getId(Entity entity) {
        return Registry.ENTITY_TYPE.getKey(entity.getType());
    }

    private boolean isSunlightDamage(DamageSource source, LivingEntity livingEntity) {
        return source.isFire()
            && livingEntity.isInvertedHealAndHarm()
            && !livingEntity.isInLava()
            && livingEntity.level.canSeeSky(floorBlockPos(livingEntity.blockPosition()));
    }

    private BlockPos floorBlockPos(BlockPos blockPos) {
        return new BlockPos(Mth.floor(blockPos.getX()), Mth.floor(blockPos.getY()), Mth.floor(blockPos.getZ()));
    }
}

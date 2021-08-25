package dev.the_fireplace.mobrebirth.event;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.mobrebirth.config.MobSettingsManager;
import dev.the_fireplace.mobrebirth.domain.event.DamageHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

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
            && mobSettingsManager.getSettings(livingEntity).preventSunlightDamage;
    }

    private boolean isSunlightDamage(DamageSource source, LivingEntity livingEntity) {
        return source.isFire()
            && livingEntity.isUndead()
            && !livingEntity.isInLava()
            && livingEntity.world.isSkyVisible(floorBlockPos(livingEntity.getBlockPos()));
    }

    private BlockPos floorBlockPos(BlockPos blockPos) {
        return new BlockPos(MathHelper.floor(blockPos.getX()), MathHelper.floor(blockPos.getY()), MathHelper.floor(blockPos.getZ()));
    }
}

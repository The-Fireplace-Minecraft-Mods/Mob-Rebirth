package dev.the_fireplace.mobrebirth.domain.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public interface DeathHandler {
    void onDeath(LivingEntity livingEntity, DamageSource damageSource);
}

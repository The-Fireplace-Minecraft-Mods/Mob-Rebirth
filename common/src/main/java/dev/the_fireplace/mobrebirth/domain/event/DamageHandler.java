package dev.the_fireplace.mobrebirth.domain.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public interface DamageHandler {
    boolean shouldCancelEntityDamage(DamageSource source, LivingEntity livingEntity);
}

package dev.the_fireplace.mobrebirth.domain.event;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

public interface DamageHandler {
    boolean shouldCancelEntityDamage(DamageSource source, LivingEntity livingEntity);
}

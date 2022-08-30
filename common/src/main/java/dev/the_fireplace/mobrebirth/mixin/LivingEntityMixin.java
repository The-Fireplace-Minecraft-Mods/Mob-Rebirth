package dev.the_fireplace.mobrebirth.mixin;

import dev.the_fireplace.mobrebirth.MobRebirthConstants;
import dev.the_fireplace.mobrebirth.domain.event.DamageHandler;
import dev.the_fireplace.mobrebirth.domain.event.DeathHandler;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings({"ConstantConditions", "unused"})
@Mixin(LivingEntity.class)
public final class LivingEntityMixin {
    @Inject(at = @At("HEAD"), method = "die")
    public void onDeath(DamageSource damageSource, CallbackInfo info) {
        MobRebirthConstants.getInjector().getInstance(DeathHandler.class).onDeath((LivingEntity) (Object) this, damageSource);
    }

    @Inject(at = @At("HEAD"), method = "hurt", cancellable = true)
    public void onDamage(DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> info) {
        if (MobRebirthConstants.getInjector().getInstance(DamageHandler.class).shouldCancelEntityDamage(damageSource, (LivingEntity) (Object) this)) {
            info.setReturnValue(false);
        }
    }
}

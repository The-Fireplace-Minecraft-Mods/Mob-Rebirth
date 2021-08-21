package the_fireplace.mobrebirth.mixin;

import dev.the_fireplace.annotateddi.api.DIContainer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import the_fireplace.mobrebirth.domain.event.DamageHandler;
import the_fireplace.mobrebirth.domain.event.DeathHandler;

@Mixin(LivingEntity.class)
public final class LivingEntityMixin {
	@Inject(at = @At("HEAD"), method = "onDeath(Lnet/minecraft/entity/damage/DamageSource;)V")
	public void onDeath(DamageSource damageSource, CallbackInfo info) {
		DIContainer.get().getInstance(DeathHandler.class).onDeath((LivingEntity)(Object)this, damageSource);
	}

	@Inject(at = @At("HEAD"), method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", cancellable = true)
	public void onDamage(DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> info) {
		if (DIContainer.get().getInstance(DamageHandler.class).shouldCancelEntityDamage(damageSource, (LivingEntity)(Object)this)) {
			info.setReturnValue(false);
		}
	}
}

package the_fireplace.mobrebirth.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import the_fireplace.mobrebirth.EventLogic;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	@Inject(at = @At("HEAD"), method = "onDeath(Lnet/minecraft/entity/damage/DamageSource;)V")
	public void onDeath(DamageSource damageSource, CallbackInfo info) {
		EventLogic.onDeath((LivingEntity)(Object)this, damageSource);
	}

	@Inject(at = @At("HEAD"), method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", cancellable = true)
	public void damage(DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> info) {
		if(EventLogic.shouldCancelEntityDamage(damageSource, (LivingEntity)(Object)this))
			info.setReturnValue(false);
	}
}

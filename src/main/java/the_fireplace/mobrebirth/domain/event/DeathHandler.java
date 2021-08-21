package the_fireplace.mobrebirth.domain.event;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

public interface DeathHandler {
    void onDeath(LivingEntity livingEntity, DamageSource damageSource);
}

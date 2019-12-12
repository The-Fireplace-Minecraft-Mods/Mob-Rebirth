package the_fireplace.mobrebirth.fabric;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class Events {
    public static void register() {

    }

    private class LivingDeathEvent {
        LivingEntity dyingEntity;
        DamageSource source;
        public LivingDeathEvent(LivingEntity dyingEntity, DamageSource source) {
            this.dyingEntity = dyingEntity;
            this.source = source;
        }
        public Entity getEntity() {
            return dyingEntity;
        }

        public LivingEntity getEntityLiving() {
            return dyingEntity;
        }

        public DamageSource getSource() {
            return source;
        }
    }

    public void onEntityLivingDeath(LivingDeathEvent event) {
        if(!event.getEntity().getEntityWorld().isClient())
            if(Config.rebirthFromNonPlayer)
                transition(event);
            else if(event.getSource().getAttacker() instanceof PlayerEntity)
                transition(event);
    }

    private void transition(LivingDeathEvent event) {
        if (event.getEntityLiving() instanceof MobEntity)
            makeMobRebornTransition(event);
        else if (Config.allowAnimals && event.getEntityLiving() instanceof AnimalEntity)
            makeMobRebornTransition(event);
    }

    private void makeMobRebornTransition(LivingDeathEvent event) {
        /*if(!MobRebirth.clansCompat.doRebirth(event.getEntityLiving().getEntityWorld().getChunk(event.getEntityLiving().getPosition())))
            return;*/
        if (Config.allowBosses) {
            if (event.getEntityLiving() instanceof WitherEntity || event.getEntityLiving() instanceof EnderDragonEntity || event.getEntityLiving() instanceof ElderGuardianEntity) {
                makeMobReborn(event);
                return;
            }
        } else if (event.getEntityLiving() instanceof WitherEntity || event.getEntityLiving() instanceof EnderDragonEntity || event.getEntityLiving() instanceof ElderGuardianEntity)
            return;
        if (Config.allowSlimes) {
            if (event.getEntityLiving() instanceof SlimeEntity) {
                makeMobReborn(event);
                return;
            }
        } else if (event.getEntityLiving() instanceof SlimeEntity)
            return;
        if (Config.vanillaMobsOnly) {
            if (isVanilla(event.getEntityLiving()))
                makeMobReborn(event);
        } else
            makeMobReborn(event);
    }

    private void makeMobReborn(LivingDeathEvent event) {
        double rand = Math.random();
        if (rand <= Config.rebirthChance) {
            if (Config.rebornAsEggs && MobRebirth.spawnEggs.containsKey(event.getEntityLiving().getType())) {
                dropMobEgg(event, event.getEntityLiving().getType());
            } else {
                createEntity(event);
                if (Config.multiMobCount > 0) {
                    double rand2 = Math.random();
                    switch(Config.multiMobMode.toLowerCase()) {
                        case "all":
                            if (rand2 <= Config.multiMobChance)
                                for (int i = 0; i < Config.multiMobCount; i++)
                                    createEntity(event);
                            break;
                        case "per-mob":
                            for (int i = 0; i < Config.multiMobCount; i++, rand2 = new Random().nextDouble())
                                if (rand2 <= Config.multiMobChance)
                                    createEntity(event);
                            break;
                        case "continuous":
                        default:
                            for (int i = 0; i < Config.multiMobCount; i++, rand2 = new Random().nextDouble())
                                if (rand2 <= Config.multiMobChance)
                                    createEntity(event);
                                else
                                    break;
                    }
                }
            }
        }
    }

    private static void dropMobEgg(LivingDeathEvent event, EntityType<?> entityType) {
        event.getEntityLiving().dropItem(() -> MobRebirth.spawnEggs.get(entityType), 0);
    }

    private void createEntity(LivingDeathEvent event) {
        //Store
        LivingEntity entity;
        World worldIn = event.getEntityLiving().world;
        Identifier sid = ForgeRegistries.ENTITIES.getKey(event.getEntityLiving().getType());
        CompoundTag storedData = new CompoundTag();
        event.getEntityLiving().writeCustomDataToTag(storedData);
        ItemStack weapon = event.getEntityLiving().getStackInHand(Hand.MAIN_HAND);
        ItemStack offhand = event.getEntityLiving().getStackInHand(Hand.OFF_HAND);
        float health = event.getEntityLiving().getHealthMaximum();
        //Read
        entity = (LivingEntity) Objects.requireNonNull(ForgeRegistries.ENTITIES.getValue(sid)).create(worldIn);
        if (entity == null)
            return;
        entity.headYaw = entity.yaw;
        //entity.renderYawOffset = entity.yaw;
        storedData.putInt("Health", (int) health);
        entity.readCustomDataFromTag(storedData);
        entity.setHealth(health);
        if (!weapon.isEmpty())
            entity.setEquippedStack(EquipmentSlot.MAINHAND, weapon);
        if (!offhand.isEmpty())
            entity.setEquippedStack(EquipmentSlot.OFFHAND, offhand);
        entity.setPosition(event.getEntityLiving().x, event.getEntityLiving().y, event.getEntityLiving().z);
        entity.setUuid(UUID.randomUUID());
        worldIn.spawnEntity(entity);
    }

    public void entityDamaged(LivingHurtEvent event) {
        if (event.getSource().isFireDamage() && !Config.damageFromSunlight && event.getEntityLiving().isEntityUndead() && !event.getEntityLiving().isInLava() && event.getEntityLiving().world.canBlockSeeSky(new BlockPos(MathHelper.floor(event.getEntityLiving().posX), MathHelper.floor(event.getEntityLiving().posY), MathHelper.floor(event.getEntityLiving().posZ))))
            event.setCanceled(true);
    }

    public static boolean isVanilla(LivingEntity entity) {
        return Objects.requireNonNull(entity.getType().getLootTableId()).getNamespace().toLowerCase().matches("minecraft");
    }
}

package the_fireplace.mobrebirth.forge;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.monster.ElderGuardianEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;

/**
 * @author The_Fireplace
 */
public class CommonEvents {

	@SubscribeEvent
	public void onEntityLivingDeath(LivingDeathEvent event) {
		/*if (MobRebirth.getHasCustomMobSettings()) {
			if (ForgeRegistries.ENTITIES.getKey(event.getEntityLiving().getType()) != null)
				if (ArrayUtils.contains(ConfigValues.CUSTOMENTITIES, ForgeRegistries.ENTITIES.getKey(event.getEntityLiving().getType()).getPath())) {
					if (ConfigValues.REBIRTHFROMNONPLAYERMAP.get(ForgeRegistries.ENTITIES.getKey(event.getEntityLiving().getType())))
						transition(event);
					else if (event.getSource().getTrueSource() instanceof PlayerEntity)
						transition(event);
					return;
				}
		}*/
		if(!event.getEntity().getEntityWorld().isRemote())
            if(MobRebirth.cfg.rebirthFromNonPlayer)
                transition(event.getEntityLiving());
            else if(event.getSource().getTrueSource() instanceof PlayerEntity)
                transition(event.getEntityLiving());
	}

	private void transition(LivingEntity entityLiving) {
		if (entityLiving instanceof IMob)
			makeMobRebornTransition(entityLiving);
		else if (MobRebirth.cfg.allowAnimals && entityLiving instanceof AnimalEntity)
			makeMobRebornTransition(entityLiving);
	}

	private void makeMobRebornTransition(LivingEntity entityLiving) {
		if(!MobRebirth.clansCompat.doRebirth(entityLiving.getEntityWorld().getChunk(entityLiving.getPosition())))
			return;
		if (MobRebirth.cfg.allowBosses) {
			if (entityLiving instanceof WitherEntity || entityLiving instanceof EnderDragonEntity || entityLiving instanceof ElderGuardianEntity) {
				makeMobReborn(entityLiving);
				return;
			}
		} else if (entityLiving instanceof WitherEntity || entityLiving instanceof EnderDragonEntity || entityLiving instanceof ElderGuardianEntity)
			return;
		if (MobRebirth.cfg.allowSlimes) {
			if (entityLiving instanceof SlimeEntity) {
				makeMobReborn(entityLiving);
				return;
			}
		} else if (entityLiving instanceof SlimeEntity)
			return;
		if (MobRebirth.cfg.vanillaMobsOnly) {
			if (isVanilla(entityLiving))
				makeMobReborn(entityLiving);
		} else
			makeMobReborn(entityLiving);
	}

	private void makeMobReborn(LivingEntity entityLiving) {
		double rand = Math.random();
		/*ResourceLocation name = ForgeRegistries.ENTITIES.getKey(event.getEntityLiving().getType());
		if (MobRebirth.getHasCustomMobSettings()) {
			if (ArrayUtils.contains(ConfigValues.CUSTOMENTITIES, name.getPath())) {
				if (rand <= ConfigValues.REBIRTHCHANCEMAP.get(name)) {
					if (ConfigValues.DROPEGGMAP.get(name) && MobRebirth.spawnEggs.containsKey(event.getEntityLiving().getType())) {
						dropMobEgg(event, event.getEntityLiving().getType());
					} else {
						createEntity(event);
						if (ConfigValues.EXTRAMOBCOUNTMAP.get(name) > 0) {
							double rand2 = Math.random();
							if (ConfigValues.MULTIMOBMODE.toLowerCase().equals("all")) {
								if (rand2 <= ConfigValues.MULTIMOBCHANCEMAP.get(name)) {
									for (int i = 0; i < ConfigValues.EXTRAMOBCOUNTMAP.get(name); i++) {
										createEntity(event);
									}
								}
							} else if (ConfigValues.MULTIMOBMODE.toLowerCase().equals("per-mob")) {
								for (int i = 0; i < ConfigValues.EXTRAMOBCOUNTMAP.get(name); i++, rand2 = new Random().nextDouble()) {
									if (rand2 <= ConfigValues.MULTIMOBCHANCEMAP.get(name)) {
										createEntity(event);
									}
								}
							} else {
								for (int i = 0; i < ConfigValues.EXTRAMOBCOUNTMAP.get(name); i++, rand2 = new Random().nextDouble()) {
									if (rand2 <= ConfigValues.MULTIMOBCHANCEMAP.get(name)) {
										createEntity(event);
									} else {
										break;
									}
								}
							}
						}
					}
				}
				return;
			}
		}*/
		if (rand <= MobRebirth.cfg.rebirthChance) {
			if (MobRebirth.cfg.rebornAsEggs && MobRebirth.spawnEggs.containsKey(entityLiving.getType())) {
				dropMobEgg(entityLiving.getType(), entityLiving);
			} else {
				createEntity(entityLiving);
				if (MobRebirth.cfg.multiMobCount > 0) {
					double rand2 = Math.random();
					switch(MobRebirth.cfg.multiMobMode.toLowerCase()) {
						case "all":
							if (rand2 <= MobRebirth.cfg.multiMobChance)
								for (int i = 0; i < MobRebirth.cfg.multiMobCount; i++)
									createEntity(entityLiving);
							break;
						case "per-mob":
							for (int i = 0; i < MobRebirth.cfg.multiMobCount; i++, rand2 = new Random().nextDouble())
								if (rand2 <= MobRebirth.cfg.multiMobChance)
									createEntity(entityLiving);
							break;
						case "continuous":
						default:
							for (int i = 0; i < MobRebirth.cfg.multiMobCount; i++, rand2 = new Random().nextDouble())
								if (rand2 <= MobRebirth.cfg.multiMobChance)
									createEntity(entityLiving);
								else
									break;
					}
				}
			}
		}
	}

	private static void dropMobEgg(EntityType<?> entityType, LivingEntity entityLiving) {
		entityLiving.entityDropItem(new ItemStack(MobRebirth.spawnEggs.get(entityType)), 0.0F);
	}

	private void createEntity(LivingEntity entityLiving) {
		//Store
		World worldIn = entityLiving.world;
		ResourceLocation sid = ForgeRegistries.ENTITIES.getKey(entityLiving.getType());
		// entityLiving.getPersistentData is throwing StackOverflowException. It should only be Forge data though, so this can probably stay like this unless any issues arise.
		CompoundNBT storedData = new CompoundNBT();//entityLiving.getPersistentData();
		entityLiving.writeUnlessPassenger(storedData);
		ItemStack weapon = entityLiving.getHeldItem(Hand.MAIN_HAND);
		ItemStack offhand = entityLiving.getHeldItem(Hand.OFF_HAND);
		float health = entityLiving.getMaxHealth();
		//Read
		LivingEntity newEntity = (LivingEntity) Objects.requireNonNull(ForgeRegistries.ENTITIES.getValue(sid)).create(worldIn);
		if (newEntity == null)
			return;
		newEntity.rotationYawHead = newEntity.rotationYaw;
		newEntity.renderYawOffset = newEntity.rotationYaw;
		storedData.putInt("Health", (int) health);
		newEntity.read(storedData);
		newEntity.setHealth(health);
		if (!weapon.isEmpty())
			newEntity.setItemStackToSlot(EquipmentSlotType.MAINHAND, weapon);
		if (!offhand.isEmpty())
			newEntity.setItemStackToSlot(EquipmentSlotType.OFFHAND, offhand);
		newEntity.setUniqueId(UUID.randomUUID());
		newEntity.setPosition(entityLiving.getPosition().getX(), entityLiving.getPosition().getY(), entityLiving.getPosition().getZ());
		worldIn.addEntity(newEntity);
	}

	@SubscribeEvent
	public void entityDamaged(LivingHurtEvent event) {
		if (event.getSource().isFireDamage() && !MobRebirth.cfg.damageFromSunlight && event.getEntityLiving().isEntityUndead() && !event.getEntityLiving().isInLava() && event.getEntityLiving().world.canBlockSeeSky(new BlockPos(MathHelper.floor(event.getEntityLiving().getPosition().getX()), MathHelper.floor(event.getEntityLiving().getPosition().getY()), MathHelper.floor(event.getEntityLiving().getPosition().getZ()))))
			event.setCanceled(true);
	}

	public static boolean isVanilla(LivingEntity entity) {
		return ForgeRegistries.ENTITIES.getKey(entity.getType()) != null && Objects.requireNonNull(entity.getType().getRegistryName()).getNamespace().toLowerCase().matches("minecraft");
	}
}

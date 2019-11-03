package the_fireplace.mobrebirth;

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
                transition(event);
            else if(event.getSource().getTrueSource() instanceof PlayerEntity)
                transition(event);
	}

	private void transition(LivingDeathEvent event) {
		if (event.getEntityLiving() instanceof IMob)
			makeMobRebornTransition(event);
		else if (MobRebirth.cfg.allowAnimals && event.getEntityLiving() instanceof AnimalEntity)
			makeMobRebornTransition(event);
	}

	private void makeMobRebornTransition(LivingDeathEvent event) {
		if(!MobRebirth.clansCompat.doRebirth(event.getEntityLiving().getEntityWorld().getChunk(event.getEntityLiving().getPosition())))
			return;
		if (MobRebirth.cfg.allowBosses) {
			if (event.getEntityLiving() instanceof WitherEntity || event.getEntityLiving() instanceof EnderDragonEntity || event.getEntityLiving() instanceof ElderGuardianEntity) {
				makeMobReborn(event);
				return;
			}
		} else if (event.getEntityLiving() instanceof WitherEntity || event.getEntityLiving() instanceof EnderDragonEntity || event.getEntityLiving() instanceof ElderGuardianEntity)
			return;
		if (MobRebirth.cfg.allowSlimes) {
			if (event.getEntityLiving() instanceof SlimeEntity) {
				makeMobReborn(event);
				return;
			}
		} else if (event.getEntityLiving() instanceof SlimeEntity)
			return;
		if (MobRebirth.cfg.vanillaMobsOnly) {
			if (isVanilla(event.getEntityLiving()))
				makeMobReborn(event);
		} else
			makeMobReborn(event);
	}

	private void makeMobReborn(LivingDeathEvent event) {
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
			if (MobRebirth.cfg.rebornAsEggs && MobRebirth.spawnEggs.containsKey(event.getEntityLiving().getType())) {
				dropMobEgg(event, event.getEntityLiving().getType());
			} else {
				createEntity(event);
				if (MobRebirth.cfg.multiMobCount > 0) {
					double rand2 = Math.random();
					switch(MobRebirth.cfg.multiMobMode.toLowerCase()) {
						case "all":
							if (rand2 <= MobRebirth.cfg.multiMobChance)
								for (int i = 0; i < MobRebirth.cfg.multiMobCount; i++)
									createEntity(event);
							break;
						case "per-mob":
							for (int i = 0; i < MobRebirth.cfg.multiMobCount; i++, rand2 = new Random().nextDouble())
								if (rand2 <= MobRebirth.cfg.multiMobChance)
									createEntity(event);
							break;
						case "continuous":
						default:
							for (int i = 0; i < MobRebirth.cfg.multiMobCount; i++, rand2 = new Random().nextDouble())
								if (rand2 <= MobRebirth.cfg.multiMobChance)
									createEntity(event);
								else
									break;
					}
				}
			}
		}
	}

	private static void dropMobEgg(LivingDeathEvent event, EntityType<?> entityType) {
		event.getEntityLiving().entityDropItem(new ItemStack(MobRebirth.spawnEggs.get(entityType)), 0.0F);
	}

	private void createEntity(LivingDeathEvent event) {
		//Store
		LivingEntity entity;
		World worldIn = event.getEntityLiving().world;
		ResourceLocation sid = ForgeRegistries.ENTITIES.getKey(event.getEntityLiving().getType());
		CompoundNBT storedData = event.getEntityLiving().getPersistentData();
		event.getEntityLiving().writeUnlessPassenger(storedData);
		ItemStack weapon = event.getEntityLiving().getHeldItem(Hand.MAIN_HAND);
		ItemStack offhand = event.getEntityLiving().getHeldItem(Hand.OFF_HAND);
		float health = event.getEntityLiving().getMaxHealth();
		//Read
		entity = (LivingEntity) Objects.requireNonNull(ForgeRegistries.ENTITIES.getValue(sid)).create(worldIn);
		if (entity == null)
			return;
		entity.rotationYawHead = entity.rotationYaw;
		entity.renderYawOffset = entity.rotationYaw;
		storedData.putInt("Health", (int) health);
		entity.read(storedData);
		entity.setHealth(health);
		if (!weapon.isEmpty())
			entity.setItemStackToSlot(EquipmentSlotType.MAINHAND, weapon);
		if (!offhand.isEmpty())
			entity.setItemStackToSlot(EquipmentSlotType.OFFHAND, offhand);
		entity.setPosition(event.getEntityLiving().posX, event.getEntityLiving().posY, event.getEntityLiving().posZ);
		entity.setUniqueId(UUID.randomUUID());
		worldIn.addEntity(entity);
	}

	@SubscribeEvent
	public void entityDamaged(LivingHurtEvent event) {
		if (event.getSource().isFireDamage() && !MobRebirth.cfg.damageFromSunlight && event.getEntityLiving().isEntityUndead() && !event.getEntityLiving().isInLava() && event.getEntityLiving().world.canBlockSeeSky(new BlockPos(MathHelper.floor(event.getEntityLiving().posX), MathHelper.floor(event.getEntityLiving().posY), MathHelper.floor(event.getEntityLiving().posZ))))
			event.setCanceled(true);
	}

	public static boolean isVanilla(LivingEntity entity) {
		return ForgeRegistries.ENTITIES.getKey(entity.getType()) != null && Objects.requireNonNull(entity.getType().getRegistryName()).getNamespace().toLowerCase().matches("minecraft");
	}
}

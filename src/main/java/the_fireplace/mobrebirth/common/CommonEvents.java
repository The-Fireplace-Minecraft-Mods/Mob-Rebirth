package the_fireplace.mobrebirth.common;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.ArrayUtils;
import the_fireplace.mobrebirth.MobRebirth;

import java.util.Random;

/**
 * @author The_Fireplace
 */
public class CommonEvents {

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
		if (eventArgs.getModID().equals(MobRebirth.MODID)) {
			MobRebirth.syncConfig();
			MobRebirth.syncMobConfigs();
			MobRebirth.createMobConfigs();
			MobRebirth.syncMobConfigs();
		}
	}

	@SubscribeEvent
	public void onEntityLivingDeath(LivingDropsEvent event) {
		if (MobRebirth.instance.getHasCustomMobSettings()) {
			if (EntityList.getKey(event.getEntityLiving()) != null)
				if (ArrayUtils.contains(ConfigValues.CUSTOMENTITIES, EntityList.getKey(event.getEntityLiving()).getPath())) {
					if (ConfigValues.REBIRTHFROMNONPLAYERMAP.get(EntityList.getKey(event.getEntityLiving())))
						transition(event);
					else if (event.getSource().getTrueSource() instanceof EntityPlayer)
						transition(event);
					return;
				}
		}
		if (ConfigValues.REBIRTHFROMNONPLAYER)
			transition(event);
		else if (event.getSource().getTrueSource() instanceof EntityPlayer)
			transition(event);
	}

	private void transition(LivingDropsEvent event) {
		if (event.getEntityLiving() instanceof IMob)
			makeMobRebornTransition(event);
		else if (event.getEntityLiving() instanceof IAnimals && ConfigValues.ANIMALREBIRTH)
			makeMobRebornTransition(event);
	}

	private void makeMobRebornTransition(LivingDropsEvent event) {
		if (ConfigValues.ALLOWBOSSES) {
			if (event.getEntityLiving() instanceof EntityWither || event.getEntityLiving() instanceof EntityDragon) {
				makeMobReborn(event);
				return;
			}
		} else if (event.getEntityLiving() instanceof EntityWither || event.getEntityLiving() instanceof EntityDragon)
			return;
		if (ConfigValues.ALLOWSLIMES) {
			if (event.getEntityLiving() instanceof EntitySlime) {
				makeMobReborn(event);
				return;
			}
		} else if (event.getEntityLiving() instanceof EntitySlime)
			return;
		if (ConfigValues.VANILLAONLY) {
			if (isVanilla(event.getEntityLiving())) {
				makeMobReborn(event);
			}
		} else {
			makeMobReborn(event);
		}
	}

	private void makeMobReborn(LivingDropsEvent event) {
		double rand = Math.random();
		ResourceLocation name = EntityList.getKey(event.getEntityLiving());
		if (MobRebirth.instance.getHasCustomMobSettings()) {
			if (ArrayUtils.contains(ConfigValues.CUSTOMENTITIES, name.getPath())) {
				if (rand <= ConfigValues.REBIRTHCHANCEMAP.get(name)) {
					if (ConfigValues.DROPEGGMAP.get(name) && EntityList.ENTITY_EGGS.containsKey(name)) {
						dropMobEgg(event, name);
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
		}
		if (rand <= ConfigValues.REBIRTHCHANCE) {
			if (ConfigValues.DROPEGG && EntityList.ENTITY_EGGS.containsKey(name)) {
				dropMobEgg(event, name);
			} else {
				createEntity(event);
				if (ConfigValues.EXTRAMOBCOUNT > 0) {
					double rand2 = Math.random();
					if (ConfigValues.MULTIMOBMODE.toLowerCase().equals("all")) {
						if (rand2 <= ConfigValues.MULTIMOBCHANCE) {
							for (int i = 0; i < ConfigValues.EXTRAMOBCOUNT; i++) {
								createEntity(event);
							}
						}
					} else if (ConfigValues.MULTIMOBMODE.toLowerCase().equals("per-mob")) {
						for (int i = 0; i < ConfigValues.EXTRAMOBCOUNT; i++, rand2 = new Random().nextDouble()) {
							if (rand2 <= ConfigValues.MULTIMOBCHANCE) {
								createEntity(event);
							}
						}
					} else {
						for (int i = 0; i < ConfigValues.EXTRAMOBCOUNT; i++, rand2 = new Random().nextDouble()) {
							if (rand2 <= ConfigValues.MULTIMOBCHANCE) {
								createEntity(event);
							} else {
								break;
							}
						}
					}
				}
			}
		}
	}

	private static void dropMobEgg(LivingDropsEvent event, ResourceLocation name) {
		ItemStack dropEgg = new ItemStack(Items.SPAWN_EGG);
		NBTTagCompound eggData = new NBTTagCompound();
		NBTTagCompound mobData = new NBTTagCompound();
		mobData.setString("id", name.toString());
		eggData.setTag("EntityTag", mobData);
		dropEgg.setTagCompound(eggData);
		event.getEntityLiving().entityDropItem(dropEgg, 0.0F);
	}

	private void createEntity(LivingDropsEvent event) {
		//Store
		EntityLivingBase entity;
		World worldIn = event.getEntityLiving().world;
		ResourceLocation sid = EntityList.getKey(event.getEntityLiving());
		NBTTagCompound storedData = event.getEntityLiving().getEntityData();
		event.getEntityLiving().writeEntityToNBT(storedData);
		ItemStack weapon = event.getEntityLiving().getHeldItem(EnumHand.MAIN_HAND);
		ItemStack offhand = event.getEntityLiving().getHeldItem(EnumHand.OFF_HAND);
		float health = event.getEntityLiving().getMaxHealth();
		//Read
		entity = (EntityLivingBase) EntityList.createEntityByIDFromName(sid, worldIn);
		if (entity == null)
			return;
		entity.rotationYawHead = entity.rotationYaw;
		entity.renderYawOffset = entity.rotationYaw;
		storedData.setInteger("Health", (int) health);
		entity.readFromNBT(storedData);
		entity.setHealth(health);
		if (!weapon.isEmpty())
			entity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, weapon);
		if (!offhand.isEmpty())
			entity.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, offhand);
		entity.setPosition(event.getEntityLiving().posX, event.getEntityLiving().posY, event.getEntityLiving().posZ);
		worldIn.spawnEntity(entity);
	}

	@SubscribeEvent
	public void entityDamaged(LivingHurtEvent event) {
		if (event.getSource().isFireDamage() && !ConfigValues.DAMAGEFROMSUNLIGHT && event.getEntityLiving().isEntityUndead() && !event.getEntityLiving().isInLava() && event.getEntityLiving().world.canBlockSeeSky(new BlockPos(MathHelper.floor(event.getEntityLiving().posX), MathHelper.floor(event.getEntityLiving().posY), MathHelper.floor(event.getEntityLiving().posZ)))) {
			event.setCanceled(true);
		}
	}

	public static boolean isVanilla(EntityLivingBase entity) {
		return EntityList.getKey(entity) != null && EntityList.getKey(entity).getNamespace().matches("minecraft");
	}
}

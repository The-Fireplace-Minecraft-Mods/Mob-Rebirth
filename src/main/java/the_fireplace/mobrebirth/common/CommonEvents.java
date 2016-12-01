package the_fireplace.mobrebirth.common;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
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
		if(eventArgs.modID.equals(MobRebirth.MODID)) {
			MobRebirth.syncConfig();
			MobRebirth.syncMobConfigs();
			MobRebirth.createMobConfigs();
			MobRebirth.syncMobConfigs();
		}
	}

	@SubscribeEvent
	public void onEntityLivingDeath(LivingDropsEvent event) {
		if(MobRebirth.instance.getHasCustomMobSettings()) {
			if(ArrayUtils.contains(ConfigValues.CUSTOMENTITIES, EntityList.getEntityString(event.entityLiving).toLowerCase())){
				if (ConfigValues.REBIRTHFROMNONPLAYERMAP.get(EntityList.getEntityString(event.entityLiving).toLowerCase()))
					transition(event);
				else if (event.source.getEntity() instanceof EntityPlayer)
					transition(event);
				return;
			}
		}
		if (ConfigValues.REBIRTHFROMNONPLAYER)
			transition(event);
		else if (event.source.getEntity() instanceof EntityPlayer)
			transition(event);
	}

	private void transition(LivingDropsEvent event){
		if (event.entityLiving instanceof IMob)
			makeMobRebornTransition(event);
		else if (event.entityLiving instanceof IAnimals && ConfigValues.ANIMALREBIRTH)
			makeMobRebornTransition(event);
	}

	private void makeMobRebornTransition(LivingDropsEvent event){
		if(ConfigValues.ALLOWBOSSES){
			if(event.entityLiving instanceof EntityWither || event.entityLiving instanceof EntityDragon){
				makeMobReborn(event);
				return;
			}
		}else if(event.entityLiving instanceof EntityWither || event.entityLiving instanceof EntityDragon)
			return;
		if(ConfigValues.ALLOWSLIMES){
			if(event.entityLiving instanceof EntitySlime){
				makeMobReborn(event);
				return;
			}
		}else if(event.entityLiving instanceof EntitySlime)
			return;
		if(ConfigValues.VANILLAONLY){
			if(isVanilla(event.entityLiving)){
				makeMobReborn(event);
			}
		}else{
			makeMobReborn(event);
		}
	}

	private void makeMobReborn(LivingDropsEvent event){
		double rand = Math.random();
		String casename = EntityList.getEntityString(event.entityLiving);
		String name = casename.toLowerCase();
		if(MobRebirth.instance.getHasCustomMobSettings()){
			if(ArrayUtils.contains(ConfigValues.CUSTOMENTITIES, name)){
				if (rand <= ConfigValues.REBIRTHCHANCEMAP.get(name)) {
					if (ConfigValues.DROPEGGMAP.get(name) && EntityList.entityEggs.containsKey(name)){
						ItemStack dropEgg = new ItemStack(Items.spawn_egg);
						NBTTagCompound eggData = new NBTTagCompound();
						NBTTagCompound mobData = new NBTTagCompound();
						mobData.setString("id", casename);
						eggData.setTag("EntityTag", mobData);
						dropEgg.setTagCompound(eggData);
						event.entityLiving.entityDropItem(dropEgg, 0.0F);
					} else {
						createEntity(event);
						if(ConfigValues.EXTRAMOBCOUNTMAP.get(name) > 0){
							double rand2 = Math.random();
							if(ConfigValues.MULTIMOBMODE.toLowerCase().equals("all")){
								if(rand2 <= ConfigValues.MULTIMOBCHANCEMAP.get(name)){
									for(int i=0;i<ConfigValues.EXTRAMOBCOUNTMAP.get(name);i++){
										createEntity(event);
									}
								}
							}else if(ConfigValues.MULTIMOBMODE.toLowerCase().equals("per-mob")){
								for(int i=0;i<ConfigValues.EXTRAMOBCOUNTMAP.get(name);i++,rand2=new Random().nextDouble()){
									if(rand2 <= ConfigValues.MULTIMOBCHANCEMAP.get(name)){
										createEntity(event);
									}
								}
							}else{
								for(int i=0;i<ConfigValues.EXTRAMOBCOUNTMAP.get(name);i++,rand2=new Random().nextDouble()){
									if(rand2 <= ConfigValues.MULTIMOBCHANCEMAP.get(name)){
										createEntity(event);
									}else{
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
			if (ConfigValues.DROPEGG && EntityList.entityEggs.containsKey(name)){
				ItemStack dropEgg = new ItemStack(Items.spawn_egg);
				NBTTagCompound eggData = new NBTTagCompound();
				NBTTagCompound mobData = new NBTTagCompound();
				mobData.setString("id", casename);
				eggData.setTag("EntityTag", mobData);
				dropEgg.setTagCompound(eggData);
				event.entityLiving.entityDropItem(dropEgg, 0.0F);
			} else {
				createEntity(event);
				if(ConfigValues.EXTRAMOBCOUNT > 0){
					double rand2 = Math.random();
					if(ConfigValues.MULTIMOBMODE.toLowerCase().equals("all")){
						if(rand2 <= ConfigValues.MULTIMOBCHANCE){
							for(int i=0;i<ConfigValues.EXTRAMOBCOUNT;i++){
								createEntity(event);
							}
						}
					}else if(ConfigValues.MULTIMOBMODE.toLowerCase().equals("per-mob")){
						for(int i=0;i<ConfigValues.EXTRAMOBCOUNT;i++,rand2=new Random().nextDouble()){
							if(rand2 <= ConfigValues.MULTIMOBCHANCE){
								createEntity(event);
							}
						}
					}else{
						for(int i=0;i<ConfigValues.EXTRAMOBCOUNT;i++,rand2=new Random().nextDouble()){
							if(rand2 <= ConfigValues.MULTIMOBCHANCE){
								createEntity(event);
							}else{
								break;
							}
						}
					}
				}
			}
		}
	}

	private void createEntity(LivingDropsEvent event){
		//Store
		EntityLivingBase entity;
		World worldIn = event.entityLiving.worldObj;
		String sid = EntityList.getEntityString(event.entityLiving);
		NBTTagCompound storedData = event.entityLiving.getEntityData();
		event.entityLiving.writeEntityToNBT(storedData);
		ItemStack weapon = event.entityLiving.getHeldItem();
		float health = event.entityLiving.getMaxHealth();
		//Read
		entity = (EntityLivingBase) EntityList.createEntityByName(sid, worldIn);
		if(entity == null)
			return;
		entity.rotationYawHead = entity.rotationYaw;
		entity.renderYawOffset = entity.rotationYaw;
		storedData.setInteger("Health", (int)health);
		entity.readFromNBT(storedData);
		entity.setHealth(health);
		if(weapon != null)
			entity.setCurrentItemOrArmor(0, weapon);
		entity.setPosition(event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ);
		worldIn.spawnEntityInWorld(entity);
	}

	@SubscribeEvent
	public void entityDamaged(LivingHurtEvent event){
		if(event.source.isFireDamage() && !ConfigValues.DAMAGEFROMSUNLIGHT && event.entityLiving.isEntityUndead() && !event.entityLiving.isInLava() && event.entityLiving.worldObj.canBlockSeeSky(new BlockPos(MathHelper.floor_double(event.entityLiving.posX), MathHelper.floor_double(event.entityLiving.posY), MathHelper.floor_double(event.entityLiving.posZ)))){
			event.setCanceled(true);
		}
	}

	public static boolean isVanilla(EntityLivingBase entity){
		return entity.getClass() == EntityDragon.class || entity.getClass() == EntityWither.class || entity.getClass() == EntityBlaze.class || entity.getClass() == EntityCaveSpider.class || entity.getClass() == EntityCreeper.class || entity.getClass() == EntityEnderman.class || entity.getClass() == EntityEndermite.class || entity.getClass() == EntityGhast.class || entity.getClass() == EntityGiantZombie.class || entity.getClass() == EntityGuardian.class || entity.getClass() == EntityIronGolem.class || entity.getClass() == EntityMagmaCube.class || entity.getClass() == EntityPigZombie.class ||  entity.getClass() == EntitySilverfish.class || entity.getClass() == EntitySkeleton.class || entity.getClass() == EntitySlime.class || entity.getClass() == EntitySnowman.class || entity.getClass() == EntitySpider.class || entity.getClass() == EntityWitch.class || entity.getClass() == EntityZombie.class || entity.getClass() == EntityBat.class || entity.getClass() == EntityChicken.class || entity.getClass() == EntityCow.class || entity.getClass() == EntityHorse.class || entity.getClass() == EntityMooshroom.class || entity.getClass() == EntityOcelot.class || entity.getClass() == EntityPig.class || entity.getClass() == EntityRabbit.class || entity.getClass() == EntitySheep.class || entity.getClass() == EntitySquid.class || entity.getClass() == EntityVillager.class || entity.getClass() == EntityWolf.class;
	}
}

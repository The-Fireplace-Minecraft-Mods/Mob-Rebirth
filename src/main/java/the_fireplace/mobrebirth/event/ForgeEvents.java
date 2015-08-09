package the_fireplace.mobrebirth.event;

import java.util.Random;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import the_fireplace.mobrebirth.MobRebirth;
import the_fireplace.mobrebirth.config.ConfigValues;
/**
 * 
 * @author The_Fireplace
 *
 */
public class ForgeEvents {

	public static EntityLivingBase storedEntity;
	public static NBTTagCompound storedNBT;
	public static double storedX;
	public static double storedY;
	public static double storedZ;

	@SubscribeEvent
	public void onEntityLivingDeath(LivingDropsEvent event) {
		if(ConfigValues.REBIRTHFROMNONPLAYER == true){
			if ((event.entityLiving instanceof IMob)) {
				makeMobRebornTransition(event);
			}else if ((event.entityLiving instanceof IAnimals)) {
				if (ConfigValues.ANIMALREBIRTH == true){
					makeMobRebornTransition(event);
				}
			}
		}
		else{
			if(event.source.getEntity() instanceof EntityPlayer){
				if ((event.entityLiving instanceof IMob)) {
					makeMobRebornTransition(event);
				}else if ((event.entityLiving instanceof IAnimals)) {
					if (ConfigValues.ANIMALREBIRTH == true){
						makeMobRebornTransition(event);
					}
				}
			}
		}
	}
	private void makeMobRebornTransition(LivingDropsEvent event){
		if(ConfigValues.ALLOWBOSSES == true){
			if(event.entityLiving instanceof EntityWither || event.entityLiving instanceof EntityDragon){
				makeMobReborn(event);
				return;
			}
		}
		if(ConfigValues.ALLOWSLIMES == true){
			if(event.entityLiving instanceof EntitySlime || event.entityLiving instanceof EntityMagmaCube){
				makeMobReborn(event);
				return;
			}
		}else{
			if(event.entityLiving instanceof EntitySlime || event.entityLiving instanceof EntityMagmaCube){
				return;
			}
		}
		if(ConfigValues.VANILLAONLY == true){
			if((event.entityLiving.getClass().getPackage().toString().contains("net.minecraft"))){
				makeMobReborn(event);
				return;
			}
		}else{
			makeMobReborn(event);
		}
	}
	private void makeMobReborn(LivingDropsEvent event){
		double rand = Math.random();
		int id = EntityList.getEntityID(event.entityLiving);
		if (rand <= ConfigValues.REBIRTHCHANCE) {
			if (!ConfigValues.DROPEGG && EntityList.entityEggs.containsKey(id)){
				ItemStack dropEgg = new ItemStack(Items.spawn_egg, 1, id);
				event.entityLiving.entityDropItem(dropEgg, 0.0F);}
			else{
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
		MobRebirth.logger.addToLog(event.entityLiving.getName()+" being created.");
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
		if(entity == null){
			MobRebirth.logger.addToLog("Entity is null when recreated, cancelling rebirth");
			return;
		}
		entity.rotationYawHead = entity.rotationYaw;
		entity.renderYawOffset = entity.rotationYaw;
		storedData.setInteger("Health", (int)health);
		entity.readFromNBT(storedData);
		entity.setHealth(health);
		if(weapon != null)
			entity.setCurrentItemOrArmor(0, weapon);
		entity.setPosition(event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ);
		worldIn.spawnEntityInWorld(entity);
		MobRebirth.logger.addToLog("Entity creation completed.");
	}

	@SubscribeEvent
	public void entityDamaged(LivingHurtEvent event){
		if(event.source.isFireDamage() && ConfigValues.DAMAGEFROMSUNLIGHT == true && event.entityLiving.isEntityUndead() && event.entityLiving.worldObj.canBlockSeeSky(new BlockPos(MathHelper.floor_double(event.entityLiving.posX), MathHelper.floor_double(event.entityLiving.posY), MathHelper.floor_double(event.entityLiving.posZ)))){
			event.setCanceled(true);
		}
	}
}

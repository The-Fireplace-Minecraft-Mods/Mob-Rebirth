package the_fireplace.mobrebirth.event;

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
		if(ConfigValues.NATURALREBIRTH == true){
			if ((event.entityLiving instanceof IMob)) {
				makeMobRebornTransition(event);
			}else if ((event.entityLiving instanceof IAnimals)) {
				if (ConfigValues.SPAWNANIMALS == true){
					makeMobRebornTransition(event);
				}
			}
		}
		else{
			if(event.source.getEntity() instanceof EntityPlayer){
				if ((event.entityLiving instanceof IMob)) {
					makeMobRebornTransition(event);
				}else if ((event.entityLiving instanceof IAnimals)) {
					if (ConfigValues.SPAWNANIMALS == true){
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
		if (rand <= ConfigValues.SPAWNMOBCHANCE) {
			if (ConfigValues.SPAWNMOB == false && EntityList.entityEggs.containsKey(id)){
				ItemStack dropEgg = new ItemStack(Items.spawn_egg, 1, id);
				event.entityLiving.entityDropItem(dropEgg, 0.0F);}
			else{
				createEntity(event);
				if(ConfigValues.EXTRAMOBCOUNT > 0){
					double rand2 = Math.random();
					if(ConfigValues.MULTIMOBMODE.toLowerCase() == "all"){
						if(rand2 <= ConfigValues.MULTIMOBCHANCE){
							int i = 0;
							while(i < ConfigValues.EXTRAMOBCOUNT){
								createEntity(event);
								i = i+1;
							}
						}
					}
					else{
						int i = 0;
						while(i < ConfigValues.EXTRAMOBCOUNT){
							if(rand2 <= ConfigValues.MULTIMOBCHANCE){
								createEntity(event);
							}
							i = i+1;
						}
					}
				}

			}
		}

	}

	private void createEntity(LivingDropsEvent event){
		EntityLivingBase entity;
		World worldIn = event.entityLiving.worldObj;
		int id = EntityList.getEntityID(event.entityLiving);
		NBTTagCompound storedData = event.entityLiving.getEntityData();
		ItemStack weapon = event.entityLiving.getHeldItem();
		entity = (EntityLivingBase) EntityList.createEntityByID(id, worldIn);
		entity.rotationYawHead = entity.rotationYaw;
		entity.renderYawOffset = entity.rotationYaw;
		entity.setHealth(event.entityLiving.getMaxHealth());
		storedData.setInteger("Health", (int)event.entityLiving.getMaxHealth());
		entity.setPosition(event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ);
		entity.readFromNBT(storedData);
		entity.setPosition(event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ);
		entity.setCurrentItemOrArmor(0, weapon);
		worldIn.spawnEntityInWorld(entity);
		entity.setPosition(event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ);
	}

	@SubscribeEvent
	public void entityDamaged(LivingHurtEvent event){
		if(event.source.isFireDamage() && ConfigValues.SUNLIGHTAPOCALYPSEFIX == true && event.entityLiving.isEntityUndead() && event.entityLiving.worldObj.canBlockSeeSky(new BlockPos(MathHelper.floor_double(event.entityLiving.posX), MathHelper.floor_double(event.entityLiving.posY), MathHelper.floor_double(event.entityLiving.posZ)))){
			event.setCanceled(true);
		}
	}
}

package the_fireplace.mobrebirth;

import the_fireplace.mobrebirth.config.ConfigValues;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraft.entity.monster.*;

public class MobRebirthHandler {

public static EntityLivingBase storedEntity;
public static NBTTagCompound storedNBT;
public static double storedX;
public static double storedY;
public static double storedZ;
	
	@SubscribeEvent
	public void onEntityLivingDeath(LivingDropsEvent event) {
		//Store data needed to spawn
       storedEntity = event.entityLiving;
       storedNBT = event.entityLiving.getEntityData();
       storedX = event.entityLiving.posX;
       storedY = event.entityLiving.posY;
       storedZ = event.entityLiving.posZ;
        //Check if conditions match config and act accordingly
		if(ConfigValues.NATURALREBIRTH == true){
			makeMobReborn(event);
		}
		else{
			if(event.source.getEntity() instanceof EntityPlayer){
				if ((event.entityLiving instanceof IMob)) {//Checks to see if it was a Mob
					makeMobReborn(event);
				}else if ((event.entityLiving instanceof IAnimals)) {//Checks to see if it was an Animal
					if (ConfigValues.SPAWNANIMALS == true){//Checks if Animal Spawning is enabled
						makeMobReborn(event);
					}
				}
			}
		}
	}
	private void makeMobReborn(LivingDropsEvent event){
		double rand = Math.random();
		if (rand <= ConfigValues.SPAWNMOBCHANCE) {//Checks the chance to see if anything should happen
			int id = EntityList.getEntityID(event.entityLiving);
			if (id > 0) {
				if (ConfigValues.SPAWNMOB == false){ //This segment only works on mobs with vanilla spawn eggs
					ItemStack dropEgg = new ItemStack(Items.spawn_egg, 1, id);
					event.entityLiving.entityDropItem(dropEgg, 0.0F);}
					//end of segment
					//TODO: Add custom spawn egg that can spawn all mobs, so this feature works on mobs with custom spawn eggs.
				else{
					World world = event.entityLiving.worldObj;
					EntityLivingBase entityliving = storedEntity;
					
					storedNBT.setInteger("health", (int) storedEntity.getMaxHealth());
                    entityliving.setLocationAndAngles(storedX, storedY, storedZ, MathHelper.wrapAngleTo180_float(world.rand.nextFloat() * 360.0F), 0.0F);
                    entityliving.readFromNBT(storedNBT);
                    entityliving.rotationYawHead = entityliving.rotationYaw;
                    entityliving.renderYawOffset = entityliving.rotationYaw;
                    //((EntityLiving) entityliving).onSpawnWithEgg((IEntityLivingData)null);
                    world.spawnEntityInWorld(entityliving);
                    ((EntityLiving) entityliving).playLivingSound();
				}
			}
		}	
	}
}

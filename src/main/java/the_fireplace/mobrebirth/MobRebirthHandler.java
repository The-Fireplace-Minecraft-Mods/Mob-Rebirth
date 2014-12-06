package the_fireplace.mobrebirth;

import the_fireplace.mobrebirth.config.ConfigValues;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
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
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraft.entity.monster.*;

public class MobRebirthHandler {
	
	@SubscribeEvent
	public void onEntityLivingDeath(LivingDropsEvent event) {
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
		EntityLivingBase storedEntity = event.entityLiving;
		Entity entity;
		World worldIn = event.entityLiving.worldObj;
		NBTTagCompound storedData = event.entityLiving.getEntityData();
		int id = EntityList.getEntityID(event.entityLiving);
		if (rand <= ConfigValues.SPAWNMOBCHANCE) {
			if (id > 0 && EntityList.entityEggs.containsKey(id)) {
					if (ConfigValues.SPAWNMOB == false){
						ItemStack dropEgg = new ItemStack(Items.spawn_egg, 1, id);
						event.entityLiving.entityDropItem(dropEgg, 0.0F);}
					else{
						entity = EntityList.createEntityByID(id, worldIn);
		                EntityLiving entityliving = (EntityLiving)entity;
		                entity.setLocationAndAngles(event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ, MathHelper.wrapAngleTo180_float(worldIn.rand.nextFloat() * 360.0F), 0.0F);
		                entityliving.rotationYawHead = entityliving.rotationYaw;
		                entityliving.renderYawOffset = entityliving.rotationYaw;
		                ((EntityLivingBase) entity).writeToNBT(storedData);
		                worldIn.spawnEntityInWorld(entity);
		                }
				}
				
			
		}
	}
}

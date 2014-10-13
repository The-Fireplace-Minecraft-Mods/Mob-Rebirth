package the_fireplace.mobrebirth;

import the_fireplace.mobrebirth.config.ConfigValues;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraft.entity.monster.*;

public class MobRebirthHandler {

public static Entity storedEntity;
public static NBTTagCompound storedNBT;
	
	@SubscribeEvent
	public void onEntityLivingDeath(LivingDropsEvent event) {
       storedEntity = event.entityLiving;
       storedNBT = event.entityLiving.NBTTagCompound;
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
			if (id > 0 && EntityList.entityEggs.containsKey(id)) {
					if (ConfigValues.SPAWNMOB == false){ //Checks to see if creature spawning instead of Eggs is turned off
						ItemStack dropEgg = new ItemStack(Items.spawn_egg, 1, id); //sets what egg should drop
						event.entityLiving.entityDropItem(dropEgg, 0.0F);}//Makes the egg drop
					else{//TODO make it metadata sensitive
						if(event.entityLiving instanceof EntitySlime){
							
							
							Entity entity = ItemMonsterPlacer.spawnCreature(event.entityLiving.worldObj , id, event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ);
						}else{
						Entity entity = ItemMonsterPlacer.spawnCreature(event.entityLiving.worldObj , id, event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ);
						}
				}
				}
				
			
		}
	}
}

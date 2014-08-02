package f1repl4ce.mobrebirth;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import f1repl4ce.mobrebirth.config.ConfigValues;
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
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraft.entity.monster.*;

public class MobRebirthHandler {
	
	@SubscribeEvent
	public void onEntityLivingDeath(LivingDeathEvent event) {//TODO make it so slimes spawn the correct size
		if(ConfigValues.NATURALREBIRTH == true){
			makeMobReborn(event);
		}
		else{
			//New experimental code
			if(/*EntityDamageSource*/event.source.getEntity().equals("player")){
				if ((event.entityLiving instanceof IMob)) {//Checks to see if it was a Mob
					makeMobReborn(event);
				}else if ((event.entityLiving instanceof IAnimals)) {//Checks to see if it was an Animal
					if (ConfigValues.SPAWNANIMALS == true){//Checks if Animal Spawning is enabled
						makeMobReborn(event);
					}
				}
			//Old code(didn't work on mod entities, such as IC2 Mining Laser, gun mods of any kind, etc
		/*if (event.source.getDamageType().equals("player")) {//Checks to see if a player killed it
			EntityPlayer p = (EntityPlayer) event.source.getEntity();

				if ((event.entityLiving instanceof IMob)) {//Checks to see if it was a Mob
					makeMobReborn(event);
			}
				else
				if ((event.entityLiving instanceof IAnimals)) {//Checks to see if it was an Animal
					if (ConfigValues.SPAWNANIMALS == true){//Checks if Animal Spawning is enabled
						makeMobReborn(event);
				}
			}
		}
		if (event.source.getSourceOfDamage() instanceof EntityArrow) {
			if (((EntityArrow) event.source.getSourceOfDamage()).shootingEntity != null) {
				if (((EntityArrow) event.source.getSourceOfDamage()).shootingEntity instanceof EntityPlayer) {//checks to see if the arrow came from a player
					EntityPlayer p = (EntityPlayer) event.source.getEntity();
						if ((event.entityLiving instanceof IMob)) {//Checks to see if the entity was a mob
							makeMobReborn(event);
						}
						else
							if ((event.entityLiving instanceof IAnimals)) {//Checks to see if it was an Animal
								if (ConfigValues.SPAWNANIMALS = true){//Checks if Animal Spawning is enabled
								makeMobReborn(event);
							}
						}
				}

			}

		}*/
			}
		}
	}
	private void makeMobReborn(LivingDeathEvent event){
		double rand = Math.random();
		//rand = 0.0d;
		if (rand <= ConfigValues.SPAWNMOBCHANCE) {//Checks the chance to see if anything should happen
			int id = EntityList.getEntityID(event.entityLiving);
			if (id > 0 && EntityList.entityEggs.containsKey(id)) {
					if (ConfigValues.SPAWNMOB == false){ //Checks to see if creature spawning instead of Eggs is turned off
						ItemStack dropEgg = new ItemStack(Items.spawn_egg, 1, id); //sets what egg should drop
						event.entityLiving.entityDropItem(dropEgg, 0.0F);}//Makes the egg drop
					else{
						Entity entity = ItemMonsterPlacer.spawnCreature(event.entityLiving.worldObj , id, event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ);
				}
				}
				
			
		}
	}
}

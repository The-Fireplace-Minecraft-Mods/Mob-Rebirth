package the_fireplace.mobrebirth.event;

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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import the_fireplace.mobrebirth.MobRebirth;
import the_fireplace.mobrebirth.config.ConfigValues;

import java.util.Random;
/**
 *
 * @author The_Fireplace
 *
 */
public class ForgeEvents {

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
		if(eventArgs.modID.equals(MobRebirth.MODID))
			MobRebirth.syncConfig();
	}

	@SubscribeEvent
	public void onEntityLivingDeath(LivingDropsEvent event) {
		if(ConfigValues.REBIRTHFROMNONPLAYER) {
			if (event.entityLiving instanceof IMob)
				makeMobRebornTransition(event);
			else if (event.entityLiving instanceof IAnimals && ConfigValues.ANIMALREBIRTH)
				makeMobRebornTransition(event);
		}else if(event.source.getEntity() instanceof EntityPlayer)
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
			if((event.entityLiving.getClass().getPackage().toString().contains("net.minecraft"))){
				makeMobReborn(event);
			}
		}else{
			makeMobReborn(event);
		}
	}
	private void makeMobReborn(LivingDropsEvent event){
		double rand = Math.random();
		int id = EntityList.getEntityID(event.entityLiving);
		if (rand <= ConfigValues.REBIRTHCHANCE) {
			if (ConfigValues.DROPEGG && EntityList.entityEggs.containsKey(id)){
				ItemStack dropEgg = new ItemStack(Items.spawn_egg, 1, id);
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
		ItemStack weapon = event.entityLiving.getHeldItem(EnumHand.MAIN_HAND);
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
			entity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, weapon);
		entity.setPosition(event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ);
		worldIn.spawnEntityInWorld(entity);
	}

	@SubscribeEvent
	public void entityDamaged(LivingHurtEvent event){
		if(event.source.isFireDamage() && !ConfigValues.DAMAGEFROMSUNLIGHT && event.entityLiving.isEntityUndead() && !event.entityLiving.isInLava() && event.entityLiving.worldObj.canBlockSeeSky(new BlockPos(MathHelper.floor_double(event.entityLiving.posX), MathHelper.floor_double(event.entityLiving.posY), MathHelper.floor_double(event.entityLiving.posZ)))){
			event.setCanceled(true);
		}
	}
}

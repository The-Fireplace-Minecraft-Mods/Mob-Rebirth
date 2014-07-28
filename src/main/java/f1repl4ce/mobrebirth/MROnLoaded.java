package f1repl4ce.mobrebirth;

import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.EnumDifficulty;

public class MROnLoaded {
@SubscribeEvent
private void onWorldLoad(FMLServerStartedEvent event, EntityPlayer player){
	if(ConfigValues.NATURALREBIRTH == true && ConfigValues.SPAWNMOB == true && ConfigValues.SPAWNMOBCHANCE == 1.00){//add check to make sure it isn't in peaceful mode
		player.addStat(ModBase.insanity, 1);
	}
	/*if(ConfigValues.NATURALREBIRTH == true && ConfigValues.SPAWNMOB == true && ConfigValues.SPAWNMOBCHANCE == 1.00){//add check to make sure it isn't in peaceful mode
		player.addStat(ModBase.truehardcore, 1);//add check to see if in hardcore
	}*/
	if(ConfigValues.SPAWNMOB == false && ConfigValues.SPAWNMOBCHANCE == 1.00){
		player.addStat(ModBase.tooeasy, 1);
	}
	if(ConfigValues.SPAWNMOBCHANCE == 0.00){
		player.addStat(ModBase.useless, 1);
	}
}
}

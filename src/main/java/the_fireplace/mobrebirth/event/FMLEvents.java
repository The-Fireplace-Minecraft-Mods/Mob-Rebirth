package the_fireplace.mobrebirth.event;

import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import the_fireplace.mobrebirth.MobRebirth;
/**
 * 
 * @author The_Fireplace
 *
 */
public class FMLEvents {
	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
		if(eventArgs.modID.equals(MobRebirth.MODID))
			MobRebirth.syncConfig();
	}
}

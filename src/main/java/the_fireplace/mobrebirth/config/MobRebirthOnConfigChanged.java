package the_fireplace.mobrebirth.config;

import the_fireplace.mobrebirth.ModBase;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class MobRebirthOnConfigChanged {
	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
	     if(eventArgs.modID.equals("mobrebirth"))
	         ModBase.syncConfig();
	}

}

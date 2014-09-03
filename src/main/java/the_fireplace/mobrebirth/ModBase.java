package the_fireplace.mobrebirth;

import java.util.logging.Level;

import the_fireplace.mobrebirth.config.ConfigValues;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = "mobrebirth", name = "Mob Rebirth", version = "2.0.3", acceptedMinecraftVersions="1.7.10", canBeDeactivated = true, guiFactory = "the_fireplace.mobrebirth.config.MobRebirthGuiFactory", dependencies="required-after:fireplacecore@[1.2.0,)")
public class ModBase {

	@Instance("mobrebirth")
	public static ModBase instance;
	
	public static Configuration file;
	
	@EventHandler
	public void PreInit(FMLPreInitializationEvent event) {
		file = new Configuration(event.getSuggestedConfigurationFile());
		syncConfig();
	}
	public static void syncConfig(){
		ConfigValues.SPAWNMOBCHANCE = file.get(Configuration.CATEGORY_GENERAL, ConfigValues.SPAWNMOBCHANCE_NAME, ConfigValues.SPAWNMOBCHANCE_DEFAULT).getDouble(ConfigValues.SPAWNMOBCHANCE_DEFAULT);
		ConfigValues.SPAWNMOB = file.get(Configuration.CATEGORY_GENERAL, ConfigValues.SPAWNMOB_NAME, ConfigValues.SPAWNMOB_DEFAULT).getBoolean(ConfigValues.SPAWNMOB_DEFAULT);
		ConfigValues.NATURALREBIRTH = file.get(Configuration.CATEGORY_GENERAL, ConfigValues.NATURALREBIRTH_NAME, ConfigValues.NATURALREBIRTH_DEFAULT).getBoolean(ConfigValues.NATURALREBIRTH_DEFAULT);
		ConfigValues.SPAWNANIMALS = file.get(Configuration.CATEGORY_GENERAL, ConfigValues.SPAWNANIMALS_NAME, ConfigValues.SPAWNANIMALS_DEFAULT).getBoolean(ConfigValues.SPAWNANIMALS_DEFAULT);
		if(file.hasChanged()){
	        file.save();
		}
	}
	@EventHandler
	public void Init(FMLInitializationEvent event) {
		FMLCommonHandler.instance().bus().register(instance);
		MinecraftForge.EVENT_BUS.register(new MobRebirthHandler());
	}
	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
	     if(eventArgs.modID.equals("mobrebirth"))
	         syncConfig();
	}
}

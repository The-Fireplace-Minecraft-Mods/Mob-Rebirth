package the_fireplace.mobrebirth;

import java.util.logging.Level;

import the_fireplace.mobrebirth.config.ConfigValues;
import the_fireplace.mobrebirth.config.MobRebirthOnConfigChanged;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
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

@Mod(modid = "mobrebirth", name = "Mob Rebirth", version = "2.0.4", acceptedMinecraftVersions="1.7.2,1.7.10", canBeDeactivated = true, guiFactory = "the_fireplace.mobrebirth.config.MobRebirthGuiFactory", dependencies="required-after:fireplacecore@[1.2.0,)")
public class ModBase {

	@Instance("mobrebirth")
	public static ModBase instance;
	public static Configuration file;
	
	public static Property SPAWNMOBCHANCE_PROPERTY;
	public static Property SPAWNMOB_PROPERTY;
	public static Property NATURALREBIRTH_PROPERTY;
	public static Property SPAWNANIMALS_PROPERTY;
	
	
	public static void syncConfig(){
		ConfigValues.SPAWNMOBCHANCE = SPAWNMOBCHANCE_PROPERTY.getDouble();
		ConfigValues.SPAWNMOB = SPAWNMOB_PROPERTY.getBoolean();
		ConfigValues.NATURALREBIRTH = NATURALREBIRTH_PROPERTY.getBoolean();
		ConfigValues.SPAWNANIMALS = SPAWNANIMALS_PROPERTY.getBoolean();
		if(file.hasChanged()){
	        file.save();
		}
	}
	
	@EventHandler
	public void PreInit(FMLPreInitializationEvent event) {
		file = new Configuration(event.getSuggestedConfigurationFile());
		file.load();
		SPAWNMOBCHANCE_PROPERTY = file.get(Configuration.CATEGORY_GENERAL, ConfigValues.SPAWNMOBCHANCE_NAME, ConfigValues.SPAWNMOBCHANCE_DEFAULT);
		SPAWNMOB_PROPERTY = file.get(Configuration.CATEGORY_GENERAL, ConfigValues.SPAWNMOB_NAME, ConfigValues.SPAWNMOB_DEFAULT);
		NATURALREBIRTH_PROPERTY = file.get(Configuration.CATEGORY_GENERAL, ConfigValues.NATURALREBIRTH_NAME, ConfigValues.NATURALREBIRTH_DEFAULT);
		SPAWNANIMALS_PROPERTY = file.get(Configuration.CATEGORY_GENERAL, ConfigValues.SPAWNANIMALS_NAME, ConfigValues.SPAWNANIMALS_DEFAULT);
		syncConfig();
	}
	@EventHandler
	public void Init(FMLInitializationEvent event) {
		FMLCommonHandler.instance().bus().register(instance);
		FMLCommonHandler.instance().bus().register(new MobRebirthOnConfigChanged());
		MinecraftForge.EVENT_BUS.register(new MobRebirthHandler());
	}
}

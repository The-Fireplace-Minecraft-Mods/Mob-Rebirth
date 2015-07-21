package the_fireplace.mobrebirth;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import the_fireplace.fulcrum.api.API;
import the_fireplace.fulcrum.logger.Logger;
import the_fireplace.fulcrum.math.VersionMath;
import the_fireplace.mobrebirth.config.ConfigValues;
import the_fireplace.mobrebirth.event.FMLEvents;
import the_fireplace.mobrebirth.event.ForgeEvents;
import the_fireplace.mobrebirth.gui.RebirthChanceSlider;
/**
 * 
 * @author The_Fireplace
 *
 */
@Mod(modid = MobRebirth.MODID, name = MobRebirth.MODNAME, version = MobRebirth.VERSION, acceptedMinecraftVersions="1.8", canBeDeactivated = true, guiFactory = "the_fireplace.mobrebirth.config.MobRebirthGuiFactory")
public class MobRebirth {
	@Instance(MobRebirth.MODID)
	public static MobRebirth instance;
	public static final String MODID = "mobrebirth";
	public static final String MODNAME = "Mob Rebirth";
	public static final String VERSION = "2.2.1.0";
	private static final String downloadURL = "http://goo.gl/EQw3Ha";
	public static Logger logger = new Logger(MODID);

	public static Configuration config;

	public static Property SPAWNMOBCHANCE_PROPERTY;
	public static Property SPAWNMOB_PROPERTY;
	public static Property NATURALREBIRTH_PROPERTY;
	public static Property SPAWNANIMALS_PROPERTY;
	public static Property EXTRAMOBCOUNT_PROPERTY;
	public static Property MULTIMOBCHANCE_PROPERTY;
	public static Property MULTIMOBMODE_PROPERTY;
	public static Property SUNLIGHTAPOCALYPSEFIX_PROPERTY;
	public static Property VANILLAONLY_PROPERTY;
	public static Property ALLOWBOSSES_PROPERTY;
	public static Property ALLOWSLIMES_PROPERTY;

	public static void syncConfig(){
		ConfigValues.SPAWNMOBCHANCE = SPAWNMOBCHANCE_PROPERTY.getDouble();
		ConfigValues.SPAWNMOB = SPAWNMOB_PROPERTY.getBoolean();
		ConfigValues.NATURALREBIRTH = NATURALREBIRTH_PROPERTY.getBoolean();
		ConfigValues.SPAWNANIMALS = SPAWNANIMALS_PROPERTY.getBoolean();
		ConfigValues.EXTRAMOBCOUNT = EXTRAMOBCOUNT_PROPERTY.getInt();
		ConfigValues.MULTIMOBCHANCE = MULTIMOBCHANCE_PROPERTY.getDouble();
		ConfigValues.MULTIMOBMODE = MULTIMOBMODE_PROPERTY.getString();
		ConfigValues.SUNLIGHTAPOCALYPSEFIX = SUNLIGHTAPOCALYPSEFIX_PROPERTY.getBoolean();
		ConfigValues.ALLOWBOSSES = ALLOWBOSSES_PROPERTY.getBoolean();
		ConfigValues.VANILLAONLY = VANILLAONLY_PROPERTY.getBoolean();
		ConfigValues.ALLOWSLIMES = ALLOWSLIMES_PROPERTY.getBoolean();
		if(config.hasChanged()){
			config.save();
		}
	}

	@EventHandler
	public void PreInit(FMLPreInitializationEvent event) {
		logger.create();
		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		SPAWNMOBCHANCE_PROPERTY = config.get(Configuration.CATEGORY_GENERAL, ConfigValues.SPAWNMOBCHANCE_NAME, ConfigValues.SPAWNMOBCHANCE_DEFAULT, StatCollector.translateToLocal("mrb2.tooltip"));
		SPAWNMOB_PROPERTY = config.get(Configuration.CATEGORY_GENERAL, ConfigValues.SPAWNMOB_NAME, ConfigValues.SPAWNMOB_DEFAULT, StatCollector.translateToLocal("mrb1.tooltip"));
		NATURALREBIRTH_PROPERTY = config.get(Configuration.CATEGORY_GENERAL, ConfigValues.NATURALREBIRTH_NAME, ConfigValues.NATURALREBIRTH_DEFAULT, StatCollector.translateToLocal("mrb5.tooltip"));
		SPAWNANIMALS_PROPERTY = config.get(Configuration.CATEGORY_GENERAL, ConfigValues.SPAWNANIMALS_NAME, ConfigValues.SPAWNANIMALS_DEFAULT, StatCollector.translateToLocal("mrb3.tooltip"));
		EXTRAMOBCOUNT_PROPERTY = config.get(Configuration.CATEGORY_GENERAL, ConfigValues.EXTRAMOBCOUNT_NAME, ConfigValues.EXTRAMOBCOUNT_DEFAULT, StatCollector.translateToLocal("mrb4.tooltip"));
		MULTIMOBCHANCE_PROPERTY = config.get(Configuration.CATEGORY_GENERAL, ConfigValues.MULTIMOBCHANCE_NAME, ConfigValues.MULTIMOBCHANCE_DEFAULT, StatCollector.translateToLocal("mrb6.tooltip"));
		MULTIMOBMODE_PROPERTY = config.get(Configuration.CATEGORY_GENERAL, ConfigValues.MULTIMOBMODE_NAME, ConfigValues.MULTIMOBMODE_DEFAULT, StatCollector.translateToLocal("mrb7.tooltip"));
		SUNLIGHTAPOCALYPSEFIX_PROPERTY = config.get(Configuration.CATEGORY_GENERAL, ConfigValues.SUNLIGHTAPOCALYPSEFIX_NAME, ConfigValues.SUNLIGHTAPOCALYPSEFIX_DEFAULT, StatCollector.translateToLocal("solar_apocalypse_fix.tooltip"));
		ALLOWBOSSES_PROPERTY = config.get(Configuration.CATEGORY_GENERAL, ConfigValues.ALLOWBOSSES_NAME, ConfigValues.ALLOWBOSSES_DEFAULT, StatCollector.translateToLocal("allowbosses.tooltip"));
		VANILLAONLY_PROPERTY = config.get(Configuration.CATEGORY_GENERAL, ConfigValues.VANILLAONLY_NAME, ConfigValues.VANILLAONLY_DEFAULT, StatCollector.translateToLocal("vanillaonly.tooltip"));
		ALLOWSLIMES_PROPERTY = config.get(Configuration.CATEGORY_GENERAL, ConfigValues.ALLOWSLIMES_NAME, ConfigValues.ALLOWSLIMES_DEFAULT, StatCollector.translateToLocal("allowslimes.tooltip"));
		if(event.getSide().isClient())
			SPAWNMOBCHANCE_PROPERTY.setConfigEntryClass(RebirthChanceSlider.class);
		SPAWNMOBCHANCE_PROPERTY.setMaxValue(1.0);
		SPAWNMOBCHANCE_PROPERTY.setMinValue(0.0);
		if(event.getSide().isClient())
			MULTIMOBCHANCE_PROPERTY.setConfigEntryClass(RebirthChanceSlider.class);
		MULTIMOBCHANCE_PROPERTY.setMaxValue(1.0);
		MULTIMOBCHANCE_PROPERTY.setMinValue(0.0);

		syncConfig();
		API.registerModToVersionChecker(this.MODNAME, this.VERSION, VersionMath.getVersionFor("https://dl.dropboxusercontent.com/s/x4a9lubkolghoge/prerelease.version?dl=0"), VersionMath.getVersionFor("https://dl.dropboxusercontent.com/s/xpf1swir6n9rx3c/release.version?dl=0"), this.downloadURL, this.MODID);
	}
	@EventHandler
	public void Init(FMLInitializationEvent event) {
		FMLCommonHandler.instance().bus().register(new FMLEvents());
		MinecraftForge.EVENT_BUS.register(new ForgeEvents());
	}
}

package the_fireplace.mobrebirth;

import com.google.common.collect.Maps;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import net.minecraftforge.fml.relauncher.Side;
import the_fireplace.mobrebirth.client.gui.CustomArrayEntry;
import the_fireplace.mobrebirth.client.gui.RebirthChanceSlider;
import the_fireplace.mobrebirth.common.CommonEvents;
import the_fireplace.mobrebirth.common.ConfigValues;

import java.io.File;
import java.util.Map;

/**
 * @author The_Fireplace
 */
@Mod(modid = MobRebirth.MODID, name = MobRebirth.MODNAME, canBeDeactivated = true, guiFactory = "the_fireplace.mobrebirth.client.gui.MobRebirthGuiFactory", updateJSON = "http://thefireplace.bitnamiapp.com/jsons/mobrebirth.json")
public class MobRebirth {
	public static final String MODID = "mobrebirth";
	public static final String MODNAME = "Mob Rebirth";
	private static final File configDir = new File((File) FMLInjectionData.data()[6], "config/MobRebirth/");
	private static final File customConfigDir = new File(configDir, "mobs");
	private boolean customProperties = false;
	public static Map<String, Configuration> mobConfigs = Maps.newHashMap();

	@Mod.Instance(MODID)
	public static MobRebirth instance;

	public boolean getHasCustomMobSettings(){
		return customProperties;
	}

	public static Configuration mobcontrols;
	public static Configuration chancecontrols;
	public static Configuration behaviorcontrols;
	public static Configuration debugcontrols;
	public static Configuration general;

	public static Property ALLOWBOSSES_PROPERTY;
	public static Property ALLOWSLIMES_PROPERTY;
	public static Property ANIMALREBIRTH_PROPERTY;

	public static Property REBIRTHCHANCE_PROPERTY;
	public static Map<String, Property> REBIRTHCHANCEMAP;
	public static Property MULTIMOBCHANCE_PROPERTY;
	public static Map<String, Property> MULTIMOBCHANCEMAP;

	public static Property DAMAGEFROMSUNLIGHT_PROPERTY;
	public static Property DROPEGG_PROPERTY;
	public static Map<String, Property> DROPEGGMAP;
	public static Property EXTRAMOBCOUNT_PROPERTY;
	public static Map<String, Property> EXTRAMOBCOUNTMAP;
	public static Property MULTIMOBMODE_PROPERTY;
	public static Property REBIRTHFROMNONPLAYER_PROPERTY;
	public static Map<String, Property> PLAYERREBIRTHMAP;

	public static Property VANILLAONLY_PROPERTY;

	public static Property CUSTOMENTITIES_PROPERTY;

	public MobRebirth(){
		REBIRTHCHANCEMAP = Maps.newHashMap();
		MULTIMOBCHANCEMAP = Maps.newHashMap();
		DROPEGGMAP = Maps.newHashMap();
		EXTRAMOBCOUNTMAP = Maps.newHashMap();
		PLAYERREBIRTHMAP = Maps.newHashMap();
	}

	public static void syncConfig(){
		ConfigValues.ALLOWBOSSES = ALLOWBOSSES_PROPERTY.getBoolean();
		ConfigValues.ALLOWSLIMES = ALLOWSLIMES_PROPERTY.getBoolean();
		ConfigValues.ANIMALREBIRTH = ANIMALREBIRTH_PROPERTY.getBoolean();

		ConfigValues.REBIRTHCHANCE = REBIRTHCHANCE_PROPERTY.getDouble();
		ConfigValues.MULTIMOBCHANCE = MULTIMOBCHANCE_PROPERTY.getDouble();

		ConfigValues.DAMAGEFROMSUNLIGHT = DAMAGEFROMSUNLIGHT_PROPERTY.getBoolean();
		ConfigValues.DROPEGG = DROPEGG_PROPERTY.getBoolean();
		ConfigValues.EXTRAMOBCOUNT = EXTRAMOBCOUNT_PROPERTY.getInt();
		ConfigValues.MULTIMOBMODE = MULTIMOBMODE_PROPERTY.getString();
		ConfigValues.REBIRTHFROMNONPLAYER = REBIRTHFROMNONPLAYER_PROPERTY.getBoolean();

		ConfigValues.VANILLAONLY = VANILLAONLY_PROPERTY.getBoolean();

		ConfigValues.CUSTOMENTITIES = CUSTOMENTITIES_PROPERTY.getStringList();
		if(mobcontrols.hasChanged())
			mobcontrols.save();
		if(chancecontrols.hasChanged())
			chancecontrols.save();
		if(behaviorcontrols.hasChanged())
			behaviorcontrols.save();
		if(debugcontrols.hasChanged())
			debugcontrols.save();
		if(general.hasChanged())
			general.save();
		instance.customProperties = ConfigValues.CUSTOMENTITIES != null && ConfigValues.CUSTOMENTITIES.length > 0;
	}

	@EventHandler
	public void PreInit(FMLPreInitializationEvent event) {
		mobcontrols = new Configuration(new File(configDir, "mobcontrols.cfg"));
		chancecontrols = new Configuration(new File(configDir, "chancecontrols.cfg"));
		behaviorcontrols = new Configuration(new File(configDir, "behaviorcontrols.cfg"));
		debugcontrols = new Configuration(new File(configDir, "debugcontrols.cfg"));
		general = new Configuration(new File(configDir, "general.cfg"));
		mobcontrols.load();
		chancecontrols.load();
		behaviorcontrols.load();
		debugcontrols.load();
		general.load();
		//Mob Controls
		ALLOWBOSSES_PROPERTY = mobcontrols.get(Configuration.CATEGORY_GENERAL, ConfigValues.ALLOWBOSSES_NAME, ConfigValues.ALLOWBOSSES_DEFAULT);
		ALLOWSLIMES_PROPERTY = mobcontrols.get(Configuration.CATEGORY_GENERAL, ConfigValues.ALLOWSLIMES_NAME, ConfigValues.ALLOWSLIMES_DEFAULT);
		ANIMALREBIRTH_PROPERTY = mobcontrols.get(Configuration.CATEGORY_GENERAL, ConfigValues.ANIMALREBIRTH_NAME, ConfigValues.ANIMALREBIRTH_DEFAULT);
		//Chance Controls
		REBIRTHCHANCE_PROPERTY = chancecontrols.get(Configuration.CATEGORY_GENERAL, ConfigValues.REBIRTHCHANCE_NAME, ConfigValues.REBIRTHCHANCE_DEFAULT);
		MULTIMOBCHANCE_PROPERTY = chancecontrols.get(Configuration.CATEGORY_GENERAL, ConfigValues.MULTIMOBCHANCE_NAME, ConfigValues.MULTIMOBCHANCE_DEFAULT);
		//Behavior Controls
		DAMAGEFROMSUNLIGHT_PROPERTY = behaviorcontrols.get(Configuration.CATEGORY_GENERAL, ConfigValues.DAMAGEFROMSUNLIGHT_NAME, ConfigValues.DAMAGEFROMSUNLIGHT_DEFAULT);
		DROPEGG_PROPERTY = behaviorcontrols.get(Configuration.CATEGORY_GENERAL, ConfigValues.DROPEGG_NAME, ConfigValues.DROPEGG_DEFAULT);
		EXTRAMOBCOUNT_PROPERTY = behaviorcontrols.get(Configuration.CATEGORY_GENERAL, ConfigValues.EXTRAMOBCOUNT_NAME, ConfigValues.EXTRAMOBCOUNT_DEFAULT);
		MULTIMOBMODE_PROPERTY = behaviorcontrols.get(Configuration.CATEGORY_GENERAL, ConfigValues.MULTIMOBMODE_NAME, ConfigValues.MULTIMOBMODE_DEFAULT);
		REBIRTHFROMNONPLAYER_PROPERTY = behaviorcontrols.get(Configuration.CATEGORY_GENERAL, ConfigValues.REBIRTHFROMNONPLAYER_NAME, ConfigValues.REBIRTHFROMNONPLAYER_DEFAULT);
		//Debug Controls
		VANILLAONLY_PROPERTY = debugcontrols.get(Configuration.CATEGORY_GENERAL, ConfigValues.VANILLAONLY_NAME, ConfigValues.VANILLAONLY_DEFAULT);
		//General
		CUSTOMENTITIES_PROPERTY = general.get(Configuration.CATEGORY_GENERAL, ConfigValues.CUSTOMENTITIES_NAME, ConfigValues.CUSTOMENTITIES_DEFAULT);

		REBIRTHCHANCE_PROPERTY.setMaxValue(1.0);
		REBIRTHCHANCE_PROPERTY.setMinValue(0.0);
		MULTIMOBCHANCE_PROPERTY.setMaxValue(1.0);
		MULTIMOBCHANCE_PROPERTY.setMinValue(0.0);
		if(event.getSide().isClient()) {
			REBIRTHCHANCE_PROPERTY.setConfigEntryClass(RebirthChanceSlider.class);
			MULTIMOBCHANCE_PROPERTY.setConfigEntryClass(RebirthChanceSlider.class);
			CUSTOMENTITIES_PROPERTY.setConfigEntryClass(CustomArrayEntry.class);
		}

		MULTIMOBMODE_PROPERTY.setValidValues(new String[]{"all","continuous","per-mob"});
		transferOldConfig(event.getSuggestedConfigurationFile());
		syncConfig();
		createMobConfigs();
		syncMobConfigs();
	}
	@EventHandler
	public void Init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new CommonEvents());
	}
	private void transferOldConfig(File file){
		if(file.exists()){
			Configuration temp = new Configuration(file);
			ConfigCategory cat = temp.getCategory(Configuration.CATEGORY_GENERAL);
			if(cat.containsKey("mrb1"))
				DROPEGG_PROPERTY.set(!cat.get("mrb1").getBoolean());
			if(cat.containsKey("mrb2"))
				REBIRTHCHANCE_PROPERTY.set(cat.get("mrb2").getDouble());
			if(cat.containsKey("mrb3"))
				ANIMALREBIRTH_PROPERTY.set(cat.get("mrb3").getBoolean());
			if(cat.containsKey("mrb4"))
				EXTRAMOBCOUNT_PROPERTY.set(cat.get("mrb4").getInt());
			if(cat.containsKey("mrb5"))
				REBIRTHFROMNONPLAYER_PROPERTY.set(cat.get("mrb5").getBoolean());
			if(cat.containsKey("mrb6"))
				MULTIMOBCHANCE_PROPERTY.set(cat.get("mrb6").getDouble());
			if(cat.containsKey("mrb7"))
				MULTIMOBMODE_PROPERTY.set(cat.get("mrb7").getString());
			if(cat.containsKey("solar_apocalypse_fix"))
				DAMAGEFROMSUNLIGHT_PROPERTY.set(!cat.get("solar_apocalypse_fix").getBoolean());
			if(cat.containsKey("allowbosses"))
				ALLOWBOSSES_PROPERTY.set(cat.get("allowbosses").getBoolean());
			if(cat.containsKey("allowslimes"))
				ALLOWSLIMES_PROPERTY.set(cat.get("allowslimes").getBoolean());
			if(cat.containsKey("vanillaonly"))
				VANILLAONLY_PROPERTY.set(cat.get("vanillaonly").getBoolean());
			if(file.delete())
				System.out.println("Old config transferred");
		}
	}

	public static void createMobConfigs(){
		if(instance.customProperties)
			for(String mobid:ConfigValues.CUSTOMENTITIES){
				Configuration mobConfig = new Configuration(new File(customConfigDir, mobid+".cfg"));
				mobConfig.load();
				if(!REBIRTHCHANCEMAP.containsKey(mobid))
					REBIRTHCHANCEMAP.put(mobid, mobConfig.get(Configuration.CATEGORY_GENERAL, ConfigValues.REBIRTHCHANCE_NAME, ConfigValues.REBIRTHCHANCE));
				if(!MULTIMOBCHANCEMAP.containsKey(mobid))
					MULTIMOBCHANCEMAP.put(mobid, mobConfig.get(Configuration.CATEGORY_GENERAL, ConfigValues.MULTIMOBCHANCE_NAME, ConfigValues.MULTIMOBCHANCE));
				if(!DROPEGGMAP.containsKey(mobid))
					DROPEGGMAP.put(mobid, mobConfig.get(Configuration.CATEGORY_GENERAL, ConfigValues.DROPEGG_NAME, ConfigValues.DROPEGG));
				if(!EXTRAMOBCOUNTMAP.containsKey(mobid))
					EXTRAMOBCOUNTMAP.put(mobid, mobConfig.get(Configuration.CATEGORY_GENERAL, ConfigValues.EXTRAMOBCOUNT_NAME, ConfigValues.EXTRAMOBCOUNT));
				if(!PLAYERREBIRTHMAP.containsKey(mobid))
					PLAYERREBIRTHMAP.put(mobid, mobConfig.get(Configuration.CATEGORY_GENERAL, ConfigValues.REBIRTHFROMNONPLAYER_NAME, ConfigValues.REBIRTHFROMNONPLAYER));
				if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {//TODO: Ensure that this doesn't crash dedicated servers
					REBIRTHCHANCEMAP.get(mobid).setConfigEntryClass(RebirthChanceSlider.class);
					MULTIMOBCHANCEMAP.get(mobid).setConfigEntryClass(RebirthChanceSlider.class);
				}
				REBIRTHCHANCEMAP.get(mobid).setMaxValue(1.0);
				REBIRTHCHANCEMAP.get(mobid).setMinValue(0.0);
				MULTIMOBCHANCEMAP.get(mobid).setMaxValue(1.0);
				MULTIMOBCHANCEMAP.get(mobid).setMinValue(0.0);
				mobConfigs.put(mobid, mobConfig);
			}
	}

	public static void syncMobConfigs(){
		if(instance.customProperties) {
			ConfigValues.REBIRTHCHANCEMAP.clear();
			ConfigValues.MULTIMOBCHANCEMAP.clear();
			ConfigValues.DROPEGGMAP.clear();
			ConfigValues.EXTRAMOBCOUNTMAP.clear();
			ConfigValues.REBIRTHFROMNONPLAYERMAP.clear();
			for (String mobid : ConfigValues.CUSTOMENTITIES) {
				ConfigValues.REBIRTHCHANCEMAP.put(mobid, REBIRTHCHANCEMAP.get(mobid).getDouble());
				ConfigValues.MULTIMOBCHANCEMAP.put(mobid, MULTIMOBCHANCEMAP.get(mobid).getDouble());
				ConfigValues.DROPEGGMAP.put(mobid, DROPEGGMAP.get(mobid).getBoolean());
				ConfigValues.EXTRAMOBCOUNTMAP.put(mobid, EXTRAMOBCOUNTMAP.get(mobid).getInt());
				ConfigValues.REBIRTHFROMNONPLAYERMAP.put(mobid, PLAYERREBIRTHMAP.get(mobid).getBoolean());
				if(mobConfigs.get(mobid).hasChanged())
					mobConfigs.get(mobid).save();
			}
		}
	}
}

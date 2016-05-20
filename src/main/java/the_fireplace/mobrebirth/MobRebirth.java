package the_fireplace.mobrebirth;

import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import the_fireplace.mobrebirth.config.ConfigValues;
import the_fireplace.mobrebirth.event.CommonEvents;
import the_fireplace.mobrebirth.gui.RebirthChanceSlider;

import java.io.File;
/**
 * @author The_Fireplace
 */
@Mod(modid = MobRebirth.MODID, name = MobRebirth.MODNAME, canBeDeactivated = true, guiFactory = "the_fireplace.mobrebirth.config.MobRebirthGuiFactory", updateJSON = "http://caterpillar.bitnamiapp.com/jsons/mobrebirth.json")
public class MobRebirth {
	public static final String MODID = "mobrebirth";
	public static final String MODNAME = "Mob Rebirth";
	public static String VERSION;
	public static final String curseCode = "226212-mob-rebirth";
	private static final File configDir = new File((File) FMLInjectionData.data()[6], "config/MobRebirth/");

	public static Configuration mobcontrols;
	public static Configuration chancecontrols;
	public static Configuration behaviorcontrols;
	public static Configuration debugcontrols;

	public static Property ALLOWBOSSES_PROPERTY;
	public static Property ALLOWSLIMES_PROPERTY;
	public static Property ANIMALREBIRTH_PROPERTY;

	public static Property REBIRTHCHANCE_PROPERTY;
	public static Property MULTIMOBCHANCE_PROPERTY;

	public static Property DAMAGEFROMSUNLIGHT_PROPERTY;
	public static Property DROPEGG_PROPERTY;
	public static Property EXTRAMOBCOUNT_PROPERTY;
	public static Property MULTIMOBMODE_PROPERTY;
	public static Property REBIRTHFROMNONPLAYER_PROPERTY;

	public static Property VANILLAONLY_PROPERTY;

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
		if(mobcontrols.hasChanged())
			mobcontrols.save();
		if(chancecontrols.hasChanged())
			chancecontrols.save();
		if(behaviorcontrols.hasChanged())
			behaviorcontrols.save();
		if(debugcontrols.hasChanged())
			debugcontrols.save();
	}

	@EventHandler
	public void PreInit(FMLPreInitializationEvent event) {
		String[] version = event.getModMetadata().version.split("\\.");
		if(version[3].equals("BUILDNUMBER"))//Dev environment
			VERSION = event.getModMetadata().version.replace("BUILDNUMBER", "9001");
		else//Released build
			VERSION = event.getModMetadata().version;
		mobcontrols = new Configuration(new File(configDir, "mobcontrols.cfg"));
		chancecontrols = new Configuration(new File(configDir, "chancecontrols.cfg"));
		behaviorcontrols = new Configuration(new File(configDir, "behaviorcontrols.cfg"));
		debugcontrols = new Configuration(new File(configDir, "debugcontrols.cfg"));
		mobcontrols.load();
		chancecontrols.load();
		behaviorcontrols.load();
		debugcontrols.load();
		//Mob Controls
		ALLOWBOSSES_PROPERTY = mobcontrols.get(Configuration.CATEGORY_GENERAL, ConfigValues.ALLOWBOSSES_NAME, ConfigValues.ALLOWBOSSES_DEFAULT, I18n.translateToLocal(ConfigValues.ALLOWBOSSES_NAME+".tooltip"));
		ALLOWSLIMES_PROPERTY = mobcontrols.get(Configuration.CATEGORY_GENERAL, ConfigValues.ALLOWSLIMES_NAME, ConfigValues.ALLOWSLIMES_DEFAULT, I18n.translateToLocal(ConfigValues.ALLOWSLIMES_NAME+".tooltip"));
		ANIMALREBIRTH_PROPERTY = mobcontrols.get(Configuration.CATEGORY_GENERAL, ConfigValues.ANIMALREBIRTH_NAME, ConfigValues.ANIMALREBIRTH_DEFAULT, I18n.translateToLocal(ConfigValues.ANIMALREBIRTH_NAME+".tooltip"));
		//Chance Controls
		REBIRTHCHANCE_PROPERTY = chancecontrols.get(Configuration.CATEGORY_GENERAL, ConfigValues.REBIRTHCHANCE_NAME, ConfigValues.REBIRTHCHANCE_DEFAULT, I18n.translateToLocal(ConfigValues.REBIRTHCHANCE_NAME+".tooltip"));
		MULTIMOBCHANCE_PROPERTY = chancecontrols.get(Configuration.CATEGORY_GENERAL, ConfigValues.MULTIMOBCHANCE_NAME, ConfigValues.MULTIMOBCHANCE_DEFAULT, I18n.translateToLocal(ConfigValues.MULTIMOBCHANCE_NAME+".tooltip"));
		//Behavior Controls
		DAMAGEFROMSUNLIGHT_PROPERTY = behaviorcontrols.get(Configuration.CATEGORY_GENERAL, ConfigValues.DAMAGEFROMSUNLIGHT_NAME, ConfigValues.DAMAGEFROMSUNLIGHT_DEFAULT, I18n.translateToLocal(ConfigValues.DAMAGEFROMSUNLIGHT_NAME+".tooltip"));
		DROPEGG_PROPERTY = behaviorcontrols.get(Configuration.CATEGORY_GENERAL, ConfigValues.DROPEGG_NAME, ConfigValues.DROPEGG_DEFAULT, I18n.translateToLocal(ConfigValues.DROPEGG_NAME+".tooltip"));
		EXTRAMOBCOUNT_PROPERTY = behaviorcontrols.get(Configuration.CATEGORY_GENERAL, ConfigValues.EXTRAMOBCOUNT_NAME, ConfigValues.EXTRAMOBCOUNT_DEFAULT, I18n.translateToLocal(ConfigValues.EXTRAMOBCOUNT_NAME+".tooltip"));
		MULTIMOBMODE_PROPERTY = behaviorcontrols.get(Configuration.CATEGORY_GENERAL, ConfigValues.MULTIMOBMODE_NAME, ConfigValues.MULTIMOBMODE_DEFAULT, I18n.translateToLocal(ConfigValues.MULTIMOBMODE_NAME+".tooltip"));
		REBIRTHFROMNONPLAYER_PROPERTY = behaviorcontrols.get(Configuration.CATEGORY_GENERAL, ConfigValues.REBIRTHFROMNONPLAYER_NAME, ConfigValues.REBIRTHFROMNONPLAYER_DEFAULT, I18n.translateToLocal(ConfigValues.REBIRTHFROMNONPLAYER_NAME+".tooltip"));
		//Debug Controls
		VANILLAONLY_PROPERTY = debugcontrols.get(Configuration.CATEGORY_GENERAL, ConfigValues.VANILLAONLY_NAME, ConfigValues.VANILLAONLY_DEFAULT, I18n.translateToLocal(ConfigValues.VANILLAONLY_NAME+".tooltip"));

		if(event.getSide().isClient())
			REBIRTHCHANCE_PROPERTY.setConfigEntryClass(RebirthChanceSlider.class);
		REBIRTHCHANCE_PROPERTY.setMaxValue(1.0);
		REBIRTHCHANCE_PROPERTY.setMinValue(0.0);
		if(event.getSide().isClient())
			MULTIMOBCHANCE_PROPERTY.setConfigEntryClass(RebirthChanceSlider.class);
		MULTIMOBCHANCE_PROPERTY.setMaxValue(1.0);
		MULTIMOBCHANCE_PROPERTY.setMinValue(0.0);

		MULTIMOBMODE_PROPERTY.setValidValues(new String[]{"all","continuous","per-mob"});
		transferOldConfig(event.getSuggestedConfigurationFile());
		syncConfig();
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
}

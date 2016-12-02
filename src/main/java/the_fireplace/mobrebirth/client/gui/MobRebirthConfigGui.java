package the_fireplace.mobrebirth.client.gui;

import cpw.mods.fml.client.config.DummyConfigElement;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.IConfigElement;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import the_fireplace.mobrebirth.MobRebirth;
import the_fireplace.mobrebirth.common.ConfigValues;

import java.util.ArrayList;
import java.util.List;
/**
 * @author The_Fireplace
 */
public class MobRebirthConfigGui extends GuiConfig {

	public MobRebirthConfigGui(GuiScreen parentScreen) {
		super(parentScreen,
				getConfigElements(), MobRebirth.MODID, false,
				false, GuiConfig.getAbridgedConfigPath(MobRebirth.mobcontrols.toString()));
	}
	public static List<IConfigElement> getConfigElements(){
		List<IConfigElement> list = new ArrayList<>();
		list.add(new DummyConfigElement.DummyCategoryElement("mobCfg", "mobCfg", MobEntry.class));
		list.add(new DummyConfigElement.DummyCategoryElement("chanceCfg", "chanceCfg", ChanceEntry.class));
		list.add(new DummyConfigElement.DummyCategoryElement("behaviorCfg", "behaviorCfg", BehaviorEntry.class));
		list.add(new DummyConfigElement.DummyCategoryElement("debugCfg", "debugCfg", DebugEntry.class));
		list.addAll(new ConfigElement(MobRebirth.general.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements());
		if(MobRebirth.instance.getHasCustomMobSettings())
			list.add(new DummyConfigElement.DummyCategoryElement("customMobs", "customMobs", CustomMobEntry.class));
		return list;
	}
	public static class CustomMobEntry extends GuiConfigEntries.CategoryEntry {

		public CustomMobEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
			super(owningScreen, owningEntryList, configElement);
		}
		@Override
		protected GuiScreen buildChildScreen(){
			return new GuiConfig(owningScreen,
					getConfigElements(), MobRebirth.MODID, false,
					false, I18n.format("customMobs.tooltip"));
		}
		public static List<IConfigElement> getConfigElements(){
			List<IConfigElement> list = new ArrayList<>();
			for(String mobstring:ConfigValues.CUSTOMENTITIES){
				list.add(new DummyConfigElement.DummyCategoryElement(mobstring, mobstring, new ConfigElement(MobRebirth.mobConfigs.get(mobstring).getCategory(Configuration.CATEGORY_GENERAL)).getChildElements()));
			}
			return list;
		}
	}
	public static class MobEntry extends GuiConfigEntries.CategoryEntry {

		public MobEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
			super(owningScreen, owningEntryList, configElement);
		}
		@Override
		protected GuiScreen buildChildScreen(){
			return new GuiConfig(owningScreen,
					new ConfigElement(MobRebirth.mobcontrols.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(), MobRebirth.MODID, false,
					false, GuiConfig.getAbridgedConfigPath(MobRebirth.mobcontrols.toString()));
		}
	}
	public static class ChanceEntry extends GuiConfigEntries.CategoryEntry {

		public ChanceEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
			super(owningScreen, owningEntryList, configElement);
		}
		@Override
		protected GuiScreen buildChildScreen(){
			return new GuiConfig(owningScreen,
					new ConfigElement(MobRebirth.chancecontrols.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(), MobRebirth.MODID, false,
					false, GuiConfig.getAbridgedConfigPath(MobRebirth.chancecontrols.toString()));
		}
	}
	public static class BehaviorEntry extends GuiConfigEntries.CategoryEntry {

		public BehaviorEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
			super(owningScreen, owningEntryList, configElement);
		}
		@Override
		protected GuiScreen buildChildScreen(){
			return new GuiConfig(owningScreen,
					new ConfigElement(MobRebirth.behaviorcontrols.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(), MobRebirth.MODID, false,
					false, GuiConfig.getAbridgedConfigPath(MobRebirth.behaviorcontrols.toString()));
		}
	}
	public static class DebugEntry extends GuiConfigEntries.CategoryEntry {

		public DebugEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
			super(owningScreen, owningEntryList, configElement);
		}
		@Override
		protected GuiScreen buildChildScreen(){
			return new GuiConfig(owningScreen,
					new ConfigElement(MobRebirth.debugcontrols.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(), MobRebirth.MODID, false,
					false, GuiConfig.getAbridgedConfigPath(MobRebirth.debugcontrols.toString()));
		}
	}
}

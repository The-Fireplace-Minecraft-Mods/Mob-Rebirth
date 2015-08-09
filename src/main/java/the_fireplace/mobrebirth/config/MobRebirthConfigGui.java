package the_fireplace.mobrebirth.config;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiConfigEntries.CategoryEntry;
import net.minecraftforge.fml.client.config.IConfigElement;
import the_fireplace.mobrebirth.MobRebirth;
/**
 *
 * @author The_Fireplace
 *
 */
public class MobRebirthConfigGui extends GuiConfig {

	public MobRebirthConfigGui(GuiScreen parentScreen) {
		super(parentScreen,
				getConfigElements(), MobRebirth.MODID, false,
				false, GuiConfig.getAbridgedConfigPath(MobRebirth.mobcontrols.toString()));
	}
	public static List<IConfigElement> getConfigElements(){
		List<IConfigElement> list = new ArrayList<IConfigElement>();
		list.add(new DummyCategoryElement("mobCfg", "mobCfg", MobEntry.class));
		list.add(new DummyCategoryElement("chanceCfg", "chanceCfg", ChanceEntry.class));
		list.add(new DummyCategoryElement("behaviorCfg", "behaviorCfg", BehaviorEntry.class));
		list.add(new DummyCategoryElement("debugCfg", "debugCfg", DebugEntry.class));
		return list;
	}
	public static class MobEntry extends CategoryEntry{

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
	public static class ChanceEntry extends CategoryEntry{

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
	public static class BehaviorEntry extends CategoryEntry{

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
	public static class DebugEntry extends CategoryEntry{

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

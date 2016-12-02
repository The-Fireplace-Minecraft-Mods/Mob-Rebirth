package the_fireplace.mobrebirth.client.gui;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.GuiSlider;
import cpw.mods.fml.client.config.IConfigElement;

/**
 * @author The_Fireplace
 */
public class RebirthChanceSlider extends GuiConfigEntries.NumberSliderEntry {

	public RebirthChanceSlider(GuiConfig owningScreen,
							   GuiConfigEntries owningEntryList, IConfigElement configElement) {
		super(owningScreen, owningEntryList, configElement);
		((GuiSlider) this.btnValue).precision=2;
		((GuiSlider) this.btnValue).updateSlider();
	}
}

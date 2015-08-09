package the_fireplace.mobrebirth.gui;

import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiConfigEntries.NumberSliderEntry;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.client.config.IConfigElement;
/**
 * 
 * @author The_Fireplace
 *
 */
public class RebirthChanceSlider extends NumberSliderEntry {

	public RebirthChanceSlider(GuiConfig owningScreen,
			GuiConfigEntries owningEntryList, IConfigElement configElement) {
		super(owningScreen, owningEntryList, configElement);
		((GuiSlider) this.btnValue).precision=2;
		((GuiSlider) this.btnValue).updateSlider();
	}
}

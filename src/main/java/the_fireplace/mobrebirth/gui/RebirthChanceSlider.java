package the_fireplace.mobrebirth.gui;

import java.math.RoundingMode;

import java.math.BigDecimal;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.client.config.GuiConfigEntries.NumberSliderEntry;
import net.minecraftforge.fml.client.config.IConfigElement;

public class RebirthChanceSlider extends NumberSliderEntry {

	public RebirthChanceSlider(GuiConfig owningScreen,
			GuiConfigEntries owningEntryList, IConfigElement configElement) {
		super(owningScreen, owningEntryList, configElement);
	}
	@Override
    public void updateValueButtonText()
    {
		//try limiting the number displayed here
		//this.btnValue.displayString = String.valueOf(round(Double.parseDouble(this.btnValue.displayString), 2));
        ((GuiSlider) this.btnValue).updateSlider();
    }
}

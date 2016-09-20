package the_fireplace.mobrebirth.client.gui;

import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiEditArray;
import net.minecraftforge.fml.client.config.IConfigElement;

/**
 * @author The_Fireplace
 */
public class CustomArrayEntry extends GuiConfigEntries.ArrayEntry {
    public CustomArrayEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
        super(owningScreen, owningEntryList, configElement);
    }
    @Override
    public void valueButtonPressed(int slotIndex)
    {
        mc.displayGuiScreen(new GuiMobList(this.owningScreen, configElement, slotIndex, currentValues, enabled()));
    }
}

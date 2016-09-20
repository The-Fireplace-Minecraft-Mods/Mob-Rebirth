package the_fireplace.mobrebirth.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityList;
import net.minecraftforge.fml.client.config.GuiEditArray;
import net.minecraftforge.fml.client.config.IConfigElement;
import the_fireplace.mobrebirth.MobRebirth;

/**
 * @author The_Fireplace
 */
public class GuiMobList extends GuiEditArray {
    public GuiMobList(GuiScreen parentScreen, IConfigElement configElement, int slotIndex, Object[] currentValues, boolean enabled) {
        super(parentScreen, configElement, slotIndex, currentValues, enabled);
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.id == 2000)
        {
            //Cancel if mob is invalid
            for(Object mob:configElement.getList()){
                if(mob instanceof String){
                    if(!EntityList.isStringValidEntityName((String)mob))
                        this.drawCenteredString(fontRendererObj, I18n.format("invalidmob"), this.height/2, this.width/2, -1);
                }
            }
            super.actionPerformed(button);
            MobRebirth.createMobConfigs();
        }
    }
}

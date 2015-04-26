package the_fireplace.mobrebirth.config;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import the_fireplace.mobrebirth.MobRebirth;
/**
 * 
 * @author The_Fireplace
 *
 */
public class MobRebirthConfigGui extends GuiConfig {

	public MobRebirthConfigGui(GuiScreen parentScreen) {
		super(parentScreen, 
				new ConfigElement(MobRebirth.file.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(), MobRebirth.MODID, false,
				false, GuiConfig.getAbridgedConfigPath(MobRebirth.file.toString()));
	}

}

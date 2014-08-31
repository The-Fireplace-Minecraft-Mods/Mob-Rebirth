package the_fireplace.mobrebirth.config;

import java.util.List;

import the_fireplace.mobrebirth.ModBase;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;

public class MobRebirthConfigGui extends GuiConfig {

	public MobRebirthConfigGui(GuiScreen parentScreen) {
		super(parentScreen, 
				new ConfigElement(ModBase.file.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(), "mobrebirth", false,
				false, GuiConfig.getAbridgedConfigPath(ModBase.file.toString()));
	}

}

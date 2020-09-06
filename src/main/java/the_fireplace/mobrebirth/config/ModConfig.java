package the_fireplace.mobrebirth.config;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import the_fireplace.mobrebirth.MobRebirth;

@Config(name = MobRebirth.MODID)
public class ModConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    public boolean allowBosses = false;
    @ConfigEntry.Gui.Tooltip
    public boolean allowSlimes = true;
    @ConfigEntry.Gui.Tooltip
    public boolean allowAnimals = true;
    @ConfigEntry.Gui.Tooltip
    public boolean vanillaMobsOnly = false;
}

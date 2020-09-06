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
    public double rebirthChance = 0.1;
    @ConfigEntry.Gui.Tooltip
    public double multiMobChance = 0.01;
    @ConfigEntry.Gui.Tooltip
    public String multiMobMode = "continuous";
    @ConfigEntry.Gui.Tooltip
    public int multiMobCount = 1;
    @ConfigEntry.Gui.Tooltip
    public boolean rebornAsEggs = false;
    @ConfigEntry.Gui.Tooltip
    public boolean rebirthFromNonPlayer = true;
    @ConfigEntry.Gui.Tooltip
    public boolean damageFromSunlight = true;
    @ConfigEntry.Gui.Tooltip
    public boolean vanillaMobsOnly = false;
}

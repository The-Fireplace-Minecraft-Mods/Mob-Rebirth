package the_fireplace.mobrebirth.config;

import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.Jankson;
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.JsonGrammar;
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.JsonObject;
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.impl.SyntaxError;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ModConfig {
    private static final File configDir = new File("config");
    private static final File baseConfigFile = new File(configDir, "mobrebirth.json5");

    public boolean allowBosses = false;
    public boolean allowSlimes = true;
    public boolean allowAnimals = true;
    public boolean vanillaMobsOnly = false;
    public boolean compactCustomMobConfigs = true;

    public void save() {
        try {
            FileWriter fw = new FileWriter(baseConfigFile);
            fw.write(Jankson.builder().build().toJson(this).toJson(JsonGrammar.JSON5));
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("ConstantConditions")
    public static ModConfig load() {
        JsonObject obj;
        ModConfig conf = new ModConfig();
        try {
            obj = Jankson.builder().build().load(baseConfigFile);
        } catch (IOException | SyntaxError | NullPointerException e) {
            e.printStackTrace();
            return conf;
        }
        if(obj.containsKey("allowBosses"))
            conf.allowBosses = obj.get(Boolean.class, "allowBosses");
        if(obj.containsKey("allowSlimes"))
            conf.allowSlimes = obj.get(Boolean.class, "allowSlimes");
        if(obj.containsKey("allowAnimals"))
            conf.allowAnimals = obj.get(Boolean.class, "allowAnimals");
        if(obj.containsKey("vanillaMobsOnly"))
            conf.vanillaMobsOnly = obj.get(Boolean.class, "vanillaMobsOnly");
        if(obj.containsKey("compactCustomMobConfigs"))
            conf.compactCustomMobConfigs = obj.get(Boolean.class, "compactCustomMobConfigs");
        return conf;
    }
}

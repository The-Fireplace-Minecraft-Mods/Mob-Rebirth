package the_fireplace.mobrebirth.config;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.Jankson;
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.JsonGrammar;
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.JsonObject;
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.impl.SyntaxError;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public class MobSettingsManager {
    private static MobSettings defaultSettings = null;
    private static final Map<Identifier, MobSettings> mobSettings = Maps.newHashMap();

    private static final File mobSettingsDir = new File("config/mobrebirth");

    public static MobSettings getSettings(LivingEntity entity) {
        return mobSettings.getOrDefault(Registry.ENTITY_TYPE.getId(entity.getType()), defaultSettings);
    }

    public static MobSettings getDefaultSettings() {
        return defaultSettings;
    }

    public static void init() {
        //noinspection ResultOfMethodCallIgnored
        mobSettingsDir.mkdirs();
        loadDefaultSettings();
        populateMap();
    }

    public static void saveAll() {
        writeSettings(defaultSettings, new File(mobSettingsDir, "default.json5"));
        //TODO once config GUI allows altering other settings, go through and save all of them.
        // When saving each, only write options that are different from the default, to save space. Perhaps add a config for this behavior.
    }

    private static void loadDefaultSettings() {
        //noinspection ConstantConditions
        defaultSettings = loadSettings(null, new File(mobSettingsDir, "default.json5"));
        if(defaultSettings == null)
            defaultSettings = new MobSettings();
        writeSettings(defaultSettings, new File(mobSettingsDir, "default.json5"));
    }

    private static void populateMap() {
        File[] files = mobSettingsDir.listFiles();
        if(files != null)
            for(File file: files) {
                if(file.isDirectory()) {
                    File[] dirFiles = file.listFiles();
                    if(dirFiles != null)
                        for(File file2: dirFiles)
                            if(Files.getFileExtension(file2.getName()).equalsIgnoreCase("json5")) {
                                Identifier id = getIdentifier(file.getName(), file2);
                                if(id != null)
                                    mobSettings.put(id, loadSettings(id, file2));
                            }
                } else {
                    if(Files.getFileExtension(file.getName()).equalsIgnoreCase("json5")) {
                        Identifier id = getIdentifier(null, file);
                        if(id != null)
                            mobSettings.put(id, loadSettings(id, file));
                    }
                }
            }
    }

    @Nullable
    private static Identifier getIdentifier(@Nullable String folder, File file) {
        try {
            JsonObject obj = Jankson.builder().build().load(file);
            if(obj.containsKey("id")) {
                String id = obj.get(String.class, "id");
                return id != null ? new Identifier(id) : null;
            }
            String id = Files.getNameWithoutExtension(file.getName());
            return folder != null ? new Identifier(folder, id) : new Identifier(id);
        } catch (IOException | SyntaxError e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("ConstantConditions")
    private static MobSettings loadSettings(Identifier id, File file) {
        JsonObject obj;
        try {
            obj = Jankson.builder().build().load(file);
        } catch (IOException | SyntaxError | NullPointerException e) {
            e.printStackTrace();
            return null;
        }
        MobSettings settings = mobSettings.getOrDefault(id, defaultSettings);
        if(settings == null)
            settings = new MobSettings();
        else
            settings = settings.clone();

        if(obj.containsKey("id"))
            settings.id = obj.get(String.class, "id");
        else
            settings.id = id.toString();
        if(obj.containsKey("enabled"))
            settings.enabled = obj.get(Boolean.class, "enabled");
        if(obj.containsKey("rebirthChance"))
            settings.rebirthChance = obj.get(Double.class, "rebirthChance");
        if(obj.containsKey("multiMobChance"))
            settings.multiMobChance = obj.get(Double.class, "multiMobChance");
        if(obj.containsKey("multiMobMode"))
            settings.multiMobMode = obj.get(String.class, "multiMobMode");
        if(obj.containsKey("multiMobCount"))
            settings.multiMobCount = obj.get(Integer.class, "multiMobCount");
        if(obj.containsKey("rebornAsEggs"))
            settings.rebornAsEggs = obj.get(Boolean.class, "rebornAsEggs");
        if(obj.containsKey("rebirthFromPlayer"))
            settings.rebirthFromPlayer = obj.get(Boolean.class, "rebirthFromPlayer");
        if(obj.containsKey("rebirthFromNonPlayer"))
            settings.rebirthFromNonPlayer = obj.get(Boolean.class, "rebirthFromNonPlayer");
        if(obj.containsKey("preventSunlightDamage"))
            settings.preventSunlightDamage = obj.get(Boolean.class, "preventSunlightDamage");
        if(obj.containsKey("biomeList"))
            settings.biomeList = obj.get(List.class, "biomeList");
        if(obj.containsKey("rebornMobWeights"))
            settings.rebornMobWeights = obj.get(Map.class, "rebornMobWeights");
        return settings;
    }

    private static void writeSettings(MobSettings settings, File file) {
        JsonObject obj = new JsonObject();
        if(settings.enabled != null)
            obj.putDefault("enabled", settings.enabled, "This option can be used to force a mob to be enabled/disabled, regardless of the general settings.");
        if(settings.id != null)
            obj.putDefault("id", settings.id, "Set the mob id these settings apply to. Leave empty to check the filename for it. You generally don't want to touch this in default.");
        if(settings.rebirthChance != null)
            obj.putDefault("rebirthChance", settings.rebirthChance, "1.0=100%. 0.5=50%");
        if(settings.multiMobChance != null)
            obj.putDefault("multiMobChance", settings.multiMobChance, "1.0=100%. 0.5=50%");
        if(settings.multiMobMode != null)
            obj.putDefault("multiMobMode", settings.multiMobMode, "Options are 'continuous', 'per-mob', or 'all'.\r\n'Continuous' applies the chance to each extra mob, and stops when one doesn't spawn\r\n'Per-Mob' applies the chance to each extra mob\r\n'All' applies the chance once.");
        if(settings.multiMobCount != null)
            obj.putDefault("multiMobCount", settings.multiMobCount, "How many extra mobs to spawn. This does not include the initial reborn mob.");
        if(settings.rebornAsEggs != null)
            obj.putDefault("rebornAsEggs", settings.rebornAsEggs, "Should the mob drop a spawn egg instead of being fully reborn?");
        if(settings.rebirthFromPlayer != null)
            obj.putDefault("rebirthFromPlayer", settings.rebirthFromPlayer, "Should the mob be reborn when killed by a player?");
        if(settings.rebirthFromNonPlayer != null)
            obj.putDefault("rebirthFromNonPlayer", settings.rebirthFromNonPlayer, "Should the mob be reborn when killed by a non-player?");
        if(settings.preventSunlightDamage != null)
            obj.putDefault("preventSunlightDamage", settings.preventSunlightDamage, "Prevent sunlight damage to the undead. Protects against the sunlight apocalypse in some scenarios.");
        if(settings.biomeList != null)
            obj.putDefault("biomeList", settings.biomeList, "Biome list for rebirth. If it contains \"*\" it is a blocklist, otherwise it is an allowlist.");
        if(settings.rebornMobWeights != null)
            obj.putDefault("rebornMobWeights", settings.rebornMobWeights, "Weighted list of mob ids that can spawn from this one. An empty id means the current mob's id will be used.");
        try {
            FileWriter fw = new FileWriter(file);
            fw.write(obj.toJson(JsonGrammar.JSON5));
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

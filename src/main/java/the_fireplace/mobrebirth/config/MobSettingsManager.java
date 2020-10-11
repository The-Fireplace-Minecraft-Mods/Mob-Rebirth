package the_fireplace.mobrebirth.config;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonGrammar;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import blue.endless.jankson.impl.SyntaxError;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import the_fireplace.mobrebirth.MobRebirth;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public final class MobSettingsManager {
    private static MobSettings defaultSettings = null;
    private static final Map<Identifier, MobSettings> MOB_SETTINGS = Maps.newHashMap();

    private static final File MOB_SETTINGS_DIR = new File("config/mobrebirth");
    private static final File DEFAULT_SETTINGS_FILE = new File(MOB_SETTINGS_DIR, "default.json5");

    public static MobSettings getSettings(LivingEntity entity) {
        return getSettings(Registry.ENTITY_TYPE.getId(entity.getType()), false);
    }
    public static MobSettings getSettings(Identifier identifier, boolean clone) {
        return clone ? MOB_SETTINGS.getOrDefault(identifier, defaultSettings).clone() : MOB_SETTINGS.getOrDefault(identifier, defaultSettings);
    }

    public static Collection<Identifier> getCustomIds() {
        return MOB_SETTINGS.keySet();
    }
    public static Collection<MobSettings> getAllSettings() {
        return MOB_SETTINGS.values();
    }

    public static MobSettings getDefaultSettings() {
        return defaultSettings;
    }

    public static void init() {
        //noinspection ResultOfMethodCallIgnored
        MOB_SETTINGS_DIR.mkdirs();
        loadDefaultSettings();
        populateMap();
    }

    public static void saveAll() {
        writeSettings(defaultSettings, DEFAULT_SETTINGS_FILE, false);
        for(MobSettings mobSettings: MOB_SETTINGS.values())
            writeSettings(mobSettings, mobSettings.file, MobRebirth.config.compactCustomMobConfigs);
    }

    private static void loadDefaultSettings() {
        defaultSettings = loadSettings(null, DEFAULT_SETTINGS_FILE);
        if(defaultSettings == null) {
            defaultSettings = new MobSettings();
            defaultSettings.file = DEFAULT_SETTINGS_FILE;
        }
        writeSettings(defaultSettings, DEFAULT_SETTINGS_FILE, false);
    }

    private static void populateMap() {
        File[] files = MOB_SETTINGS_DIR.listFiles();
        if(files != null)
            for(File file: files) {
                if(file.isDirectory()) {
                    File[] dirFiles = file.listFiles();
                    if(dirFiles != null)
                        for(File file2: dirFiles)
                            if(Files.getFileExtension(file2.getName()).equalsIgnoreCase("json5")) {
                                Identifier id = getIdentifier(file.getName(), file2);
                                if(id != null)
                                    MOB_SETTINGS.put(id, loadSettings(id, file2));
                            }
                } else {
                    if(Files.getFileExtension(file.getName()).equalsIgnoreCase("json5")) {
                        Identifier id = getIdentifier(null, file);
                        if(id != null)
                            MOB_SETTINGS.put(id, loadSettings(id, file));
                    }
                }
            }
    }

    @Nullable
    public static Identifier getIdentifier(@Nullable String folder, File file) {
        try {
            JsonObject obj = Jankson.builder().build().load(file);
            if(obj.containsKey("id")) {
                String id = obj.get(String.class, "id");
                if(id == null || !id.isEmpty())//empty id should be folder based
                    return id != null ? new Identifier(id) : null;
            }
            String id = Files.getNameWithoutExtension(file.getName());
            if(id.equalsIgnoreCase("default"))
                return null;
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
        MobSettings settings = MOB_SETTINGS.getOrDefault(id, defaultSettings);
        if(settings == null)
            settings = new MobSettings();
        else
            settings = settings.clone();

        settings.file = file;
        if(obj.containsKey("id") && !obj.get(String.class, "id").isEmpty())
            settings.id = obj.get(String.class, "id");
        else if(id != null)
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
            settings.biomeList = Lists.newArrayList(obj.get(String[].class, "biomeList"));
        if(obj.containsKey("rebornMobWeights")) {
            //It gets deserialized into a JsonObject, so convert it to a HashMap then convert the values from JsonPrimitive to Integer
            //noinspection unchecked
            settings.rebornMobWeights = Maps.newHashMap(obj.get(Map.class, "rebornMobWeights"));
            for(Map.Entry<?, ?> entry: Sets.newHashSet(settings.rebornMobWeights.entrySet()))
                if(entry.getValue().getClass().isAssignableFrom(JsonPrimitive.class))
                    settings.rebornMobWeights.put((String) entry.getKey(), Integer.parseInt(entry.getValue().toString()));
        }
        return settings;
    }

    public static MobSettings createSettings(Identifier id) {
        MobSettings settings = getSettings(id, true);
        File domainFolder = new File(MOB_SETTINGS_DIR, id.getNamespace());
        if(!domainFolder.exists())
            if(!domainFolder.mkdir()) {
                MobRebirth.LOGGER.error("Unable to make domain folder for "+id.toString());
                return null;
            }
        File targetFile = new File(domainFolder, id.getPath()+".json5");
        settings.file = targetFile;
        writeSettings(settings, targetFile, MobRebirth.config.compactCustomMobConfigs);

        MOB_SETTINGS.put(id, settings);
        return settings;
    }

    public static void deleteSettings(Identifier id, MobSettings settings) {
        settings.file.delete();
        MOB_SETTINGS.remove(id);
    }

    private static void writeSettings(MobSettings settings, File file, boolean compact) {
        JsonObject obj = new JsonObject();
        if(settings.enabled != null && (!compact || settings.enabled != defaultSettings.enabled))
            obj.putDefault("enabled", settings.enabled, "This option can be used to force a mob to be enabled/disabled, regardless of the general settings.");
        if(!compact || !settings.id.equals(defaultSettings.id))
            obj.putDefault("id", settings.id, "Set the mob id these settings apply to. Leave empty to check the filename for it. You generally don't want to touch this in default.");
        if(!compact || !settings.rebirthChance.equals(defaultSettings.rebirthChance))
            obj.putDefault("rebirthChance", settings.rebirthChance, "1.0=100%. 0.5=50%");
        if(!compact || !settings.multiMobChance.equals(defaultSettings.multiMobChance))
            obj.putDefault("multiMobChance", settings.multiMobChance, "1.0=100%. 0.5=50%");
        if(!compact || !settings.multiMobMode.equalsIgnoreCase(defaultSettings.multiMobMode))
            obj.putDefault("multiMobMode", settings.multiMobMode, "Options are 'continuous', 'per-mob', or 'all'.\r\n'Continuous' applies the chance to each extra mob, and stops when one doesn't spawn\r\n'Per-Mob' applies the chance to each extra mob\r\n'All' applies the chance once.");
        if(!compact || !settings.multiMobCount.equals(defaultSettings.multiMobCount))
            obj.putDefault("multiMobCount", settings.multiMobCount, "How many extra mobs to spawn. This does not include the initial reborn mob.");
        if(!compact || !settings.rebornAsEggs.equals(defaultSettings.rebornAsEggs))
            obj.putDefault("rebornAsEggs", settings.rebornAsEggs, "Should the mob drop a spawn egg instead of being fully reborn?");
        if(!compact || !settings.rebirthFromPlayer.equals(defaultSettings.rebirthFromPlayer))
            obj.putDefault("rebirthFromPlayer", settings.rebirthFromPlayer, "Should the mob be reborn when killed by a player?");
        if(!compact || !settings.rebirthFromNonPlayer.equals(defaultSettings.rebirthFromNonPlayer))
            obj.putDefault("rebirthFromNonPlayer", settings.rebirthFromNonPlayer, "Should the mob be reborn when killed by a non-player?");
        if(!compact || !settings.preventSunlightDamage.equals(defaultSettings.preventSunlightDamage))
            obj.putDefault("preventSunlightDamage", settings.preventSunlightDamage, "Prevent sunlight damage to the undead. Protects against the sunlight apocalypse in some scenarios.");
        if(!compact || !settings.biomeList.equals(defaultSettings.biomeList))
            obj.putDefault("biomeList", settings.biomeList, "Biome list for rebirth. If it contains \"*\" it is a blocklist, otherwise it is an allowlist.");
        if(!compact || !settings.rebornMobWeights.equals(defaultSettings.rebornMobWeights))
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

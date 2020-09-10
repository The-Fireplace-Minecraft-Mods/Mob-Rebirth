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

    public static void init() {
        //noinspection ResultOfMethodCallIgnored
        mobSettingsDir.mkdirs();
        loadDefaultSettings();
        populateMap();
    }

    private static void loadDefaultSettings() {
        //noinspection ConstantConditions
        defaultSettings = loadSettings(null, new File(mobSettingsDir, "default.json5"));
        if(defaultSettings == null)
            defaultSettings = new MobSettings();
        writeSettings(defaultSettings, new File(mobSettingsDir, "default.json5"));
    }

    private static void populateMap() {
        //TODO data pack support?
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
        try {
            JsonObject obj = Jankson.builder().build().load(file);
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
                settings.rebirthChance = obj.get(Double.class, "enabled");
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
        } catch (IOException | SyntaxError | NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void writeSettings(MobSettings settings, File file) {
        JsonObject obj = new JsonObject();
        if(settings.enabled != null)
            obj.putDefault("enabled", settings.enabled, null);
        if(settings.id != null)
            obj.putDefault("id", settings.id, null);
        if(settings.rebirthChance != null)
            obj.putDefault("rebirthChance", settings.rebirthChance, null);
        if(settings.multiMobChance != null)
            obj.putDefault("multiMobChance", settings.multiMobChance, null);
        if(settings.multiMobMode != null)
            obj.putDefault("multiMobMode", settings.multiMobMode, null);
        if(settings.multiMobCount != null)
            obj.putDefault("multiMobCount", settings.multiMobCount, null);
        if(settings.rebornAsEggs != null)
            obj.putDefault("rebornAsEggs", settings.rebornAsEggs, null);
        if(settings.rebirthFromPlayer != null)
            obj.putDefault("rebirthFromPlayer", settings.rebirthFromPlayer, null);
        if(settings.rebirthFromNonPlayer != null)
            obj.putDefault("rebirthFromNonPlayer", settings.rebirthFromNonPlayer, null);
        if(settings.preventSunlightDamage != null)
            obj.putDefault("preventSunlightDamage", settings.preventSunlightDamage, null);
        if(settings.biomeList != null)
            obj.putDefault("biomeList", settings.biomeList, null);
        if(settings.rebornMobWeights != null)
            obj.putDefault("rebornMobWeights", settings.rebornMobWeights, null);
        try {
            FileWriter fw = new FileWriter(file);
            fw.write(obj.toJson(JsonGrammar.JSON5));
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

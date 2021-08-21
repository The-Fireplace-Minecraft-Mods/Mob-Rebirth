package the_fireplace.mobrebirth.config;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.*;
import dev.the_fireplace.annotateddi.api.DIContainer;
import dev.the_fireplace.lib.api.io.injectables.JsonFileReader;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import the_fireplace.mobrebirth.MobRebirthConstants;
import the_fireplace.mobrebirth.domain.config.ConfigValues;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.util.*;

@SuppressWarnings("UnstableApiUsage")
@Singleton
public final class MobSettingsManager {

    @Nullable
    private MobSettings defaultSettings = null;
    private final Map<Identifier, MobSettings> MOB_SETTINGS = Maps.newHashMap();

    private final File MOB_SETTINGS_DIR = new File("config/mobrebirth");
    private final File DEFAULT_SETTINGS_FILE = new File(MOB_SETTINGS_DIR, "default.json");
    
    private final ConfigValues configValues;

    @Inject
    public MobSettingsManager(ConfigValues configValues) {
        this.configValues = configValues;
    }

    public MobSettings getSettings(LivingEntity entity) {
        return getSettings(Registry.ENTITY_TYPE.getId(entity.getType()), false);
    }
    public MobSettings getSettings(Identifier identifier, boolean clone) {
        return clone ? MOB_SETTINGS.getOrDefault(identifier, defaultSettings).clone() : MOB_SETTINGS.getOrDefault(identifier, defaultSettings);
    }

    public Collection<Identifier> getCustomIds() {
        return MOB_SETTINGS.keySet();
    }
    public Collection<MobSettings> getAllSettings() {
        return MOB_SETTINGS.values();
    }

    public MobSettings getDefaultSettings() {
        if (defaultSettings == null) {
            throw new IllegalStateException("Attempted to access default mob settings before they were initialized!");
        }
        return defaultSettings;
    }

    public void init() {
        //noinspection ResultOfMethodCallIgnored
        MOB_SETTINGS_DIR.mkdirs();
        loadDefaultSettings();
        populateMap();
    }

    public void saveAll() {
        writeSettings(getDefaultSettings(), DEFAULT_SETTINGS_FILE, false);
        for (MobSettings mobSettings: MOB_SETTINGS.values()) {
            writeSettings(mobSettings, mobSettings.file, configValues.getUseCompactCustomMobConfigs());
        }
    }

    private void loadDefaultSettings() {
        defaultSettings = loadSettings(null, DEFAULT_SETTINGS_FILE);
        if (defaultSettings == null) {
            defaultSettings = new MobSettings();
            defaultSettings.file = DEFAULT_SETTINGS_FILE;
        }
        writeSettings(defaultSettings, DEFAULT_SETTINGS_FILE, false);
    }

    private void populateMap() {
        File[] files = MOB_SETTINGS_DIR.listFiles();
        if(files != null)
            for(File file: files) {
                if(file.isDirectory()) {
                    File[] dirFiles = file.listFiles();
                    if(dirFiles != null)
                        for(File file2: dirFiles)
                            if(Files.getFileExtension(file2.getName()).equalsIgnoreCase("json")) {
                                Identifier id = getIdentifier(file.getName(), file2);
                                if(id != null)
                                    MOB_SETTINGS.put(id, loadSettings(id, file2));
                            }
                } else {
                    if(Files.getFileExtension(file.getName()).equalsIgnoreCase("json")) {
                        Identifier id = getIdentifier(null, file);
                        if(id != null)
                            MOB_SETTINGS.put(id, loadSettings(id, file));
                    }
                }
            }
    }

    @Nullable
    public Identifier getIdentifier(@Nullable String folder, File file) {
        try {
            JsonObject obj = new JsonObject();
            if(obj.has("id")) {
                String id = obj.get("id").getAsString();
                if(id == null || !id.isEmpty())//empty id should be folder based
                    return id != null ? new Identifier(id) : null;
            }
            String id = Files.getNameWithoutExtension(file.getName());
            if(id.equalsIgnoreCase("default"))
                return null;
            return folder != null ? new Identifier(folder, id) : new Identifier(id);
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    private MobSettings loadSettings(Identifier id, File file) {
        JsonObject obj = DIContainer.get().getInstance(JsonFileReader.class).readJsonFile(file);
        if (obj == null) {
            return null;
        }
        MobSettings settings = MOB_SETTINGS.getOrDefault(id, defaultSettings);
        if (settings == null) {
            settings = new MobSettings();
        } else {
            settings = settings.clone();
        }

        settings.file = file;
        if(obj.has("id") && !obj.get("id").getAsString().isEmpty())
            settings.id = obj.get("id").getAsString();
        else if(id != null)
            settings.id = id.toString();
        if(obj.has("enabled"))
            settings.enabled = obj.get("enabled").getAsBoolean();
        if(obj.has("rebirthChance"))
            settings.rebirthChance = obj.get("rebirthChance").getAsDouble();
        if(obj.has("multiMobChance"))
            settings.multiMobChance = obj.get("multiMobChance").getAsDouble();
        if(obj.has("multiMobMode"))
            settings.multiMobMode = obj.get("multiMobMode").getAsString();
        if(obj.has("multiMobCount"))
            settings.multiMobCount = obj.get("multiMobCount").getAsInt();
        if(obj.has("rebornAsEggs"))
            settings.rebornAsEggs = obj.get("rebornAsEggs").getAsBoolean();
        if(obj.has("rebirthFromPlayer"))
            settings.rebirthFromPlayer = obj.get("rebirthFromPlayer").getAsBoolean();
        if(obj.has("rebirthFromNonPlayer"))
            settings.rebirthFromNonPlayer = obj.get("rebirthFromNonPlayer").getAsBoolean();
        if(obj.has("preventSunlightDamage"))
            settings.preventSunlightDamage = obj.get("preventSunlightDamage").getAsBoolean();
        if(obj.has("biomeList")) {
            JsonArray biomeListJson = obj.getAsJsonArray("biomeList");
            settings.biomeList = new ArrayList<>(biomeListJson.size());
            for (JsonElement jsonElement : biomeListJson) {
                String biome = jsonElement.getAsString();
                settings.biomeList.add(biome);
            }
        }
        if(obj.has("rebornMobWeights")) {
            JsonArray mobWeightsJson = obj.get("rebornMobWeights").getAsJsonArray();
            settings.rebornMobWeights = new HashMap<>(mobWeightsJson.size());
            for (JsonElement mobWeightJson : mobWeightsJson) {
                JsonObject mobWeightJsonObject = mobWeightJson.getAsJsonObject();
                settings.rebornMobWeights.put(mobWeightJsonObject.get("mobId").getAsString(), mobWeightJsonObject.get("weight").getAsInt());
            }
        }
        return settings;
    }

    public void createSettings(Identifier id) {
        MobSettings settings = getSettings(id, true);
        File domainFolder = new File(MOB_SETTINGS_DIR, id.getNamespace());
        if(!domainFolder.exists())
            if(!domainFolder.mkdir()) {
                MobRebirthConstants.LOGGER.error("Unable to make domain folder for " + id);
                return;
            }
        File targetFile = new File(domainFolder, id.getPath()+".json5");
        settings.file = targetFile;
        writeSettings(settings, targetFile, configValues.getUseCompactCustomMobConfigs());

        MOB_SETTINGS.put(id, settings);
    }

    public void deleteSettings(Identifier id, MobSettings settings) {
        try {
            java.nio.file.Files.delete(settings.file.toPath());
        } catch (IOException exception) {
            MobRebirthConstants.LOGGER.error("Unable to delete mob settings!", exception);
        }
        MOB_SETTINGS.remove(id);
    }

    private void writeSettings(MobSettings settings, File file, boolean shouldOutputCompactFile) {
        JsonObject obj = new JsonObject();
        MobSettings defaultSettings = getDefaultSettings();
        if(settings.enabled != null && (!shouldOutputCompactFile || settings.enabled != defaultSettings.enabled))
            obj.addProperty("enabled", settings.enabled);
        if(!shouldOutputCompactFile || !settings.id.equals(defaultSettings.id))
            obj.addProperty("id", settings.id);
        if(!shouldOutputCompactFile || !settings.rebirthChance.equals(defaultSettings.rebirthChance))
            obj.addProperty("rebirthChance", settings.rebirthChance);
        if(!shouldOutputCompactFile || !settings.multiMobChance.equals(defaultSettings.multiMobChance))
            obj.addProperty("multiMobChance", settings.multiMobChance);
        if(!shouldOutputCompactFile || !settings.multiMobMode.equalsIgnoreCase(defaultSettings.multiMobMode))
            obj.addProperty("multiMobMode", settings.multiMobMode);
        if(!shouldOutputCompactFile || !settings.multiMobCount.equals(defaultSettings.multiMobCount))
            obj.addProperty("multiMobCount", settings.multiMobCount);
        if(!shouldOutputCompactFile || !settings.rebornAsEggs.equals(defaultSettings.rebornAsEggs))
            obj.addProperty("rebornAsEggs", settings.rebornAsEggs);
        if(!shouldOutputCompactFile || !settings.rebirthFromPlayer.equals(defaultSettings.rebirthFromPlayer))
            obj.addProperty("rebirthFromPlayer", settings.rebirthFromPlayer);
        if(!shouldOutputCompactFile || !settings.rebirthFromNonPlayer.equals(defaultSettings.rebirthFromNonPlayer))
            obj.addProperty("rebirthFromNonPlayer", settings.rebirthFromNonPlayer);
        if(!shouldOutputCompactFile || !settings.preventSunlightDamage.equals(defaultSettings.preventSunlightDamage))
            obj.addProperty("preventSunlightDamage", settings.preventSunlightDamage);
        if(!shouldOutputCompactFile || !settings.biomeList.equals(defaultSettings.biomeList)) {
            JsonArray biomeListJson = new JsonArray();
            for (String biome: settings.biomeList) {
                biomeListJson.add(biome);
            }
            obj.add("biomeList", biomeListJson);
        }
        if(!shouldOutputCompactFile || !settings.rebornMobWeights.equals(defaultSettings.rebornMobWeights)) {
            JsonArray mobWeightsJson = new JsonArray();
            for (Map.Entry<String, Integer> rebornMobWeight: settings.rebornMobWeights.entrySet()) {
                JsonObject mobWeightJson = new JsonObject();
                mobWeightJson.addProperty("mobId", rebornMobWeight.getKey());
                mobWeightJson.addProperty("weight", rebornMobWeight.getValue());
                mobWeightsJson.add(mobWeightJson);
            }
            obj.add("rebornMobWeights", mobWeightsJson);
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file), Short.MAX_VALUE)) {
            Gson gson = shouldOutputCompactFile
                ? new Gson()
                : new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(obj, bw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

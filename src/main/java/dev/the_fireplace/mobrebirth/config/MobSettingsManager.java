package dev.the_fireplace.mobrebirth.config;

import com.google.gson.*;
import dev.the_fireplace.annotateddi.api.DIContainer;
import dev.the_fireplace.lib.api.io.injectables.DirectoryResolver;
import dev.the_fireplace.lib.api.io.injectables.JsonFileReader;
import dev.the_fireplace.mobrebirth.MobRebirthConstants;
import dev.the_fireplace.mobrebirth.domain.config.ConfigValues;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@SuppressWarnings("UnstableApiUsage")
@Singleton
public final class MobSettingsManager {
    private final ConfigValues configValues;

    private final Map<Identifier, MobSettings> mobSettings = new HashMap<>();
    private final Path mobSettingsDirectory;
    private final File defaultSettingsFile;

    @Nullable
    private MobSettings defaultSettings = null;

    @Inject
    public MobSettingsManager(ConfigValues configValues, DirectoryResolver directoryResolver) {
        this.configValues = configValues;
        mobSettingsDirectory = directoryResolver.getConfigPath().resolve(MobRebirthConstants.MODID);
        defaultSettingsFile = mobSettingsDirectory.resolve("default.json").toFile();
    }

    public MobSettings getSettings(LivingEntity entity) {
        return getSettings(Registry.ENTITY_TYPE.getId(entity.getType()), false);
    }

    public MobSettings getSettings(Identifier identifier, boolean clone) {
        return clone ? mobSettings.getOrDefault(identifier, defaultSettings).clone() : mobSettings.getOrDefault(identifier, defaultSettings);
    }

    public Collection<Identifier> getCustomIds() {
        return mobSettings.keySet();
    }

    public Collection<MobSettings> getAllSettings() {
        return mobSettings.values();
    }

    public MobSettings getDefaultSettings() {
        if (defaultSettings == null) {
            throw new IllegalStateException("Attempted to access default mob settings before they were initialized!");
        }
        return defaultSettings;
    }

    public void init() {
        try {
            Files.createDirectories(mobSettingsDirectory);
        } catch (IOException | SecurityException exception) {
            MobRebirthConstants.LOGGER.error("Unable to create mob settings directory!", exception);
        }
        loadDefaultSettings();
        try {
            populateMobSettingsFromConfig();
        } catch (IOException exception) {
            MobRebirthConstants.LOGGER.error("Unable to read custom mob configs!", exception);
        }
    }

    public void saveAll() {
        writeSettings(getDefaultSettings(), defaultSettingsFile, false);
        for (MobSettings mobSettings : mobSettings.values()) {
            writeSettings(mobSettings, mobSettings.file, configValues.getUseCompactCustomMobConfigs());
        }
    }

    private void loadDefaultSettings() {
        defaultSettings = loadSettings(null, defaultSettingsFile);
        if (defaultSettings == null) {
            defaultSettings = new MobSettings();
            defaultSettings.file = defaultSettingsFile;
        }
        writeSettings(defaultSettings, defaultSettingsFile, false);
    }

    private void populateMobSettingsFromConfig() throws IOException {
        for (Iterator<Path> it = Files.list(mobSettingsDirectory).iterator(); it.hasNext(); ) {
            Path file = it.next();
            readMobConfigFile(file);
        }
    }

    private void readMobConfigFile(Path configFilePath) throws IOException {
        if (Files.isDirectory(configFilePath)) {
            Files.list(configFilePath).forEach(file2 -> scanSettingsFromFile(configFilePath.toFile(), file2.toFile()));
        } else {
            scanSettingsFromFile(null, configFilePath.toFile());
        }
    }

    private void scanSettingsFromFile(File filePath, File file2) {
        if (com.google.common.io.Files.getFileExtension(file2.getName()).equalsIgnoreCase("json")) {
            Identifier id = getIdentifier(filePath, file2);
            if (id != null) {
                mobSettings.put(id, loadSettings(id, file2));
            }
        }
    }

    @Nullable
    public Identifier getIdentifier(@Nullable File folder, File file) {
        try {
            JsonObject obj = new JsonObject();
            if (obj.has("id")) {
                String id = obj.get("id").getAsString();
                if (id == null || !id.isEmpty()) {//empty id should be folder based
                    return id != null ? new Identifier(id) : null;
                }
            }
            String id = com.google.common.io.Files.getNameWithoutExtension(file.getName());
            if (id.equalsIgnoreCase("default")) {
                return null;
            }
            return folder != null ? new Identifier(folder.getName(), id) : new Identifier(id);
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
        MobSettings settings = mobSettings.getOrDefault(id, defaultSettings);
        if (settings == null) {
            settings = new MobSettings();
        } else {
            settings = settings.clone();
        }

        settings.file = file;
        if (obj.has("id") && !obj.get("id").getAsString().isEmpty()) {
            settings.id = obj.get("id").getAsString();
        } else if (id != null) {
            settings.id = id.toString();
        }
        if (obj.has("enabled")) {
            settings.enabled = obj.get("enabled").getAsBoolean();
        }
        if (obj.has("rebirthChance")) {
            settings.rebirthChance = obj.get("rebirthChance").getAsDouble();
        }
        if (obj.has("multiMobChance")) {
            settings.extraMobChance = obj.get("multiMobChance").getAsDouble();
        }
        if (obj.has("multiMobMode")) {
            settings.extraMobMode = obj.get("multiMobMode").getAsString();
        }
        if (obj.has("multiMobCount")) {
            settings.extraMobCount = obj.get("multiMobCount").getAsInt();
        }
        if (obj.has("rebornAsEggs")) {
            settings.rebornAsEggs = obj.get("rebornAsEggs").getAsBoolean();
        }
        if (obj.has("rebirthFromPlayer")) {
            settings.rebirthFromPlayer = obj.get("rebirthFromPlayer").getAsBoolean();
        }
        if (obj.has("rebirthFromNonPlayer")) {
            settings.rebirthFromNonPlayer = obj.get("rebirthFromNonPlayer").getAsBoolean();
        }
        if (obj.has("preventSunlightDamage")) {
            settings.preventSunlightDamage = obj.get("preventSunlightDamage").getAsBoolean();
        }
        if (obj.has("biomeList")) {
            JsonArray biomeListJson = obj.getAsJsonArray("biomeList");
            settings.biomeList = new ArrayList<>(biomeListJson.size());
            for (JsonElement jsonElement : biomeListJson) {
                String biome = jsonElement.getAsString();
                settings.biomeList.add(biome);
            }
        }
        if (obj.has("rebornMobWeights")) {
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
        File domainFolder = mobSettingsDirectory.resolve(id.getNamespace()).toFile();
        if (!domainFolder.exists()) {
            if (!domainFolder.mkdir()) {
                MobRebirthConstants.LOGGER.error("Unable to make domain folder for " + id);
                return;
            }
        }
        File targetFile = new File(domainFolder, id.getPath() + ".json");
        settings.file = targetFile;
        writeSettings(settings, targetFile, configValues.getUseCompactCustomMobConfigs());

        mobSettings.put(id, settings);
    }

    public void deleteSettings(Identifier id, MobSettings settings) {
        try {
            Files.delete(settings.file.toPath());
        } catch (IOException exception) {
            MobRebirthConstants.LOGGER.error("Unable to delete mob settings!", exception);
        }
        mobSettings.remove(id);
    }

    private void writeSettings(MobSettings settings, File file, boolean shouldOutputCompactFile) {
        JsonObject obj = new JsonObject();
        MobSettings defaultSettings = getDefaultSettings();
        if (settings.enabled != null && (!shouldOutputCompactFile || settings.enabled != defaultSettings.enabled)) {
            obj.addProperty("enabled", settings.enabled);
        }
        if (!shouldOutputCompactFile || !settings.id.equals(defaultSettings.id)) {
            obj.addProperty("id", settings.id);
        }
        if (!shouldOutputCompactFile || settings.rebirthChance != defaultSettings.rebirthChance) {
            obj.addProperty("rebirthChance", settings.rebirthChance);
        }
        if (!shouldOutputCompactFile || settings.extraMobChance != defaultSettings.extraMobChance) {
            obj.addProperty("multiMobChance", settings.extraMobChance);
        }
        if (!shouldOutputCompactFile || !settings.extraMobMode.equalsIgnoreCase(defaultSettings.extraMobMode)) {
            obj.addProperty("multiMobMode", settings.extraMobMode);
        }
        if (!shouldOutputCompactFile || settings.extraMobCount != defaultSettings.extraMobCount) {
            obj.addProperty("multiMobCount", settings.extraMobCount);
        }
        if (!shouldOutputCompactFile || settings.rebornAsEggs != defaultSettings.rebornAsEggs) {
            obj.addProperty("rebornAsEggs", settings.rebornAsEggs);
        }
        if (!shouldOutputCompactFile || settings.rebirthFromPlayer != defaultSettings.rebirthFromPlayer) {
            obj.addProperty("rebirthFromPlayer", settings.rebirthFromPlayer);
        }
        if (!shouldOutputCompactFile || settings.rebirthFromNonPlayer != defaultSettings.rebirthFromNonPlayer) {
            obj.addProperty("rebirthFromNonPlayer", settings.rebirthFromNonPlayer);
        }
        if (!shouldOutputCompactFile || settings.preventSunlightDamage != defaultSettings.preventSunlightDamage) {
            obj.addProperty("preventSunlightDamage", settings.preventSunlightDamage);
        }
        if (!shouldOutputCompactFile || !settings.biomeList.equals(defaultSettings.biomeList)) {
            JsonArray biomeListJson = new JsonArray();
            for (String biome : settings.biomeList) {
                biomeListJson.add(biome);
            }
            obj.add("biomeList", biomeListJson);
        }
        if (!shouldOutputCompactFile || !settings.rebornMobWeights.equals(defaultSettings.rebornMobWeights)) {
            JsonArray mobWeightsJson = new JsonArray();
            for (Map.Entry<String, Integer> rebornMobWeight : settings.rebornMobWeights.entrySet()) {
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

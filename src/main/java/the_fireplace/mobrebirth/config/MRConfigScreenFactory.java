package the_fireplace.mobrebirth.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import dev.the_fireplace.lib.api.chat.injectables.TranslatorFactory;
import dev.the_fireplace.lib.api.chat.interfaces.Translator;
import dev.the_fireplace.lib.api.client.injectables.ConfigScreenBuilderFactory;
import dev.the_fireplace.lib.api.client.interfaces.ConfigScreenBuilder;
import dev.the_fireplace.lib.api.lazyio.injectables.ConfigStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import the_fireplace.mobrebirth.MobRebirth;
import the_fireplace.mobrebirth.domain.config.ConfigValues;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
@Singleton
public final class MRConfigScreenFactory {
    private static final String TRANSLATION_BASE = "text.config." + MobRebirth.MODID + ".";
    private static final String OPTION_TRANSLATION_BASE = TRANSLATION_BASE + "option.";
    private static final String MOB_WEIGHT_MAP_ENTRY_REGEX = "([a-zA-Z_\\-0-9./]+(:[a-zA-Z_\\-0-9.]+)?)?=[0-9]+";

    private final Translator translator;
    private final ConfigStateManager configStateManager;
    private final MRConfig config;
    private final ConfigValues defaultConfigValues;
    private final ConfigScreenBuilderFactory configScreenBuilderFactory;

    private final MobSettings mobSettingsDefaults = new MobSettings();

    private ConfigScreenBuilder configScreenBuilder;

    @Inject
    public MRConfigScreenFactory(
        TranslatorFactory translatorFactory,
        ConfigStateManager configStateManager,
        MRConfig config,
        @Named("default") ConfigValues defaultConfigValues,
        ConfigScreenBuilderFactory configScreenBuilderFactory
    ) {
        this.translator = translatorFactory.getTranslator(MobRebirth.MODID);
        this.configStateManager = configStateManager;
        this.config = config;
        this.defaultConfigValues = defaultConfigValues;
        this.configScreenBuilderFactory = configScreenBuilderFactory;
    }

    public Screen getConfigScreen(Screen parent) {
        this.configScreenBuilder = configScreenBuilderFactory.create(
            translator,
            TRANSLATION_BASE + "title",
            TRANSLATION_BASE + "general",
            parent,
            () -> configStateManager.save(config)
        );
        addGeneralCategoryEntries();
        buildDefaultMobSettingsCategory(MobSettingsManager.getDefaultSettings());
        for (MobSettings mobSettings: MobSettingsManager.getAllSettings()) {
            buildCustomMobSettingsCategory(mobSettings);
        }

        return this.configScreenBuilder.build();
    }

    private void addGeneralCategoryEntries() {
        configScreenBuilder.addBoolToggle(
            OPTION_TRANSLATION_BASE + "allowBossRebirth",
            config.getAllowBossRebirth(),
            defaultConfigValues.getAllowBossRebirth(),
            config::setAllowBossRebirth
        );
        configScreenBuilder.addBoolToggle(
            OPTION_TRANSLATION_BASE + "allowSlimeRebirth",
            config.getAllowSlimeRebirth(),
            defaultConfigValues.getAllowSlimeRebirth(),
            config::setAllowSlimeRebirth
        );
        configScreenBuilder.addBoolToggle(
            OPTION_TRANSLATION_BASE + "allowAnimalRebirth",
            config.getAllowAnimalRebirth(),
            defaultConfigValues.getAllowAnimalRebirth(),
            config::setAllowAnimalRebirth
        );
        configScreenBuilder.addBoolToggle(
            OPTION_TRANSLATION_BASE + "onlyAllowVanillaMobRebirth",
            config.getVanillaRebirthOnly(),
            defaultConfigValues.getVanillaRebirthOnly(),
            config::setOnlyAllowVanillaMobRebirth
        );
        configScreenBuilder.addBoolToggle(
            OPTION_TRANSLATION_BASE + "useCompactCustomMobConfigs",
            config.getUseCompactCustomMobConfigs(),
            defaultConfigValues.getUseCompactCustomMobConfigs(),
            config::setUseCompactCustomMobConfigs,
            (byte) 2
        );

        createAddCustomMobDropdown();
    }

    private void createAddCustomMobDropdown() {
        List<Identifier> entityIdentifiers = Lists.newArrayList(Registry.ENTITY_TYPE.getIds()).stream().filter(id -> Registry.ENTITY_TYPE.get(id).isSummonable()).collect(Collectors.toList());
        List<String> mobIdsWithoutCustomSettings = Lists.newArrayListWithCapacity(entityIdentifiers.size()-MobSettingsManager.getAllSettings().size());
        for (Identifier id: entityIdentifiers) {
            if (!MobSettingsManager.getCustomIds().contains(id)) {
                mobIdsWithoutCustomSettings.add(id.toString());
            }
        }

        // Not a real, savable option, but it's the easiest way to allow adding custom mobs in the GUI without hacking cloth config or building something custom that users won't be familiar with.
        configScreenBuilder.addStringDropdown(
            OPTION_TRANSLATION_BASE + "addCustomMob",
            "",
            "",
            mobIdsWithoutCustomSettings,
            newValue -> {
                if (!newValue.isEmpty()) {
                    MobSettingsManager.createSettings(new Identifier(newValue));
                }
            },
            false,
            (byte) 2,
            value ->
                MobSettingsManager.getCustomIds().contains(new Identifier(value))
                    ? Optional.of(translator.getTranslatedText(OPTION_TRANSLATION_BASE + "addCustomMob.err"))
                    : Optional.empty()
        );
    }

    private void buildDefaultMobSettingsCategory(MobSettings mobSettings) {
        configScreenBuilder.startCategory(TRANSLATION_BASE + "defaultSettings");
        addCommonMobSettingsCategoryOptions(mobSettings);
    }

    private void buildCustomMobSettingsCategory(MobSettings mobSettings) {
        //noinspection UnstableApiUsage
        Identifier id = mobSettings.id.isEmpty() ? MobSettingsManager.getIdentifier(Files.getNameWithoutExtension(mobSettings.getFile().getParent()), mobSettings.getFile()) : new Identifier(mobSettings.id);
        if (id == null) {
            MobRebirth.LOGGER.error("Unable to get id for mob with settings at " + mobSettings.getFile().toString());
            return;
        }
        configScreenBuilder.startCategory(TRANSLATION_BASE + "mobSettings", id.toString());
        configScreenBuilder.addBoolToggle(
            OPTION_TRANSLATION_BASE + "enabled",
            mobSettings.enabled == null || mobSettings.enabled,
            true,
            newValue -> mobSettings.enabled = newValue,
            (byte) 2
        );

        addCommonMobSettingsCategoryOptions(mobSettings);

        // Fake option, used to allow deleting custom settings without hacking cloth config
        configScreenBuilder.addBoolToggle(
            OPTION_TRANSLATION_BASE + "deleteCustomMob",
            false,
            false,
            newValue -> {
                if (newValue) {
                    MobSettingsManager.deleteSettings(id, mobSettings);
                }
            },
            (byte) 2
        );
    }

    private void addCommonMobSettingsCategoryOptions(MobSettings mobSettings) {
        configScreenBuilder.addDoublePercentSlider(
            OPTION_TRANSLATION_BASE + "rebirthChance",
            mobSettings.rebirthChance,
            mobSettingsDefaults.rebirthChance,
            newValue -> mobSettings.rebirthChance = newValue
        );
        configScreenBuilder.addDoublePercentSlider(
            OPTION_TRANSLATION_BASE + "multiMobChance",
            mobSettings.multiMobChance,
            mobSettingsDefaults.multiMobChance,
            newValue -> mobSettings.multiMobChance = newValue
        );
        configScreenBuilder.addStringDropdown(
            OPTION_TRANSLATION_BASE + "multiMobMode",
            mobSettings.multiMobMode,
            mobSettingsDefaults.multiMobMode,
            Arrays.asList("continuous", "per-mob", "all"),
            newValue -> mobSettings.multiMobMode = newValue,
            false,
            (byte) 5
        );
        configScreenBuilder.addIntField(
            OPTION_TRANSLATION_BASE + "multiMobCount",
            mobSettings.multiMobCount,
            mobSettingsDefaults.multiMobCount,
            newValue -> mobSettings.multiMobCount = newValue,
            0,
            Integer.MAX_VALUE,
            (byte) 2
        );
        configScreenBuilder.addBoolToggle(
            OPTION_TRANSLATION_BASE + "rebornAsEggs",
            mobSettings.rebornAsEggs,
            mobSettingsDefaults.rebornAsEggs,
            newValue -> mobSettings.rebornAsEggs = newValue
        );
        configScreenBuilder.addBoolToggle(
            OPTION_TRANSLATION_BASE + "rebirthFromPlayer",
            mobSettings.rebirthFromPlayer,
            mobSettingsDefaults.rebirthFromPlayer,
            newValue -> mobSettings.rebirthFromPlayer = newValue
        );
        configScreenBuilder.addBoolToggle(
            OPTION_TRANSLATION_BASE + "rebirthFromNonPlayer",
            mobSettings.rebirthFromNonPlayer,
            mobSettingsDefaults.rebirthFromNonPlayer,
            newValue -> mobSettings.rebirthFromNonPlayer = newValue
        );
        configScreenBuilder.addBoolToggle(
            OPTION_TRANSLATION_BASE + "preventSunlightDamage",
            mobSettings.preventSunlightDamage,
            mobSettingsDefaults.preventSunlightDamage,
            newValue -> mobSettings.preventSunlightDamage = newValue,
            (byte) 2
        );
        configScreenBuilder.addStringListField(
            OPTION_TRANSLATION_BASE + "biomeList",
            mobSettings.biomeList,
            mobSettingsDefaults.biomeList,
            newValue -> mobSettings.biomeList = newValue,
            (byte) 2
        );
        configScreenBuilder.addStringListField(
            OPTION_TRANSLATION_BASE + "rebornMobWeights",
            mapToList(mobSettings.rebornMobWeights),
            mapToList(mobSettingsDefaults.rebornMobWeights),
            newValue -> mobSettings.rebornMobWeights = listToMap(newValue),
            (byte) 2,
            strList -> {
                for (String str: strList) {
                    if (!str.matches(MOB_WEIGHT_MAP_ENTRY_REGEX)) {
                        return Optional.of(translator.getTranslatedText(OPTION_TRANSLATION_BASE + "rebornMobWeights.err", str));
                    }
                }
                return Optional.empty();
            }
        );
    }

    //TODO integrate these with FL 6.0.0
    public static final String MAP_SEPARATOR = "=";

    private static List<String> mapToList(Map<String, Integer> map) {
        List<String> stringList = Lists.newArrayList();
        for (Map.Entry<String, Integer> entry: map.entrySet()) {
            stringList.add(entry.getKey() + MAP_SEPARATOR + entry.getValue().toString());
        }

        return stringList;
    }

    private static Map<String, Integer> listToMap(List<String> list) {
        Map<String, Integer> map = Maps.newHashMap();
        for (String str: list) {
            String[] parts = str.split(MAP_SEPARATOR);
            map.put(parts[0], Integer.parseInt(parts[1]));
        }

        return map;
    }
}

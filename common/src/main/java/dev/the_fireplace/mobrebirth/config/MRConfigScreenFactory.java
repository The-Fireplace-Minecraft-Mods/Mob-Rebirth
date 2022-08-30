package dev.the_fireplace.mobrebirth.config;

import dev.the_fireplace.lib.api.chat.injectables.TranslatorFactory;
import dev.the_fireplace.lib.api.chat.interfaces.Translator;
import dev.the_fireplace.lib.api.client.injectables.ConfigScreenBuilderFactory;
import dev.the_fireplace.lib.api.client.interfaces.ConfigScreenBuilder;
import dev.the_fireplace.lib.api.client.interfaces.DecimalSliderOptionBuilder;
import dev.the_fireplace.lib.api.client.interfaces.OptionBuilder;
import dev.the_fireplace.lib.api.lazyio.injectables.ConfigStateManager;
import dev.the_fireplace.mobrebirth.MobRebirthConstants;
import dev.the_fireplace.mobrebirth.domain.config.ConfigValues;
import dev.the_fireplace.mobrebirth.util.MapListConverter;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
public final class MRConfigScreenFactory {
    private static final String TRANSLATION_BASE = "text.config." + MobRebirthConstants.MODID + ".";
    private static final String OPTION_TRANSLATION_BASE = TRANSLATION_BASE + "option.";
    private static final String MOB_WEIGHT_MAP_ENTRY_REGEX = "([a-zA-Z_\\-0-9./]+(:[a-zA-Z_\\-0-9.]+)?)?=[0-9]+";

    private final Translator translator;
    private final ConfigStateManager configStateManager;
    private final MRConfig config;
    private final ConfigValues defaultConfigValues;
    private final ConfigScreenBuilderFactory configScreenBuilderFactory;
    private final MobSettingsManager mobSettingsManager;

    private final MobSettings defaultMobSettings;

    private ConfigScreenBuilder configScreenBuilder;

    @Inject
    public MRConfigScreenFactory(
        TranslatorFactory translatorFactory,
        ConfigStateManager configStateManager,
        MRConfig config,
        @Named("default") ConfigValues defaultConfigValues,
        ConfigScreenBuilderFactory configScreenBuilderFactory,
        MobSettingsManager mobSettingsManager,
        DefaultMobSettings defaultMobSettings
    ) {
        this.translator = translatorFactory.getTranslator(MobRebirthConstants.MODID);
        this.configStateManager = configStateManager;
        this.config = config;
        this.defaultConfigValues = defaultConfigValues;
        this.configScreenBuilderFactory = configScreenBuilderFactory;
        this.mobSettingsManager = mobSettingsManager;
        this.defaultMobSettings = defaultMobSettings;
    }

    public Screen getConfigScreen(Screen parent) {
        this.configScreenBuilder = configScreenBuilderFactory.create(
            translator,
            TRANSLATION_BASE + "title",
            TRANSLATION_BASE + "general",
            parent,
            () -> {
                configStateManager.save(config);
                mobSettingsManager.saveAll();
            }
        ).orElseThrow();
        addGeneralCategoryEntries();
        buildDefaultMobSettingsCategory(defaultMobSettings);
        for (ResourceLocation customMobId : mobSettingsManager.getMobIdsWithCustomSettings()) {
            buildCustomMobSettingsCategory(customMobId, mobSettingsManager.getSettings(customMobId));
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

        createAddCustomMobDropdown();
    }

    private void createAddCustomMobDropdown() {

        // Not a real, savable option, but it's the easiest way to allow adding custom mobs in the GUI without hacking cloth config or building something custom that users won't be familiar with.
        configScreenBuilder.addStringDropdown(
            OPTION_TRANSLATION_BASE + "addCustomMob",
            "",
            "",
            mobSettingsManager.getMobIdsWithoutCustomSettings().stream().map(ResourceLocation::toString).sorted().collect(Collectors.toList()),
            newValue -> {
                if (!newValue.isEmpty()) {
                    mobSettingsManager.addCustom(new ResourceLocation(newValue), defaultMobSettings.clone());
                }
            }
        ).setDescriptionRowCount((byte) 2).setErrorSupplier(value ->
            mobSettingsManager.isCustom(new ResourceLocation(value))
                ? Optional.of(translator.getTranslatedText(OPTION_TRANSLATION_BASE + "addCustomMob.err"))
                : Optional.empty()
        );
    }

    private void buildDefaultMobSettingsCategory(MobSettings mobSettings) {
        configScreenBuilder.startCategory(TRANSLATION_BASE + "defaultSettings");
        addCommonMobSettingsCategoryOptions(mobSettings);
    }

    private void buildCustomMobSettingsCategory(ResourceLocation id, MobSettings mobSettings) {
        configScreenBuilder.startCategory(TRANSLATION_BASE + "mobSettings", id.toString());

        addCommonMobSettingsCategoryOptions(mobSettings);

        // Fake option, used to allow deleting custom settings without hacking cloth config
        configScreenBuilder.addBoolToggle(
            OPTION_TRANSLATION_BASE + "deleteCustomMob",
            false,
            false,
            newValue -> {
                if (newValue) {
                    mobSettingsManager.deleteCustom(id);
                }
            }
        ).setDescriptionRowCount((byte) 2);
    }

    private void addCommonMobSettingsCategoryOptions(MobSettings mobSettings) {
        OptionBuilder<Boolean> enabled = configScreenBuilder.addBoolToggle(
            OPTION_TRANSLATION_BASE + "enabled",
            mobSettings.isEnabled(),
            defaultMobSettings.isEnabled(),
            mobSettings::setEnabled
        ).setDescriptionRowCount((byte) 2);
        DecimalSliderOptionBuilder<Double> rebirthChance = configScreenBuilder.addDoubleSlider(
            OPTION_TRANSLATION_BASE + "rebirthChance",
            mobSettings.getRebirthChance(),
            defaultMobSettings.getRebirthChance(),
            mobSettings::setRebirthChance,
            0,
            100
        );
        rebirthChance.enablePercentMode();
        rebirthChance.addDependency(enabled);
        DecimalSliderOptionBuilder<Double> multiMobChance = configScreenBuilder.addDoubleSlider(
            OPTION_TRANSLATION_BASE + "multiMobChance",
            mobSettings.getExtraMobChance(),
            defaultMobSettings.getExtraMobChance(),
            mobSettings::setExtraMobChance,
            0,
            100
        );
        multiMobChance.enablePercentMode();
        multiMobChance.addDependency(rebirthChance, value -> value > 0);
        configScreenBuilder.addStringDropdown(
            OPTION_TRANSLATION_BASE + "multiMobMode",
            mobSettings.getExtraMobMode(),
            defaultMobSettings.getExtraMobMode(),
            Arrays.asList("continuous", "per-mob", "all"),
            mobSettings::setExtraMobMode
        ).setDescriptionRowCount((byte) 5).addDependency(multiMobChance, value -> value > 0);
        configScreenBuilder.addIntField(
            OPTION_TRANSLATION_BASE + "multiMobCount",
            mobSettings.getExtraMobCount(),
            defaultMobSettings.getExtraMobCount(),
            mobSettings::setExtraMobCount
        ).setMinimum(0).setDescriptionRowCount((byte) 2).addDependency(multiMobChance, value -> value > 0);
        ;
        configScreenBuilder.addBoolToggle(
            OPTION_TRANSLATION_BASE + "rebornAsEggs",
            mobSettings.isRebornAsEggs(),
            defaultMobSettings.isRebornAsEggs(),
            mobSettings::setRebornAsEggs
        ).addDependency(rebirthChance, value -> value > 0);
        configScreenBuilder.addBoolToggle(
            OPTION_TRANSLATION_BASE + "rebirthFromPlayer",
            mobSettings.isRebirthFromPlayer(),
            defaultMobSettings.isRebirthFromPlayer(),
            mobSettings::setRebirthFromPlayer
        ).addDependency(rebirthChance, value -> value > 0);
        configScreenBuilder.addBoolToggle(
            OPTION_TRANSLATION_BASE + "rebirthFromNonPlayer",
            mobSettings.isRebirthFromNonPlayer(),
            defaultMobSettings.isRebirthFromNonPlayer(),
            mobSettings::setRebirthFromNonPlayer
        ).addDependency(rebirthChance, value -> value > 0);
        configScreenBuilder.addBoolToggle(
            OPTION_TRANSLATION_BASE + "preventSunlightDamage",
            mobSettings.isPreventSunlightDamage(),
            defaultMobSettings.isPreventSunlightDamage(),
            mobSettings::setPreventSunlightDamage
        ).setDescriptionRowCount((byte) 2).addDependency(enabled);
        configScreenBuilder.addStringListField(
            OPTION_TRANSLATION_BASE + "biomeList",
            mobSettings.getBiomeList(),
            defaultMobSettings.getBiomeList(),
            mobSettings::setBiomeList
        ).setDescriptionRowCount((byte) 2).addDependency(rebirthChance, value -> value > 0);
        configScreenBuilder.addStringListField(
            OPTION_TRANSLATION_BASE + "rebornMobWeights",
            MapListConverter.mapToList(mobSettings.getRebornMobWeights()),
            MapListConverter.mapToList(defaultMobSettings.getRebornMobWeights()),
            newValue -> mobSettings.setRebornMobWeights(MapListConverter.listToMap(newValue))
        ).setDescriptionRowCount((byte) 2).setErrorSupplier(strList -> {
            for (String str : strList) {
                if (!str.matches(MOB_WEIGHT_MAP_ENTRY_REGEX)) {
                    return Optional.of(translator.getTranslatedText(OPTION_TRANSLATION_BASE + "rebornMobWeights.err", str));
                }
            }
            return Optional.empty();
        }).addDependency(rebirthChance, value -> value > 0);
    }

}

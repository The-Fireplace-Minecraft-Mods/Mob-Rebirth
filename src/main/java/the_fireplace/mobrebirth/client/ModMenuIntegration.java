package the_fireplace.mobrebirth.client;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.TranslatableText;
import the_fireplace.mobrebirth.MobRebirth;
import the_fireplace.mobrebirth.config.MobSettings;
import the_fireplace.mobrebirth.config.MobSettingsManager;
import the_fireplace.mobrebirth.config.ModConfig;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(new TranslatableText("text.config.mobrebirth.title"));

            ConfigCategory general = builder.getOrCreateCategory(new TranslatableText("text.config.mobrebirth.general"));
            general.setDescription(new StringVisitable[]{new TranslatableText("text.config.mobrebirth.general.desc")});
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();
            general.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("text.config.mobrebirth.option.allowBosses"), MobRebirth.config.allowBosses)
                .setDefaultValue(new ModConfig().allowBosses)
                .setTooltip(new TranslatableText("text.config.mobrebirth.option.allowBosses.desc"))
                .setSaveConsumer(newValue -> MobRebirth.config.allowBosses = newValue)
                .build());
            general.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("text.config.mobrebirth.option.allowSlimes"), MobRebirth.config.allowSlimes)
                .setDefaultValue(new ModConfig().allowSlimes)
                .setTooltip(new TranslatableText("text.config.mobrebirth.option.allowSlimes.desc"))
                .setSaveConsumer(newValue -> MobRebirth.config.allowSlimes = newValue)
                .build());
            general.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("text.config.mobrebirth.option.allowAnimals"), MobRebirth.config.allowAnimals)
                .setDefaultValue(new ModConfig().allowAnimals)
                .setTooltip(new TranslatableText("text.config.mobrebirth.option.allowAnimals.desc"))
                .setSaveConsumer(newValue -> MobRebirth.config.allowAnimals = newValue)
                .build());
            general.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("text.config.mobrebirth.option.vanillaMobsOnly"), MobRebirth.config.vanillaMobsOnly)
                .setDefaultValue(new ModConfig().vanillaMobsOnly)
                .setTooltip(new TranslatableText("text.config.mobrebirth.option.vanillaMobsOnly.desc"))
                .setSaveConsumer(newValue -> MobRebirth.config.vanillaMobsOnly = newValue)
                .build());

            ConfigCategory defaultSettings = builder.getOrCreateCategory(new TranslatableText("text.config.mobrebirth.defaultSettings"));
            defaultSettings.setDescription(new StringVisitable[]{new TranslatableText("text.config.mobrebirth.defaultSettings.desc")});
            defaultSettings.addEntry(entryBuilder.startDoubleField(new TranslatableText("text.config.mobrebirth.option.rebirthChance"), MobSettingsManager.getDefaultSettings().rebirthChance)
                .setDefaultValue(new MobSettings().rebirthChance)
                .setTooltip(new TranslatableText("text.config.mobrebirth.option.rebirthChance.desc"))
                .setSaveConsumer(newValue -> MobSettingsManager.getDefaultSettings().rebirthChance = newValue)
                .build());
            defaultSettings.addEntry(entryBuilder.startDoubleField(new TranslatableText("text.config.mobrebirth.option.multiMobChance"), MobSettingsManager.getDefaultSettings().multiMobChance)
                .setDefaultValue(new MobSettings().multiMobChance)
                .setTooltip(new TranslatableText("text.config.mobrebirth.option.multiMobChance.desc"))
                .setSaveConsumer(newValue -> MobSettingsManager.getDefaultSettings().multiMobChance = newValue)
                .build());
            defaultSettings.addEntry(entryBuilder.startSelector(new TranslatableText("text.config.mobrebirth.option.multiMobMode"), new String[]{"continuous", "per-mob", "all"}, MobSettingsManager.getDefaultSettings().multiMobMode)
                .setDefaultValue(new MobSettings().multiMobMode)
                .setTooltip(new TranslatableText("text.config.mobrebirth.option.multiMobMode.desc.0"), new TranslatableText("text.config.mobrebirth.option.multiMobMode.desc.1"), new TranslatableText("text.config.mobrebirth.option.multiMobMode.desc.2"), new TranslatableText("text.config.mobrebirth.option.multiMobMode.desc.3"))
                .setSaveConsumer(newValue -> MobSettingsManager.getDefaultSettings().multiMobMode = newValue)
                .build());
            defaultSettings.addEntry(entryBuilder.startIntField(new TranslatableText("text.config.mobrebirth.option.multiMobCount"), MobSettingsManager.getDefaultSettings().multiMobCount)
                .setDefaultValue(new MobSettings().multiMobCount)
                .setTooltip(new TranslatableText("text.config.mobrebirth.option.multiMobCount.desc"))
                .setSaveConsumer(newValue -> MobSettingsManager.getDefaultSettings().multiMobCount = newValue)
                .build());
            defaultSettings.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("text.config.mobrebirth.option.rebornAsEggs"), MobSettingsManager.getDefaultSettings().rebornAsEggs)
                .setDefaultValue(new MobSettings().rebornAsEggs)
                .setTooltip(new TranslatableText("text.config.mobrebirth.option.rebornAsEggs.desc"))
                .setSaveConsumer(newValue -> MobSettingsManager.getDefaultSettings().rebornAsEggs = newValue)
                .build());
            defaultSettings.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("text.config.mobrebirth.option.rebirthFromPlayer"), MobSettingsManager.getDefaultSettings().rebirthFromPlayer)
                .setDefaultValue(new MobSettings().rebirthFromPlayer)
                .setTooltip(new TranslatableText("text.config.mobrebirth.option.rebirthFromPlayer.desc"))
                .setSaveConsumer(newValue -> MobSettingsManager.getDefaultSettings().rebirthFromPlayer = newValue)
                .build());
            defaultSettings.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("text.config.mobrebirth.option.rebirthFromNonPlayer"), MobSettingsManager.getDefaultSettings().rebirthFromNonPlayer)
                .setDefaultValue(new MobSettings().rebirthFromNonPlayer)
                .setTooltip(new TranslatableText("text.config.mobrebirth.option.rebirthFromNonPlayer.desc"))
                .setSaveConsumer(newValue -> MobSettingsManager.getDefaultSettings().rebirthFromNonPlayer = newValue)
                .build());
            defaultSettings.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("text.config.mobrebirth.option.preventSunlightDamage"), MobSettingsManager.getDefaultSettings().preventSunlightDamage)
                .setDefaultValue(new MobSettings().preventSunlightDamage)
                .setTooltip(new TranslatableText("text.config.mobrebirth.option.preventSunlightDamage.desc"))
                .setSaveConsumer(newValue -> MobSettingsManager.getDefaultSettings().preventSunlightDamage = newValue)
                .build());
            defaultSettings.addEntry(entryBuilder.startStrList(new TranslatableText("text.config.mobrebirth.option.biomeList"), MobSettingsManager.getDefaultSettings().biomeList)
                .setDefaultValue(new MobSettings().biomeList)
                .setTooltip(new TranslatableText("text.config.mobrebirth.option.biomeList.desc"))
                .setSaveConsumer(newValue -> MobSettingsManager.getDefaultSettings().biomeList = newValue)
                .build());
            /*defaultSettings.addEntry(entryBuilder.start(new TranslatableText("text.config.mobrebirth.option.rebornMobWeights"), MobSettingsManager.getDefaultSettings().rebornMobWeights)
                .setDefaultValue(new MobSettings().rebornMobWeights)
                .setTooltip(new TranslatableText("text.config.mobrebirth.option.rebornMobWeights.desc"))
                .setSaveConsumer(newValue -> MobSettingsManager.getDefaultSettings().rebornMobWeights = newValue)
                .build());*/

            builder.setSavingRunnable(() -> {
                MobRebirth.config.save();
                MobSettingsManager.saveAll();
            });
            return builder.build();
        };
    }
}

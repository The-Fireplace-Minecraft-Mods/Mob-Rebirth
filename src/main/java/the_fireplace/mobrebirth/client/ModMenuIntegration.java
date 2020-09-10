package the_fireplace.mobrebirth.client;

import com.google.common.collect.Sets;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import the_fireplace.mobrebirth.MobRebirth;
import the_fireplace.mobrebirth.config.MobSettings;
import the_fireplace.mobrebirth.config.MobSettingsManager;
import the_fireplace.mobrebirth.config.ModConfig;

import java.util.Set;

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
            general.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("text.config.mobrebirth.option.compactCustomMobConfigs"), MobRebirth.config.compactCustomMobConfigs)
                .setDefaultValue(new ModConfig().compactCustomMobConfigs)
                .setTooltip(genDescriptionTranslatables("text.config.mobrebirth.option.compactCustomMobConfigs.desc", 2))
                .setSaveConsumer(newValue -> MobRebirth.config.compactCustomMobConfigs = newValue)
                .build());

            buildMobSettingsCategory(builder, entryBuilder, MobSettingsManager.getDefaultSettings(), true);
            for(MobSettings mobSettings: MobSettingsManager.getAllSettings())
                buildMobSettingsCategory(builder, entryBuilder, mobSettings, false);

            builder.setSavingRunnable(() -> {
                MobRebirth.config.save();
                MobSettingsManager.saveAll();
            });
            return builder.build();
        };
    }
    
    public static void buildMobSettingsCategory(ConfigBuilder builder, ConfigEntryBuilder entryBuilder, MobSettings mobSettings, boolean isDefault) {
        ConfigCategory defaultSettings = builder.getOrCreateCategory(isDefault ? new TranslatableText("text.config.mobrebirth.defaultSettings") : new TranslatableText("text.config.mobrebirth.mobSettings", mobSettings.id));
        defaultSettings.setDescription(new StringVisitable[]{(isDefault ? new TranslatableText("text.config.mobrebirth.defaultSettings.desc") : new TranslatableText("text.config.mobrebirth.mobSettings.desc", mobSettings.id))});
        if(!isDefault) {
            defaultSettings.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("text.config.mobrebirth.option.enabled"), mobSettings.enabled == null || mobSettings.enabled)
                .setDefaultValue(true)
                .setTooltip(new TranslatableText("text.config.mobrebirth.option.enabled.desc"))
                .setSaveConsumer(newValue -> mobSettings.enabled = newValue)
                .build());
        }
        defaultSettings.addEntry(entryBuilder.startDoubleField(new TranslatableText("text.config.mobrebirth.option.rebirthChance"), mobSettings.rebirthChance)
            .setDefaultValue(new MobSettings().rebirthChance)
            .setTooltip(new TranslatableText("text.config.mobrebirth.option.rebirthChance.desc"))
            .setSaveConsumer(newValue -> mobSettings.rebirthChance = newValue)
            .build());
        defaultSettings.addEntry(entryBuilder.startDoubleField(new TranslatableText("text.config.mobrebirth.option.multiMobChance"), mobSettings.multiMobChance)
            .setDefaultValue(new MobSettings().multiMobChance)
            .setTooltip(new TranslatableText("text.config.mobrebirth.option.multiMobChance.desc"))
            .setSaveConsumer(newValue -> mobSettings.multiMobChance = newValue)
            .build());
        defaultSettings.addEntry(entryBuilder.startSelector(new TranslatableText("text.config.mobrebirth.option.multiMobMode"), new String[]{"continuous", "per-mob", "all"}, mobSettings.multiMobMode)
            .setDefaultValue(new MobSettings().multiMobMode)
            .setTooltip(genDescriptionTranslatables("text.config.mobrebirth.option.multiMobMode.desc", 5))
            .setSaveConsumer(newValue -> mobSettings.multiMobMode = newValue)
            .build());
        defaultSettings.addEntry(entryBuilder.startIntField(new TranslatableText("text.config.mobrebirth.option.multiMobCount"), mobSettings.multiMobCount)
            .setDefaultValue(new MobSettings().multiMobCount)
            .setTooltip(new TranslatableText("text.config.mobrebirth.option.multiMobCount.desc"))
            .setSaveConsumer(newValue -> mobSettings.multiMobCount = newValue)
            .build());
        defaultSettings.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("text.config.mobrebirth.option.rebornAsEggs"), mobSettings.rebornAsEggs)
            .setDefaultValue(new MobSettings().rebornAsEggs)
            .setTooltip(new TranslatableText("text.config.mobrebirth.option.rebornAsEggs.desc"))
            .setSaveConsumer(newValue -> mobSettings.rebornAsEggs = newValue)
            .build());
        defaultSettings.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("text.config.mobrebirth.option.rebirthFromPlayer"), mobSettings.rebirthFromPlayer)
            .setDefaultValue(new MobSettings().rebirthFromPlayer)
            .setTooltip(new TranslatableText("text.config.mobrebirth.option.rebirthFromPlayer.desc"))
            .setSaveConsumer(newValue -> mobSettings.rebirthFromPlayer = newValue)
            .build());
        defaultSettings.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("text.config.mobrebirth.option.rebirthFromNonPlayer"), mobSettings.rebirthFromNonPlayer)
            .setDefaultValue(new MobSettings().rebirthFromNonPlayer)
            .setTooltip(new TranslatableText("text.config.mobrebirth.option.rebirthFromNonPlayer.desc"))
            .setSaveConsumer(newValue -> mobSettings.rebirthFromNonPlayer = newValue)
            .build());
        defaultSettings.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("text.config.mobrebirth.option.preventSunlightDamage"), mobSettings.preventSunlightDamage)
            .setDefaultValue(new MobSettings().preventSunlightDamage)
            .setTooltip(genDescriptionTranslatables("text.config.mobrebirth.option.preventSunlightDamage.desc", 2))
            .setSaveConsumer(newValue -> mobSettings.preventSunlightDamage = newValue)
            .build());
        defaultSettings.addEntry(entryBuilder.startStrList(new TranslatableText("text.config.mobrebirth.option.biomeList"), mobSettings.biomeList)
            .setDefaultValue(new MobSettings().biomeList)
            .setTooltip(new TranslatableText("text.config.mobrebirth.option.biomeList.desc"))
            .setSaveConsumer(newValue -> mobSettings.biomeList = newValue)
            .build());
        /*defaultSettings.addEntry(entryBuilder.startMap(new TranslatableText("text.config.mobrebirth.option.rebornMobWeights"), mobSettings.rebornMobWeights)
            .setDefaultValue(new MobSettings().rebornMobWeights)
            .setTooltip(genDescriptionTranslatables("text.config.mobrebirth.option.rebornMobWeights.desc", 2))
            .setSaveConsumer(newValue -> mobSettings.rebornMobWeights = newValue)
            .build());*/
    }

    private static Text[] genDescriptionTranslatables(String baseKey, int count) {
        Set<Text> texts = Sets.newHashSet();
        for(int i=0;i<count;i++)
            texts.add(new TranslatableText(baseKey+"."+i));
        return texts.toArray(new Text[0]);
    }
}

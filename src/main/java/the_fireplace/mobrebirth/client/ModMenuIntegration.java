package the_fireplace.mobrebirth.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import the_fireplace.mobrebirth.MobRebirth;
import the_fireplace.mobrebirth.config.MobSettings;
import the_fireplace.mobrebirth.config.MobSettingsManager;
import the_fireplace.mobrebirth.config.ModConfig;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
            List<Identifier> mobids = Lists.newArrayList(Registry.ENTITY_TYPE.getIds()).stream().filter(id -> Registry.ENTITY_TYPE.get(id).isSummonable()).collect(Collectors.toList());
            List<String> mobidStrs = Lists.newArrayListWithCapacity(mobids.size()-MobSettingsManager.getAllSettings().size());
            for(Identifier id: mobids)
                if(!MobSettingsManager.getCustomIds().contains(id))
                    mobidStrs.add(id.toString());

            general.addEntry(entryBuilder.startStringDropdownMenu(new TranslatableText("text.config.mobrebirth.option.addCustomMob"), "")
                    .setSelections(mobidStrs)
                    .setSuggestionMode(false)
                    .setDefaultValue("")
                    .setTooltip(genDescriptionTranslatables("text.config.mobrebirth.option.addCustomMob.desc", 2))
                    .setSaveConsumer(newValue -> {
                        if(!newValue.isEmpty())
                            MobSettingsManager.createSettings(new Identifier(newValue));
                    })
                    .setErrorSupplier(value ->
                            MobSettingsManager.getCustomIds().contains(new Identifier(value))
                            ? Optional.of(new TranslatableText("text.config.mobrebirth.option.addCustomMob.err"))
                            : Optional.empty())
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
        //noinspection UnstableApiUsage
        Identifier id = mobSettings.id.isEmpty() ? MobSettingsManager.getIdentifier(Files.getNameWithoutExtension(mobSettings.getFile().getParent()), mobSettings.getFile()) : new Identifier(mobSettings.id);
        if(!isDefault && id == null) {
            MobRebirth.LOGGER.error("Unable to get id for mob with settings at "+mobSettings.getFile().toString());
            return;
        }
        ConfigCategory settingsCategory = builder.getOrCreateCategory(isDefault ? new TranslatableText("text.config.mobrebirth.defaultSettings") : new TranslatableText("text.config.mobrebirth.mobSettings", id.toString()));
        settingsCategory.setDescription(new StringVisitable[]{(isDefault ? new TranslatableText("text.config.mobrebirth.defaultSettings.desc") : new TranslatableText("text.config.mobrebirth.mobSettings.desc", id.toString()))});
        if(!isDefault) {
            settingsCategory.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("text.config.mobrebirth.option.enabled"), mobSettings.enabled == null || mobSettings.enabled)
                .setDefaultValue(true)
                .setTooltip(genDescriptionTranslatables("text.config.mobrebirth.option.enabled.desc", 2))
                .setSaveConsumer(newValue -> mobSettings.enabled = newValue)
                .build());
        }
        settingsCategory.addEntry(entryBuilder.startIntSlider(new TranslatableText("text.config.mobrebirth.option.rebirthChance"), (int)(mobSettings.rebirthChance*1000), 0, 1000)
            .setDefaultValue((int)(new MobSettings().rebirthChance*1000))
            .setTextGetter(val -> Text.of(String.format("%.1f", val/10d)+"%"))
            .setTooltip(new TranslatableText("text.config.mobrebirth.option.rebirthChance.desc"))
            .setSaveConsumer(newValue -> mobSettings.rebirthChance = ((double)newValue)/1000d)
            .build());
        settingsCategory.addEntry(entryBuilder.startIntSlider(new TranslatableText("text.config.mobrebirth.option.multiMobChance"), (int)(mobSettings.multiMobChance*1000), 0, 1000)
                .setDefaultValue((int)(new MobSettings().multiMobChance*1000))
                .setTextGetter(val -> Text.of(String.format("%.1f", val/10d)+"%"))
                .setTooltip(new TranslatableText("text.config.mobrebirth.option.multiMobChance.desc"))
                .setSaveConsumer(newValue -> mobSettings.multiMobChance = ((double)newValue)/1000d)
                .build());
        settingsCategory.addEntry(entryBuilder.startSelector(new TranslatableText("text.config.mobrebirth.option.multiMobMode"), new String[]{"continuous", "per-mob", "all"}, mobSettings.multiMobMode)
            .setDefaultValue(new MobSettings().multiMobMode)
            .setTooltip(genDescriptionTranslatables("text.config.mobrebirth.option.multiMobMode.desc", 5))
            .setSaveConsumer(newValue -> mobSettings.multiMobMode = newValue)
            .build());
        settingsCategory.addEntry(entryBuilder.startIntField(new TranslatableText("text.config.mobrebirth.option.multiMobCount"), mobSettings.multiMobCount)
            .setDefaultValue(new MobSettings().multiMobCount)
            .setTooltip(genDescriptionTranslatables("text.config.mobrebirth.option.multiMobCount.desc", 2))
            .setSaveConsumer(newValue -> mobSettings.multiMobCount = newValue)
            .build());
        settingsCategory.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("text.config.mobrebirth.option.rebornAsEggs"), mobSettings.rebornAsEggs)
            .setDefaultValue(new MobSettings().rebornAsEggs)
            .setTooltip(new TranslatableText("text.config.mobrebirth.option.rebornAsEggs.desc"))
            .setSaveConsumer(newValue -> mobSettings.rebornAsEggs = newValue)
            .build());
        settingsCategory.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("text.config.mobrebirth.option.rebirthFromPlayer"), mobSettings.rebirthFromPlayer)
            .setDefaultValue(new MobSettings().rebirthFromPlayer)
            .setTooltip(new TranslatableText("text.config.mobrebirth.option.rebirthFromPlayer.desc"))
            .setSaveConsumer(newValue -> mobSettings.rebirthFromPlayer = newValue)
            .build());
        settingsCategory.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("text.config.mobrebirth.option.rebirthFromNonPlayer"), mobSettings.rebirthFromNonPlayer)
            .setDefaultValue(new MobSettings().rebirthFromNonPlayer)
            .setTooltip(new TranslatableText("text.config.mobrebirth.option.rebirthFromNonPlayer.desc"))
            .setSaveConsumer(newValue -> mobSettings.rebirthFromNonPlayer = newValue)
            .build());
        settingsCategory.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("text.config.mobrebirth.option.preventSunlightDamage"), mobSettings.preventSunlightDamage)
            .setDefaultValue(new MobSettings().preventSunlightDamage)
            .setTooltip(genDescriptionTranslatables("text.config.mobrebirth.option.preventSunlightDamage.desc", 2))
            .setSaveConsumer(newValue -> mobSettings.preventSunlightDamage = newValue)
            .build());
        settingsCategory.addEntry(entryBuilder.startStrList(new TranslatableText("text.config.mobrebirth.option.biomeList"), mobSettings.biomeList)
            .setDefaultValue(new MobSettings().biomeList)
            .setTooltip(genDescriptionTranslatables("text.config.mobrebirth.option.biomeList.desc", 2))
            .setSaveConsumer(newValue -> mobSettings.biomeList = newValue)
            .build());
        settingsCategory.addEntry(entryBuilder.startStrList(new TranslatableText("text.config.mobrebirth.option.rebornMobWeights"), mapToList(mobSettings.rebornMobWeights))
            .setDefaultValue(mapToList(new MobSettings().rebornMobWeights))
            .setTooltip(genDescriptionTranslatables("text.config.mobrebirth.option.rebornMobWeights.desc", 2))
            .setSaveConsumer(newValue -> mobSettings.rebornMobWeights = listToMap(newValue))
            .setErrorSupplier(strList -> {
                for(String str: strList)
                    if(!str.matches("([a-zA-Z_\\-0-9./]+(:[a-zA-Z_\\-0-9.]+)?)?=[0-9]+"))
                        return Optional.of(new TranslatableText("text.config.mobrebirth.option.rebornMobWeights.err", str));
                return Optional.empty();
            })
            .build());
        if(!isDefault) {
            settingsCategory.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("text.config.mobrebirth.option.deleteCustomMob"), false)
                    .setDefaultValue(false)
                    .setTooltip(genDescriptionTranslatables("text.config.mobrebirth.option.deleteCustomMob.desc", 2))
                    .setSaveConsumer(newValue -> {
                        if(newValue)
                            MobSettingsManager.deleteSettings(id, mobSettings);
                    })
                    .build());
        }
    }

    private static Text[] genDescriptionTranslatables(String baseKey, int count) {
        List<Text> texts = Lists.newArrayList();
        for(int i=0;i<count;i++)
            texts.add(new TranslatableText(baseKey+"."+i));
        return texts.toArray(new Text[0]);
    }

    public static final String MAP_SEPARATOR = "=";
    
    private static List<String> mapToList(Map<String, Integer> map) {
        List<String> stringList = Lists.newArrayList();
        for(Map.Entry<String, Integer> entry: map.entrySet())
            stringList.add(entry.getKey()+MAP_SEPARATOR+entry.getValue().toString());
        return stringList;
    }

    private static Map<String, Integer> listToMap(List<String> list) {
        Map<String, Integer> map = Maps.newHashMap();
        for(String str: list) {
            String[] parts = str.split(MAP_SEPARATOR);
            map.put(parts[0], Integer.parseInt(parts[1]));
        }
        return map;
    }
}

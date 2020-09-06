package the_fireplace.mobrebirth;

import com.google.common.collect.Maps;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.EntityType;
import net.minecraft.item.SpawnEggItem;
import the_fireplace.mobrebirth.config.MobSettingsManager;
import the_fireplace.mobrebirth.config.ModConfig;

import java.util.Map;

public class MobRebirth implements ModInitializer {
	public static final String MODID = "mobrebirth";
	static final Map<EntityType<?>, SpawnEggItem> spawnEggs = Maps.newHashMap();
	public static ModConfig config;

	@Override
	public void onInitialize() {
		AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
		config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

		MobSettingsManager.init();

		for(SpawnEggItem egg: SpawnEggItem.getAll())
			spawnEggs.put(egg.getEntityType(null), egg);
	}
}

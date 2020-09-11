package the_fireplace.mobrebirth;

import com.google.common.collect.Maps;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.EntityType;
import net.minecraft.item.SpawnEggItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import the_fireplace.mobrebirth.config.MobSettingsManager;
import the_fireplace.mobrebirth.config.ModConfig;

import java.util.Map;

public class MobRebirth implements ModInitializer {
	public static final String MODID = "mobrebirth";
	static final Map<EntityType<?>, SpawnEggItem> spawnEggs = Maps.newHashMap();
	public static ModConfig config;
	public static Logger LOGGER = LogManager.getLogger(MODID);

	@Override
	public void onInitialize() {
		config = ModConfig.load();
		config.save();

		MobSettingsManager.init();

		for(SpawnEggItem egg: SpawnEggItem.getAll())
			spawnEggs.put(egg.getEntityType(null), egg);
	}
}

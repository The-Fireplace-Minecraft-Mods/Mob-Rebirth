package the_fireplace.mobrebirth.fabric;

import com.google.common.collect.Maps;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.EntityType;
import net.minecraft.item.SpawnEggItem;

import java.util.Map;

public class MobRebirth implements ModInitializer {
	public static final String MODID = "mobrebirth";
	public static Map<EntityType<?>, SpawnEggItem> spawnEggs = Maps.newHashMap();
	@Override
	public void onInitialize() {
		Config.load();
		Events.register();
		for(SpawnEggItem egg: SpawnEggItem.getAll())
			spawnEggs.put(egg.getEntityType(null), egg);
	}
}

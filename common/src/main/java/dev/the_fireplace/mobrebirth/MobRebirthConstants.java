package dev.the_fireplace.mobrebirth;

import com.google.common.collect.Maps;
import com.google.inject.Injector;
import dev.the_fireplace.annotateddi.api.Injectors;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.SpawnEggItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public final class MobRebirthConstants
{
    public static final String MODID = "mobrebirth";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public static Injector getInjector() {
        return Injectors.INSTANCE.getAutoInjector(MODID);
    }


    private static final Map<EntityType<?>, SpawnEggItem> spawnEggs = Maps.newHashMap();

    public static Map<EntityType<?>, SpawnEggItem> getSpawnEggs() {
        if (spawnEggs.isEmpty()) {
            for (SpawnEggItem egg : SpawnEggItem.eggs()) {
                spawnEggs.put(egg.getType(null), egg);
            }
        }

        return spawnEggs;
    }
}

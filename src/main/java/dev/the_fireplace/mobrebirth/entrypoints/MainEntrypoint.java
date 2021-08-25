package dev.the_fireplace.mobrebirth.entrypoints;

import com.google.common.collect.Maps;
import com.google.inject.Injector;
import dev.the_fireplace.annotateddi.api.entrypoints.DIModInitializer;
import dev.the_fireplace.mobrebirth.config.MobSettingsManager;
import net.minecraft.entity.EntityType;
import net.minecraft.item.SpawnEggItem;

import java.util.Map;

public final class MainEntrypoint implements DIModInitializer {
    public static final Map<EntityType<?>, SpawnEggItem> spawnEggs = Maps.newHashMap();

    @Override
    public void onInitialize(Injector diContainer) {
        diContainer.getInstance(MobSettingsManager.class).init();

        for (SpawnEggItem egg : SpawnEggItem.getAll()) {
            spawnEggs.put(egg.getEntityType(null), egg);
        }
    }
}

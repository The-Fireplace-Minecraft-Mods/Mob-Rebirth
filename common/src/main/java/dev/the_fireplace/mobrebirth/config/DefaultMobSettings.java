package dev.the_fireplace.mobrebirth.config;

import dev.the_fireplace.lib.api.io.interfaces.access.SimpleBuffer;
import dev.the_fireplace.lib.api.io.interfaces.access.StorageReadBuffer;
import dev.the_fireplace.lib.api.lazyio.injectables.ConfigStateManager;
import dev.the_fireplace.lib.api.lazyio.interfaces.Config;
import dev.the_fireplace.mobrebirth.MobRebirthConstants;
import dev.the_fireplace.mobrebirth.util.MapListConverter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Singleton
public final class DefaultMobSettings extends MobSettings implements Config {

    @Inject
    public DefaultMobSettings(ConfigStateManager configStateManager) {
        configStateManager.initialize(this);
    }

    @Override
    public String getId() {
        return MobRebirthConstants.MODID + "_defaultMobSettings";
    }

    @Override
    public void afterReload(SimpleBuffer changedValues) {
        super.afterReload(changedValues);
    }

    @Override
    public void readFrom(StorageReadBuffer storageReadBuffer) {
        enabled = storageReadBuffer.readBool("enabled", true);
        rebirthChance = storageReadBuffer.readDouble("rebirthChance", 0.1);
        extraMobChance = storageReadBuffer.readDouble("extraMobChance", 0.01);
        extraMobMode = storageReadBuffer.readString("extraMobMode", "continuous");
        extraMobCount = storageReadBuffer.readInt("extraMobCount", 1);
        rebornAsEggs = storageReadBuffer.readBool("rebornAsEggs", false);
        rebirthFromPlayer = storageReadBuffer.readBool("rebirthFromPlayer", true);
        rebirthFromNonPlayer = storageReadBuffer.readBool("rebirthFromNonPlayer", true);
        preventSunlightDamage = storageReadBuffer.readBool("preventSunlightDamage", false);
        biomeList = new ArrayList<>();
        rebornMobWeights = new HashMap<>();
        List<String> rebornMobWeightList = new ArrayList<>();

        for (String key : storageReadBuffer.getKeys()) {
            if (key.startsWith("biomeList-")) {
                biomeList.add(storageReadBuffer.readString(key, ""));
            } else if (key.startsWith("rebornMobWeights-")) {
                rebornMobWeightList.add(storageReadBuffer.readString(key, ""));
            }
        }

        if (biomeList.isEmpty() && !storageReadBuffer.readBool("biomeListIsEmpty", false)) {
            biomeList.add("*");
        }

        if (rebornMobWeightList.isEmpty() && !storageReadBuffer.readBool("rebornMobWeightListIsEmpty", false)) {
            rebornMobWeights.put("", 1);
        } else {
            rebornMobWeights.putAll(MapListConverter.listToMap(rebornMobWeightList));
        }
    }
}

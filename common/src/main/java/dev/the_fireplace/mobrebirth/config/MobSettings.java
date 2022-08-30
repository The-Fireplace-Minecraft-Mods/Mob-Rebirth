package dev.the_fireplace.mobrebirth.config;

import dev.the_fireplace.lib.api.io.interfaces.access.StorageReadBuffer;
import dev.the_fireplace.lib.api.io.interfaces.access.StorageWriteBuffer;
import dev.the_fireplace.lib.api.lazyio.interfaces.HierarchicalConfig;
import dev.the_fireplace.mobrebirth.MobRebirthConstants;
import dev.the_fireplace.mobrebirth.util.MapListConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MobSettings implements HierarchicalConfig {
    protected boolean enabled;
    protected double rebirthChance;
    protected double extraMobChance;
    protected String extraMobMode;
    protected int extraMobCount;
    protected boolean rebornAsEggs;
    protected boolean rebirthFromPlayer;
    protected boolean rebirthFromNonPlayer;
    protected boolean preventSunlightDamage;
    protected List<String> biomeList;
    protected Map<String, Integer> rebornMobWeights;

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public MobSettings clone() {
        MobSettings clone = new MobSettings();
        clone.enabled = enabled;
        clone.rebirthChance = rebirthChance;
        clone.extraMobChance = extraMobChance;
        clone.extraMobMode = extraMobMode;
        clone.extraMobCount = extraMobCount;
        clone.rebornAsEggs = rebornAsEggs;
        clone.rebirthFromPlayer = rebirthFromPlayer;
        clone.rebirthFromNonPlayer = rebirthFromNonPlayer;
        clone.preventSunlightDamage = preventSunlightDamage;
        clone.biomeList = biomeList;
        clone.rebornMobWeights = rebornMobWeights;
        return clone;
    }

    @Override
    public void readFrom(StorageReadBuffer storageReadBuffer) {
        MobSettings defaultSettings = MobRebirthConstants.getInjector().getInstance(DefaultMobSettings.class);
        enabled = storageReadBuffer.readBool("enabled", defaultSettings.isEnabled());
        rebirthChance = storageReadBuffer.readDouble("rebirthChance", defaultSettings.getRebirthChance());
        extraMobChance = storageReadBuffer.readDouble("extraMobChance", defaultSettings.getExtraMobChance());
        extraMobMode = storageReadBuffer.readString("extraMobMode", defaultSettings.getExtraMobMode());
        extraMobCount = storageReadBuffer.readInt("extraMobCount", defaultSettings.getExtraMobCount());
        rebornAsEggs = storageReadBuffer.readBool("rebornAsEggs", defaultSettings.isRebornAsEggs());
        rebirthFromPlayer = storageReadBuffer.readBool("rebirthFromPlayer", defaultSettings.isRebirthFromPlayer());
        rebirthFromNonPlayer = storageReadBuffer.readBool("rebirthFromNonPlayer", defaultSettings.isRebirthFromNonPlayer());
        preventSunlightDamage = storageReadBuffer.readBool("preventSunlightDamage", defaultSettings.isPreventSunlightDamage());
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
            biomeList.addAll(defaultSettings.getBiomeList());
        }

        if (rebornMobWeightList.isEmpty() && !storageReadBuffer.readBool("rebornMobWeightListIsEmpty", false)) {
            rebornMobWeights.putAll(defaultSettings.getRebornMobWeights());
        } else {
            rebornMobWeights.putAll(MapListConverter.listToMap(rebornMobWeightList));
        }
    }

    @Override
    public void writeTo(StorageWriteBuffer storageWriteBuffer) {
        storageWriteBuffer.writeBool("enabled", enabled);
        storageWriteBuffer.writeDouble("rebirthChance", rebirthChance);
        storageWriteBuffer.writeDouble("extraMobChance", extraMobChance);
        storageWriteBuffer.writeString("extraMobMode", extraMobMode);
        storageWriteBuffer.writeInt("extraMobCount", extraMobCount);
        storageWriteBuffer.writeBool("rebornAsEggs", rebornAsEggs);
        storageWriteBuffer.writeBool("rebirthFromPlayer", rebirthFromPlayer);
        storageWriteBuffer.writeBool("rebirthFromNonPlayer", rebirthFromNonPlayer);
        storageWriteBuffer.writeBool("preventSunlightDamage", preventSunlightDamage);
        //TODO This is horrible, rewrite with FL 6.0.0
        for (String biome : biomeList) {
            storageWriteBuffer.writeString("biomeList-" + biome, biome);
        }
        if (biomeList.isEmpty()) {
            storageWriteBuffer.writeBool("biomeListIsEmpty", true);
        }
        //TODO This is even worse. Rewrite with FL 6.0.0
        List<String> rebornMobWeightList = MapListConverter.mapToList(rebornMobWeights);
        for (String weight : rebornMobWeightList) {
            storageWriteBuffer.writeString("rebornMobWeights-" + weight, weight);
        }
        if (rebornMobWeightList.isEmpty()) {
            storageWriteBuffer.writeBool("rebornMobWeightListIsEmpty", true);
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public double getRebirthChance() {
        return rebirthChance;
    }

    public void setRebirthChance(double rebirthChance) {
        this.rebirthChance = rebirthChance;
    }

    public double getExtraMobChance() {
        return extraMobChance;
    }

    public void setExtraMobChance(double extraMobChance) {
        this.extraMobChance = extraMobChance;
    }

    public String getExtraMobMode() {
        return extraMobMode;
    }

    public void setExtraMobMode(String extraMobMode) {
        this.extraMobMode = extraMobMode;
    }

    public int getExtraMobCount() {
        return extraMobCount;
    }

    public void setExtraMobCount(int extraMobCount) {
        this.extraMobCount = extraMobCount;
    }

    public boolean isRebornAsEggs() {
        return rebornAsEggs;
    }

    public void setRebornAsEggs(boolean rebornAsEggs) {
        this.rebornAsEggs = rebornAsEggs;
    }

    public boolean isRebirthFromPlayer() {
        return rebirthFromPlayer;
    }

    public void setRebirthFromPlayer(boolean rebirthFromPlayer) {
        this.rebirthFromPlayer = rebirthFromPlayer;
    }

    public boolean isRebirthFromNonPlayer() {
        return rebirthFromNonPlayer;
    }

    public void setRebirthFromNonPlayer(boolean rebirthFromNonPlayer) {
        this.rebirthFromNonPlayer = rebirthFromNonPlayer;
    }

    public boolean isPreventSunlightDamage() {
        return preventSunlightDamage;
    }

    public void setPreventSunlightDamage(boolean preventSunlightDamage) {
        this.preventSunlightDamage = preventSunlightDamage;
    }

    public List<String> getBiomeList() {
        return biomeList;
    }

    public void setBiomeList(List<String> biomeList) {
        this.biomeList = biomeList;
    }

    public Map<String, Integer> getRebornMobWeights() {
        return rebornMobWeights;
    }

    public void setRebornMobWeights(Map<String, Integer> rebornMobWeights) {
        this.rebornMobWeights = rebornMobWeights;
    }
}

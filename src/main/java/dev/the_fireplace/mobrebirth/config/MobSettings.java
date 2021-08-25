package dev.the_fireplace.mobrebirth.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class MobSettings {
    public File getFile() {
        return file;
    }

    File file;
    @Nullable
    public Boolean enabled = null;
    public String id = "";
    public double rebirthChance = 0.1;
    public double extraMobChance = 0.01;
    public String extraMobMode = "continuous";
    public int extraMobCount = 1;
    public boolean rebornAsEggs = false;
    public boolean rebirthFromPlayer = true;
    public boolean rebirthFromNonPlayer = true;
    public boolean preventSunlightDamage = false;
    public List<String> biomeList = Lists.newArrayList("*");
    public Map<String, Integer> rebornMobWeights = Maps.newHashMap(Collections.singletonMap("", 1));

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public MobSettings clone() {
        MobSettings clone = new MobSettings();
        clone.enabled = enabled;
        clone.id = id;
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
}

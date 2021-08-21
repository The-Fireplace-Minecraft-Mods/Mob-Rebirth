package the_fireplace.mobrebirth.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import javax.annotation.Nonnull;
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
    @Nonnull
    public String id = "";
    @Nonnull
    public Double rebirthChance = 0.1;
    @Nonnull
    public Double extraMobChance = 0.01;
    @Nonnull
    public String extraMobMode = "continuous";
    @Nonnull
    public Integer extraMobCount = 1;
    @Nonnull
    public Boolean rebornAsEggs = false;
    @Nonnull
    public Boolean rebirthFromPlayer = true;
    @Nonnull
    public Boolean rebirthFromNonPlayer = true;
    @Nonnull
    public Boolean preventSunlightDamage = false;
    @Nonnull
    public List<String> biomeList = Lists.newArrayList("*");
    @Nonnull
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

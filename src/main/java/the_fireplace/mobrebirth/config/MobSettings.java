package the_fireplace.mobrebirth.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MobSettings {
    public Boolean enabled = null;
    public String id = "";
    public Double rebirthChance = 0.1;
    public Double multiMobChance = 0.01;
    public String multiMobMode = "continuous";
    public Integer multiMobCount = 1;
    public Boolean rebornAsEggs = false;
    public Boolean rebirthFromPlayer = true;
    public Boolean rebirthFromNonPlayer = true;
    public Boolean preventSunlightDamage = false;
    public List<String> biomeList = Lists.newArrayList("*");
    public Map<String, Integer> rebornMobWeights = Maps.newHashMap(Collections.singletonMap("", 1));

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public MobSettings clone() {
        MobSettings clone = new MobSettings();
        clone.enabled = enabled;
        clone.id = id;
        clone.rebirthChance = rebirthChance;
        clone.multiMobChance = multiMobChance;
        clone.multiMobMode = multiMobMode;
        clone.multiMobCount = multiMobCount;
        clone.rebornAsEggs = rebornAsEggs;
        clone.rebirthFromPlayer = rebirthFromPlayer;
        clone.rebirthFromNonPlayer = rebirthFromNonPlayer;
        clone.preventSunlightDamage = preventSunlightDamage;
        clone.biomeList = biomeList;
        clone.rebornMobWeights = rebornMobWeights;
        return clone;
    }
}

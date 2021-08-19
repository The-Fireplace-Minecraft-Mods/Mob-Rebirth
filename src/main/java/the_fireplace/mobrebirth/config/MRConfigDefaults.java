package the_fireplace.mobrebirth.config;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import the_fireplace.mobrebirth.domain.config.ConfigValues;

import javax.inject.Singleton;

@Implementation(name="default")
@Singleton
public final class MRConfigDefaults implements ConfigValues {
    @Override
    public boolean getAllowBossRebirth() {
        return false;
    }

    @Override
    public boolean getAllowSlimeRebirth() {
        return true;
    }

    @Override
    public boolean getAllowAnimalRebirth() {
        return true;
    }

    @Override
    public boolean getVanillaRebirthOnly() {
        return false;
    }

    @Override
    public boolean getUseCompactCustomMobConfigs() {
        return true;
    }
}

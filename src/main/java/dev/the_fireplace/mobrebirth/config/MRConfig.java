package dev.the_fireplace.mobrebirth.config;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.lib.api.io.interfaces.access.StorageReadBuffer;
import dev.the_fireplace.lib.api.io.interfaces.access.StorageWriteBuffer;
import dev.the_fireplace.lib.api.lazyio.injectables.ConfigStateManager;
import dev.the_fireplace.lib.api.lazyio.interfaces.Config;
import dev.the_fireplace.mobrebirth.MobRebirthConstants;
import dev.the_fireplace.mobrebirth.domain.config.ConfigValues;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Implementation("dev.the_fireplace.mobrebirth.domain.config.ConfigValues")
@Singleton
public final class MRConfig implements Config, ConfigValues {
    private final ConfigValues defaultConfig;

    private boolean allowBossRebirth;
    private boolean allowSlimeRebirth;
    private boolean allowAnimalRebirth;
    private boolean onlyAllowVanillaMobRebirth;

    @Inject
    public MRConfig(ConfigStateManager configStateManager, @Named("default") ConfigValues defaultConfig) {
        this.defaultConfig = defaultConfig;
        configStateManager.initialize(this);
    }

    @Override
    public String getId() {
        return MobRebirthConstants.MODID;
    }

    @Override
    public void readFrom(StorageReadBuffer buffer) {
        allowBossRebirth = buffer.readBool("allowBossRebirth", defaultConfig.getAllowBossRebirth());
        allowSlimeRebirth = buffer.readBool("allowSlimeRebirth", defaultConfig.getAllowSlimeRebirth());
        allowAnimalRebirth = buffer.readBool("allowAnimalRebirth", defaultConfig.getAllowAnimalRebirth());
        onlyAllowVanillaMobRebirth = buffer.readBool("onlyAllowVanillaMobRebirth", defaultConfig.getVanillaRebirthOnly());
    }

    @Override
    public void writeTo(StorageWriteBuffer buffer) {
        buffer.writeBool("allowBossRebirth", allowBossRebirth);
        buffer.writeBool("allowSlimeRebirth", allowSlimeRebirth);
        buffer.writeBool("allowAnimalRebirth", allowAnimalRebirth);
        buffer.writeBool("onlyAllowVanillaMobRebirth", onlyAllowVanillaMobRebirth);
    }

    @Override
    public boolean getAllowBossRebirth() {
        return allowBossRebirth;
    }

    @Override
    public boolean getAllowSlimeRebirth() {
        return allowSlimeRebirth;
    }

    @Override
    public boolean getAllowAnimalRebirth() {
        return allowAnimalRebirth;
    }

    @Override
    public boolean getVanillaRebirthOnly() {
        return onlyAllowVanillaMobRebirth;
    }

    public void setAllowBossRebirth(boolean allowBossRebirth) {
        this.allowBossRebirth = allowBossRebirth;
    }

    public void setAllowSlimeRebirth(boolean allowSlimeRebirth) {
        this.allowSlimeRebirth = allowSlimeRebirth;
    }

    public void setAllowAnimalRebirth(boolean allowAnimalRebirth) {
        this.allowAnimalRebirth = allowAnimalRebirth;
    }

    public void setOnlyAllowVanillaMobRebirth(boolean onlyAllowVanillaMobRebirth) {
        this.onlyAllowVanillaMobRebirth = onlyAllowVanillaMobRebirth;
    }
}

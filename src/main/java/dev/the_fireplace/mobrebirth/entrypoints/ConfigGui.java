package dev.the_fireplace.mobrebirth.entrypoints;

import com.google.inject.Injector;
import dev.the_fireplace.lib.api.client.entrypoints.ConfigGuiEntrypoint;
import dev.the_fireplace.lib.api.client.interfaces.ConfigGuiRegistry;
import dev.the_fireplace.mobrebirth.config.MRConfigScreenFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class ConfigGui implements ConfigGuiEntrypoint
{
    @Override
    public void registerConfigGuis(Injector injector, ConfigGuiRegistry configGuiRegistry) {
        configGuiRegistry.register(injector.getInstance(MRConfigScreenFactory.class)::getConfigScreen);
    }
}

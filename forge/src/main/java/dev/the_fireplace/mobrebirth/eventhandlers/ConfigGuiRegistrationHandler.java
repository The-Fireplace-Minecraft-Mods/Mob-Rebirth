package dev.the_fireplace.mobrebirth.eventhandlers;

import dev.the_fireplace.lib.api.events.ConfigScreenRegistration;
import dev.the_fireplace.mobrebirth.MobRebirthConstants;
import dev.the_fireplace.mobrebirth.config.MRConfigScreenFactory;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.inject.Inject;

public final class ConfigGuiRegistrationHandler
{
    private final MRConfigScreenFactory configScreenFactory;

    @Inject
    public ConfigGuiRegistrationHandler(MRConfigScreenFactory configScreenFactory) {
        this.configScreenFactory = configScreenFactory;
    }

    @SubscribeEvent
    public void registerConfigGui(ConfigScreenRegistration configScreenRegistration) {
        configScreenRegistration.getConfigGuiRegistry().register(MobRebirthConstants.MODID, configScreenFactory::getConfigScreen);
    }
}

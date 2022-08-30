package dev.the_fireplace.mobrebirth.entrypoints;

import com.google.inject.Injector;
import dev.the_fireplace.lib.api.events.FLEventBus;
import dev.the_fireplace.mobrebirth.MobRebirthConstants;
import dev.the_fireplace.mobrebirth.eventhandlers.ConfigGuiRegistrationHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmllegacy.network.FMLNetworkConstants;

@Mod("mobrebirth")
public final class Forge
{
    public Forge() {
        Injector injector = MobRebirthConstants.getInjector();
        DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> {
            FLEventBus.BUS.register(injector.getInstance(ConfigGuiRegistrationHandler.class));
            return null;
        });

        // Register as optional on both sides
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
    }
}

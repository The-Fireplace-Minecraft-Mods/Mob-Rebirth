package dev.the_fireplace.mobrebirth.entrypoints;

import dev.the_fireplace.annotateddi.api.DIContainer;
import dev.the_fireplace.mobrebirth.MobRebirthConstants;
import dev.the_fireplace.mobrebirth.config.MRConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;

import java.util.function.Function;

@Environment(EnvType.CLIENT)
public final class ModMenuEntrypoint implements ModMenuApi {
    private final MRConfigScreenFactory configScreenFactory = DIContainer.get().getInstance(MRConfigScreenFactory.class);

    @Override
    public String getModId() {
        return MobRebirthConstants.MODID;
    }

    @Override
    public Function<Screen, ? extends Screen> getConfigScreenFactory() {
        return configScreenFactory::getConfigScreen;
    }
}

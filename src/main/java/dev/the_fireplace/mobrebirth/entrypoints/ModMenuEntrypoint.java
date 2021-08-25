package dev.the_fireplace.mobrebirth.entrypoints;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.the_fireplace.annotateddi.api.DIContainer;
import dev.the_fireplace.mobrebirth.config.MRConfigScreenFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;

@Environment(EnvType.CLIENT)
public final class ModMenuEntrypoint implements ModMenuApi {
    private final MRConfigScreenFactory configScreenFactory = DIContainer.get().getInstance(MRConfigScreenFactory.class);

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (ConfigScreenFactory<Screen>) configScreenFactory::getConfigScreen;
    }
}

package dev.the_fireplace.mobrebirth.compat.modmenu;

import com.terraformersmc.modmenu.gui.ModsScreen;
import dev.the_fireplace.annotateddi.api.DIContainer;
import dev.the_fireplace.mobrebirth.MobRebirthConstants;
import dev.the_fireplace.mobrebirth.config.MRConfigScreenFactory;
import dev.the_fireplace.mobrebirth.mixin.clothconfig.AbstractConfigScreenAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

/**
 * Counteract Mod Menu's old caching mechanism (MM 1.16.9 and earlier), which shouldn't be used with Cloth Config GUIs and causes problems for how Mob Rebirth handles custom mob settings
 * See also: https://github.com/TerraformersMC/ModMenu/issues/254
 */
@Environment(EnvType.CLIENT)
public final class OldModMenuCompat implements ModMenuCompat {
    @Override
    public void forceReloadConfigGui() {
        MRConfigScreenFactory configScreenFactory = DIContainer.get().getInstance(MRConfigScreenFactory.class);
        Screen screen = MinecraftClient.getInstance().currentScreen;
        if (screen instanceof AbstractConfigScreenAccessor) {
            Screen parent = ((AbstractConfigScreenAccessor) screen).getParent();
            if (parent instanceof ModsScreen) {
                ((ModsScreen) parent).getConfigScreenCache().put(MobRebirthConstants.MODID, configScreenFactory.getConfigScreen(parent));
            }
        }
    }
}

package dev.the_fireplace.mobrebirth.config;

import com.google.common.collect.Sets;
import dev.the_fireplace.lib.api.lazyio.injectables.ConfigStateManager;
import dev.the_fireplace.lib.api.lazyio.injectables.HierarchicalConfigManagerFactory;
import dev.the_fireplace.lib.api.lazyio.interfaces.NamespacedHierarchicalConfigManager;
import dev.the_fireplace.mobrebirth.MobRebirthConstants;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public final class MobSettingsManager {

    private final NamespacedHierarchicalConfigManager<MobSettings> hierarchicalConfigManager;
    private final ConfigStateManager configStateManager;
    private final DefaultMobSettings defaultSettings;

    private final Set<Identifier> allowedEntityIdentifiers;

    @Inject
    public MobSettingsManager(
        HierarchicalConfigManagerFactory hierarchicalConfigManagerFactory,
        ConfigStateManager configStateManager,
        DefaultMobSettings defaultSettings
    ) {
        this.defaultSettings = defaultSettings;
        this.configStateManager = configStateManager;
        this.allowedEntityIdentifiers = Registry.ENTITY_TYPE.getIds().stream().filter(id -> Registry.ENTITY_TYPE.get(id).isSummonable()).collect(Collectors.toSet());
        this.hierarchicalConfigManager = hierarchicalConfigManagerFactory.createNamespaced(
            MobRebirthConstants.MODID + "_customMobSettings",
            defaultSettings,
            allowedEntityIdentifiers
        );
    }

    public Collection<Identifier> getAllowedMobIds() {
        return allowedEntityIdentifiers;
    }

    public Collection<Identifier> getMobIdsWithCustomSettings() {
        return hierarchicalConfigManager.getCustoms();
    }

    public Collection<Identifier> getMobIdsWithoutCustomSettings() {
        Set<Identifier> idsWithoutSettings = Sets.newHashSet(allowedEntityIdentifiers);
        idsWithoutSettings.removeAll(getMobIdsWithCustomSettings());

        return idsWithoutSettings;
    }

    public MobSettings getSettings(Identifier mobId) {
        return hierarchicalConfigManager.get(mobId);
    }

    public boolean isCustom(Identifier mobId) {
        return hierarchicalConfigManager.isCustom(mobId);
    }

    public void addCustom(Identifier mobId, MobSettings settings) {
        hierarchicalConfigManager.addCustom(mobId, settings);
    }

    public boolean deleteCustom(Identifier mobId) {
        return hierarchicalConfigManager.deleteCustom(mobId);
    }

    public void saveAll() {
        this.configStateManager.save(defaultSettings);
        this.hierarchicalConfigManager.saveAllCustoms();
    }
}

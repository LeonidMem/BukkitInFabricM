package ru.leonidm.bukkitinfabric.mixin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import ru.leonidm.bukkitinfabric.bukkit.FabricBukkitPlugin;
import ru.leonidm.bukkitinfabric.interfaces.ExtendedPluginManager;
import ru.leonidm.bukkitinfabric.interfaces.InitializablePlugin;
import ru.leonidm.bukkitinfabric.utils.OpenUnsafe;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

@Mixin(SimplePluginManager.class)
public abstract class SimplePluginManagerMixin implements ExtendedPluginManager {

    @Shadow(remap = false)
    @Final
    private List<Plugin> plugins;
    @Shadow(remap = false)
    @Final
    private Map<String, Plugin> lookupNames;
    @Shadow(remap = false)
    @Final
    private Map<Pattern, PluginLoader> fileAssociations;

    private JavaPluginLoader javaPluginLoader;

    @Override
    @NotNull
    public Plugin loadPlugin(@NotNull Class<? extends FabricBukkitPlugin> pluginClass, @NotNull PluginDescriptionFile description,
                             @NotNull File dataFolder, @NotNull File jarFolder) {
        Plugin plugin;
        try {
            plugin = (Plugin) OpenUnsafe.get().allocateInstance(pluginClass);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        plugins.add(plugin);
        lookupNames.put(description.getName().toLowerCase(Locale.ENGLISH), plugin);

        if (javaPluginLoader == null) {
            javaPluginLoader = (JavaPluginLoader) fileAssociations.values().stream()
                    .filter(JavaPluginLoader.class::isInstance)
                    .findAny()
                    .orElseThrow();
        }

        ((InitializablePlugin) plugin).initialize(
                javaPluginLoader, Bukkit.getServer(), description, dataFolder, jarFolder, pluginClass.getClassLoader()
        );

        return plugin;
    }
}

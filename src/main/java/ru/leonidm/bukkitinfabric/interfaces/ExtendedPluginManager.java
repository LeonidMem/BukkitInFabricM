package ru.leonidm.bukkitinfabric.interfaces;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import ru.leonidm.bukkitinfabric.bukkit.FabricBukkitPlugin;

import java.io.File;

public interface ExtendedPluginManager extends PluginManager {

    @NotNull
    Plugin loadPlugin(@NotNull Class<? extends FabricBukkitPlugin> pluginClass, @NotNull PluginDescriptionFile description,
                      @NotNull File dataFolder, @NotNull File jarFolder);

    void ep$enablePlugin(@NotNull Plugin plugin);

}

package ru.leonidm.bukkitinfabric.interfaces;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public interface InitializablePlugin extends Plugin {

    void initialize(@NotNull PluginLoader loader, @NotNull Server server, @NotNull PluginDescriptionFile description,
                    @NotNull File dataFolder, @NotNull File file, @NotNull ClassLoader classLoader);

    void ip$setEnabled(boolean enabled);

}

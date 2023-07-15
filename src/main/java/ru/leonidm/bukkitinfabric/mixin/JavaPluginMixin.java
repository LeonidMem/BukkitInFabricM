package ru.leonidm.bukkitinfabric.mixin;

import org.bukkit.Server;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import ru.leonidm.bukkitinfabric.interfaces.InitializablePlugin;

import java.io.File;

@Mixin(JavaPlugin.class)
public abstract class JavaPluginMixin implements InitializablePlugin {

    @Shadow(remap = false)
    abstract void init(@NotNull PluginLoader loader, @NotNull Server server, @NotNull PluginDescriptionFile description,
                       @NotNull File dataFolder, @NotNull File file, @NotNull ClassLoader classLoader);

    @Shadow(remap = false)
    protected abstract void setEnabled(boolean enabled);

    @Override
    public void initialize(@NotNull PluginLoader loader, @NotNull Server server, @NotNull PluginDescriptionFile description,
                           @NotNull File dataFolder, @NotNull File file, @NotNull ClassLoader classLoader) {
        init(loader, server, description, dataFolder, file, classLoader);
    }

    @Override
    public void ip$setEnabled(boolean enabled) {
        setEnabled(enabled);
    }
}

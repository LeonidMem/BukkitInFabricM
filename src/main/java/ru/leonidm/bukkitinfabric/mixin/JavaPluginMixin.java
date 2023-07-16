package ru.leonidm.bukkitinfabric.mixin;

import org.bukkit.Server;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.leonidm.bukkitinfabric.BukkitInFabricM;
import ru.leonidm.bukkitinfabric.interfaces.ExtendedPlugin;

import java.io.File;

@Mixin(JavaPlugin.class)
public abstract class JavaPluginMixin implements ExtendedPlugin {

    @Shadow(remap = false)
    abstract void init(@NotNull PluginLoader loader, @NotNull Server server, @NotNull PluginDescriptionFile description,
                       @NotNull File dataFolder, @NotNull File file, @NotNull ClassLoader classLoader);

    @Shadow(remap = false)
    protected abstract void setEnabled(boolean enabled);

    @Redirect(method = "<init>()V", at = @At(value = "INVOKE", target = "Ljava/lang/Class;getClassLoader()Ljava/lang/ClassLoader;"),
            remap = false)
    @NotNull
    private ClassLoader initClassLoader(Class<?> instance) {
        if (BukkitInFabricM.isFabricPlugin(getClass())) {
            return BukkitInFabricM.getDummyClassLoader();
        }

        return instance.getClassLoader();
    }

    @Override
    public void bifm$initialize(@NotNull PluginLoader loader, @NotNull Server server, @NotNull PluginDescriptionFile description,
                                @NotNull File dataFolder, @NotNull File file, @NotNull ClassLoader classLoader) {
        init(loader, server, description, dataFolder, file, classLoader);
    }

    @Override
    public void bifm$setEnabled(boolean enabled) {
        setEnabled(enabled);
    }
}

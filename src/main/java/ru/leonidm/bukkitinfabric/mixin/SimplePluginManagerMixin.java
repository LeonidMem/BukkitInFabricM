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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.leonidm.bukkitinfabric.BukkitInFabricM;
import ru.leonidm.bukkitinfabric.interfaces.ExtendedPlugin;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

@Mixin(SimplePluginManager.class)
public abstract class SimplePluginManagerMixin {

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

    @Inject(method = "loadPlugin", at = @At("HEAD"), remap = false, cancellable = true)
    private void loadPlugin(@NotNull File file, CallbackInfoReturnable<Plugin> cir) throws Exception {
        PluginDescriptionFile description = BukkitInFabricM.getPluginDescription(file);
        if (description == null) {
            return;
        }

        String mainClassName = description.getMain();
        Class<?> mainClass = Class.forName(mainClassName, true, BukkitInFabricM.class.getClassLoader());

        File dataFolder = new File(file.getParent(), description.getName() + "/");

        Plugin plugin;
        try {
            plugin = mainClass.asSubclass(Plugin.class).getConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        if (javaPluginLoader == null) {
            javaPluginLoader = (JavaPluginLoader) fileAssociations.values().stream()
                    .filter(JavaPluginLoader.class::isInstance)
                    .findAny()
                    .orElseThrow();
        }

        ((ExtendedPlugin) plugin).bifm$initialize(
                javaPluginLoader, Bukkit.getServer(), description, dataFolder, file, mainClass.getClassLoader()
        );

        plugins.add(plugin);
        lookupNames.put(description.getName().toLowerCase(Locale.ENGLISH), plugin);


        cir.setReturnValue(plugin);
    }
}

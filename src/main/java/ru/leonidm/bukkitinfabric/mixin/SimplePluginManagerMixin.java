package ru.leonidm.bukkitinfabric.mixin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.UnknownDependencyException;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.leonidm.bukkitinfabric.BukkitInFabricM;
import ru.leonidm.bukkitinfabric.interfaces.InitializablePlugin;
import ru.leonidm.bukkitinfabric.utils.OpenUnsafe;

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

    @Shadow(remap = false)
    @Nullable
    public abstract Plugin loadPlugin(@NotNull File file) throws InvalidPluginException, UnknownDependencyException;

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
            plugin = (Plugin) OpenUnsafe.get().allocateInstance(mainClass);
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
                javaPluginLoader, Bukkit.getServer(), description, dataFolder, file, mainClass.getClassLoader()
        );

        cir.setReturnValue(plugin);
    }
}

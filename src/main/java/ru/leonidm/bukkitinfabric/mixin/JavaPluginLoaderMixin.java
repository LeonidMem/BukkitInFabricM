package ru.leonidm.bukkitinfabric.mixin;

import org.bukkit.Server;
import org.bukkit.UnsafeValues;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.leonidm.bukkitinfabric.BukkitInFabricM;
import ru.leonidm.bukkitinfabric.interfaces.ExtendedPlugin;

import java.util.logging.Level;

@Mixin(JavaPluginLoader.class)
public abstract class JavaPluginLoaderMixin {

    @Shadow(remap = false)
    @Final
    Server server;

    @Inject(method = "enablePlugin", at = @At("HEAD"), cancellable = true, remap = false)
    private void enablePlugin(Plugin plugin, CallbackInfo ci) {
        if (!BukkitInFabricM.isFabricPlugin(plugin.getClass())) {
            return;
        }

        ci.cancel();

        if (!plugin.isEnabled()) {
            String enableMsg = "Enabling " + plugin.getDescription().getFullName();
            if (UnsafeValues.isLegacyPlugin(plugin)) {
                enableMsg = enableMsg + "*";
            }

            plugin.getLogger().info(enableMsg);
            ExtendedPlugin jPlugin = (ExtendedPlugin) plugin;

            try {
                jPlugin.bifm$setEnabled(true);
            } catch (Throwable var6) {
                server.getLogger().log(Level.SEVERE, "Error occurred while enabling " + plugin.getDescription().getFullName() + " (Is it up to date?)", var6);
                server.getPluginManager().disablePlugin(jPlugin);
            }
        }
    }
}

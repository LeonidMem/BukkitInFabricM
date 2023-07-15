package ru.leonidm.bukkitinfabric.mixin;

import net.fabricmc.loader.impl.util.UrlUtil;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.SimplePluginManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.leonidm.bukkitinfabric.BukkitInFabricM;
import ru.leonidm.bukkitinfabric.bukkit.FabricBukkitPlugin;
import ru.leonidm.bukkitinfabric.interfaces.ExtendedPluginManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Mixin(CraftServer.class)
public class CraftServerMixin {

    private static final Logger LOGGER = LoggerFactory.getLogger("BukkitInFabricM");

    @Shadow(remap = false)
    @Final
    private SimplePluginManager pluginManager;

    @Inject(method = "loadPlugins", at = @At("TAIL"), remap = false)
    private void loadPlugins(CallbackInfo ci) {
        for (var pair : BukkitInFabricM.getPluginsToLoad()) {
            Class<?> modInitializerClass = pair.getFirst();
            PluginDescriptionFile description = pair.getSecond();

            if (pluginManager.isPluginEnabled(description.getName())) {
                continue;
            }

            Path jarPath = UrlUtil.getCodeSource(modInitializerClass);
            Path dataPath = jarPath.getParent().resolve(description.getName() + "/");

            try {
                Files.createDirectories(dataPath);

                String mainClassName = description.getMain();
                Class<?> mainClass = Class.forName(mainClassName, true, modInitializerClass.getClassLoader());

                if (!FabricBukkitPlugin.class.isAssignableFrom(mainClass)) {
                    LOGGER.error("Plugin class {} does not implement {} interface, did not load it",
                            mainClassName, FabricBukkitPlugin.class.getSimpleName());
                    continue;
                }

                saveEmptyJar(dataPath);

                ((ExtendedPluginManager) (PluginManager) pluginManager).loadPlugin(mainClass.asSubclass(FabricBukkitPlugin.class),
                        description, dataPath.toFile(), jarPath.toFile());
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private void saveEmptyJar(@NotNull Path path) {
        byte[] bytes = new byte[22];
        bytes[0] = 0x50;
        bytes[1] = 0x4B;
        bytes[2] = 0x05;
        bytes[3] = 0x06;

        try {
            if (!Files.exists(path)) {
                Files.write(path, bytes);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}

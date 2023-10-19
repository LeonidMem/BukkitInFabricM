package ru.leonidm.bukkitinfabric.mixin;

import com.mojang.datafixers.util.Pair;
import io.papermc.paper.plugin.provider.PluginProvider;
import io.papermc.paper.plugin.provider.type.spigot.SpigotPluginProvider;
import io.papermc.paper.plugin.storage.ServerPluginProviderStorage;
import io.papermc.paper.plugin.storage.SimpleProviderStorage;
import org.bukkit.plugin.PluginDescriptionFile;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import ru.leonidm.bukkitinfabric.BukkitInFabricM;

import java.nio.file.Path;
import java.util.List;
import java.util.jar.JarFile;

@Mixin(SimpleProviderStorage.class)
public class SimpleProviderStorageMixin {

    @Shadow(remap = false)
    @Final
    private static Logger LOGGER;

    @ModifyArg(
            method = "enter",
            at = @At(value = "INVOKE", target = "Lio/papermc/paper/plugin/entrypoint/strategy/ProviderLoadingStrategy;loadProviders(Ljava/util/List;Lio/papermc/paper/plugin/entrypoint/dependency/MetaDependencyTree;)Ljava/util/List;"),
            index = 0, remap = false
    )
    private <P> List<PluginProvider<P>> enter(List<PluginProvider<P>> providers) {
        if (!((Object) this instanceof ServerPluginProviderStorage)) {
            return providers;
        }

        List<Pair<Path, PluginDescriptionFile>> pluginsToLoad = BukkitInFabricM.getPluginsToLoad();

        for (var pair : pluginsToLoad) {
            try {
                JarFile jarFile = new JarFile(pair.getFirst().toFile());
                SpigotPluginProvider provider = SpigotPluginProvider.FACTORY.build(jarFile, pair.getSecond(), pair.getFirst());
                providers.add((PluginProvider<P>) provider);
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }

        return providers;
    }
}

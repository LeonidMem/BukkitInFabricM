package ru.leonidm.bukkitinfabric.mixin;

import com.mojang.datafixers.util.Pair;
import org.bukkit.plugin.SimplePluginManager;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import ru.leonidm.bukkitinfabric.BukkitInFabricM;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Mixin(targets = {
        "org.bukkit.craftbukkit.v1_14_R1.CraftServer",
        "org.bukkit.craftbukkit.v1_15_R1.CraftServer",
        "org.bukkit.craftbukkit.v1_16_R1.CraftServer",
        "org.bukkit.craftbukkit.v1_16_R2.CraftServer",
        "org.bukkit.craftbukkit.v1_16_R3.CraftServer",
        "org.bukkit.craftbukkit.v1_17_R1.CraftServer",
        "org.bukkit.craftbukkit.v1_17_R2.CraftServer",
        "org.bukkit.craftbukkit.v1_18_R2.CraftServer",
        "org.bukkit.craftbukkit.v1_18_R2.CraftServer",
        "org.bukkit.craftbukkit.v1_19_R1.CraftServer",
        "org.bukkit.craftbukkit.v1_19_R2.CraftServer",
        "org.bukkit.craftbukkit.v1_19_R3.CraftServer",
        "org.bukkit.craftbukkit.V1_20_R1.CraftServer",
}, remap = false)
@Pseudo
public abstract class CraftServerMixin {

    @Shadow(remap = false)
    @Final
    private SimplePluginManager pluginManager;

    private boolean loadedFabricPlugins = false;

    @ModifyArg(method = "loadPlugins", at = @At(value = "INVOKE", target = "Lorg/bukkit/plugin/SimplePluginManager;loadPlugins(Ljava/io/File;Ljava/util/List;)[Lorg/bukkit/plugin/Plugin;"),
            index = 1, remap = false)
    @NotNull
    private List<File> loadPluginsHead(@NotNull List<File> extraPluginJars) {
        if (loadedFabricPlugins) {
            return extraPluginJars;
        }

        loadedFabricPlugins = true;

        extraPluginJars = new ArrayList<>(extraPluginJars);
        BukkitInFabricM.getPluginsToLoad()
                .stream()
                .map(Pair::getFirst)
                .map(Path::toFile)
                .forEach(extraPluginJars::add);
        return extraPluginJars;
    }
}

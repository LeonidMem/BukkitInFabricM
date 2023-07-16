package ru.leonidm.bukkitinfabric.mixin;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.PluginClassLoader;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.leonidm.bukkitinfabric.interfaces.ExtendedPluginClassLoader;

@Mixin(PluginClassLoader.class)
public class PluginClassLoaderMixin implements ExtendedPluginClassLoader {

    private boolean dummy;

    @Inject(method = "initialize", at = @At("HEAD"), cancellable = true, remap = false)
    private void initialize(@NotNull JavaPlugin javaPlugin, @NotNull CallbackInfo ci) {
        if (dummy) {
            ci.cancel();
        }
    }

    @Override
    public boolean bifm$isDummy() {
        return dummy;
    }

    @Override
    public void bifm$setDummy(boolean dummy) {
        this.dummy = dummy;
    }
}

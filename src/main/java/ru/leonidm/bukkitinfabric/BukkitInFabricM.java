package ru.leonidm.bukkitinfabric;

import com.mojang.datafixers.util.Pair;
import lombok.SneakyThrows;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.impl.util.UrlUtil;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class BukkitInFabricM implements ModInitializer {

    private static final List<Pair<Class<?>, PluginDescriptionFile>> PLUGINS_TO_LOAD = new ArrayList<>();

    private final Logger logger = LoggerFactory.getLogger(BukkitInFabricM.class.getSimpleName());

    @Override
    @SneakyThrows
    public void onInitialize() {
        logger.info("Enabled!");
    }

    public static void loadPlugin(@NotNull Class<?> modInitializerClass) {
        Path jarPath = UrlUtil.getCodeSource(modInitializerClass);

        try (ZipFile zipFile = new ZipFile(jarPath.toFile())) {
            ZipEntry zipEntry = zipFile.getEntry("plugin.yml");
            if (zipEntry == null) {
                throw new IllegalStateException("Cannot find plugin.yml file in " + jarPath);
            }

            try (InputStream inputStream = zipFile.getInputStream(zipEntry)) {
                PluginDescriptionFile pluginDescriptionFile = new PluginDescriptionFile(inputStream);

                PLUGINS_TO_LOAD.add(Pair.of(modInitializerClass, pluginDescriptionFile));
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @NotNull
    @Unmodifiable
    public static List<Pair<Class<?>, PluginDescriptionFile>> getPluginsToLoad() {
        return Collections.unmodifiableList(PLUGINS_TO_LOAD);
    }
}

package io.github.how_bout_no.completeconfig.io;

import io.github.how_bout_no.completeconfig.CompleteConfig;
import io.github.how_bout_no.completeconfig.data.Config;
import io.github.how_bout_no.completeconfig.extensions.CompleteConfigExtension;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Log4j2
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public final class ConfigSource {

    private static final TypeSerializerCollection GLOBAL_TYPE_SERIALIZERS;
    private static final Set<ConfigSource> sources = new HashSet<>();

    static {
        TypeSerializerCollection.Builder builder = TypeSerializerCollection.builder();
        switch (FMLEnvironment.dist) {
            case CLIENT:
                builder.registerAll(ClientSerializers.COLLECTION);
                break;
        }
        GLOBAL_TYPE_SERIALIZERS = builder.build();
    }

    @EqualsAndHashCode.Include
    @ToString.Include
    @Getter
    private final String modID;
    @EqualsAndHashCode.Include
    @ToString.Include
    @Getter
    private final String[] branch;
    private final HoconConfigurationLoader loader;

    public ConfigSource(String modID, String[] branch) {
        this.modID = modID;
        this.branch = branch;
        if (!sources.add(this)) {
            throw new IllegalArgumentException("A config of the mod " + modID + " with the specified branch " + Arrays.toString(branch) + " already exists");
        }
        Path path = FMLPaths.CONFIGDIR.get();
        String[] subPath = ArrayUtils.add(branch, 0, modID);
        subPath[subPath.length - 1] = subPath[subPath.length - 1] + ".conf";
        for (String child : subPath) {
            path = path.resolve(child);
        }
        loader = HoconConfigurationLoader.builder()
                .path(path)
                .defaultOptions(options -> options.serializers(builder -> {
                    builder.registerAll(GLOBAL_TYPE_SERIALIZERS);
                    for (TypeSerializerCollection collection : CompleteConfig.collectExtensions(CompleteConfigExtension.class, CompleteConfigExtension::getTypeSerializers)) {
                        builder.registerAll(collection);
                    }
                }))
                .build();
    }

    public void load(Config config) {
        try {
            CommentedConfigurationNode root = loader.load();
            if (!root.virtual()) {
                config.apply(root);
            }
        } catch (ConfigurateException e) {
            log.error("[CompleteConfig] Failed to load config from file", e);
        }
        save(config);
    }

    public void save(Config config) {
        CommentedConfigurationNode root = loader.createNode();
        config.fetch(root);
        try {
            loader.save(root);
        } catch (ConfigurateException e) {
            log.error("[CompleteConfig] Failed to save config to file", e);
        }
    }

}

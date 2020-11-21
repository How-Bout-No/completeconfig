package me.lortseam.completeconfig;

import com.google.gson.*;
import me.lortseam.completeconfig.api.ConfigCategory;
import me.lortseam.completeconfig.api.ConfigOwner;
import me.lortseam.completeconfig.gui.GuiBuilder;
import me.lortseam.completeconfig.gui.cloth.ClothGuiBuilder;
import me.lortseam.completeconfig.serialization.CollectionSerializer;
import me.lortseam.completeconfig.serialization.EntrySerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public final class ConfigHandler {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(CollectionSerializer.TYPE, new CollectionSerializer())
            .registerTypeAdapter(EntrySerializer.TYPE, new EntrySerializer())
            .setPrettyPrinting()
            .create();
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<Class<? extends ConfigOwner>, ConfigHandler> HANDLERS = new HashMap<>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (ConfigHandler handler : HANDLERS.values()) {
                handler.save();
            }
        }));
    }

    private final String modID;
    private final Path jsonPath;
    protected Config config;
    @Environment(EnvType.CLIENT)
    private GuiBuilder guiBuilder;

    /**
     * Gets the {@link ConfigHandler} for the specified owner if that owner created a config before.
     *
     * @param owner The owner class of the config
     * @return The {@link ConfigHandler} if one was found or else an empty result
     */
    public static Optional<ConfigHandler> of(Class<? extends ConfigOwner> owner) {
        return Optional.ofNullable(HANDLERS.get(owner));
    }

    ConfigHandler(String modID, String[] branch) {
        this.modID = modID;
        branch = ArrayUtils.add(branch, 0, modID);
        branch[branch.length - 1] = branch[branch.length - 1] + ".json";
        jsonPath = Paths.get(FabricLoader.getInstance().getConfigDir().toString(), branch);
    }

    void register(Class<? extends ConfigOwner> owner, List<ConfigCategory> topLevelCategories) {
        if (HANDLERS.containsKey(owner)) {
            throw new IllegalArgumentException("The specified owner already created a config!");
        }
        HANDLERS.put(owner, this);
        config = new Config(modID, topLevelCategories, load());
    }

    private JsonElement load() {
        if(Files.exists(jsonPath)) {
            try {
                return GSON.fromJson(new FileReader(jsonPath.toString()), JsonElement.class);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (JsonSyntaxException e) {
                LOGGER.warn("[CompleteConfig] An error occurred while trying to load the config " + jsonPath.toString());
            }
        }
        return JsonNull.INSTANCE;
    }

    /**
     * Sets a custom GUI builder.
     * @param guiBuilder The GUI builder for the mod's config
     */
    @Environment(EnvType.CLIENT)
    public void setGuiBuilder(GuiBuilder guiBuilder) {
        Objects.requireNonNull(guiBuilder);
        this.guiBuilder = guiBuilder;
    }

    /**
     * Generates the configuration GUI.
     * @param parentScreen The parent screen
     * @return The generated configuration screen
     */
    @Environment(EnvType.CLIENT)
    public Screen buildScreen(Screen parentScreen) {
        if (guiBuilder == null) {
            if (FabricLoader.getInstance().isModLoaded("cloth-config2")) {
                guiBuilder = new ClothGuiBuilder();
            } else {
                throw new UnsupportedOperationException("No GUI builder provided");
            }
        }
        return guiBuilder.buildScreen(parentScreen, config, this::save);
    }

    /**
     * Saves the config to a save file.
     */
    //TODO: Needs public access?
    public void save() {
        if (!Files.exists(jsonPath)) {
            try {
                Files.createDirectories(jsonPath.getParent());
                Files.createFile(jsonPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try(Writer writer = Files.newBufferedWriter(jsonPath)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
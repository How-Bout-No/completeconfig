package io.github.how_bout_no.completeconfig.gui;

import io.github.how_bout_no.completeconfig.CompleteConfigUtil;
import io.github.how_bout_no.completeconfig.data.Config;
import io.github.how_bout_no.completeconfig.gui.cloth.ClothConfigScreenBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class ConfigScreenBuilder {

    private static final ConfigScreenBuilder fallback = CompleteConfigUtil.isModLoaded("cloth-config2") ? new ClothConfigScreenBuilder() : null;
    private static final Map<String, ConfigScreenBuilder> mainBuilders = new HashMap<>();

    /**
     * Sets the main screen builder for a mod. The main screen builder will be used to build the config screen if no
     * custom builder was specified.
     *
     * @param modID         the mod's ID
     * @param screenBuilder the screen builder
     */
    public static void setMain(String modID, ConfigScreenBuilder screenBuilder) {
        mainBuilders.put(modID, screenBuilder);
    }

    public static Optional<ConfigScreenBuilder> getMain(String modID) {
        if (mainBuilders.containsKey(modID)) {
            return Optional.of(mainBuilders.get(modID));
        }
        return Optional.ofNullable(fallback);
    }

    /**
     * Tries to build a screen based on a config. Fails if no main screen builder was specified for the mod and no
     * fallback builder exists.
     *
     * @param parentScreen the parent screen
     * @param config       the config to build the screen of
     * @return the built screen, if successful
     */
    public static Screen tryBuild(Screen parentScreen, Config config) {
        return getMain(config.getMod().getModId()).orElseThrow(() -> {
            return new UnsupportedOperationException("No screen builder found for mod " + config.getMod().getModId());
        }).build(parentScreen, config);
    }

    /**
     * Builds a screen based on a config.
     *
     * @param parentScreen the parent screen
     * @param config       the config to build the screen of
     * @return the built screen
     */
    @OnlyIn(Dist.CLIENT)
    public abstract Screen build(Screen parentScreen, Config config);

}

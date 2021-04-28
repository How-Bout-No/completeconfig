package io.github.how_bout_no.completeconfig.data;

import io.github.how_bout_no.completeconfig.api.ConfigContainer;
import io.github.how_bout_no.completeconfig.data.text.TranslationIdentifier;
import io.github.how_bout_no.completeconfig.io.ConfigSource;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;

import java.util.*;

@Log4j2
public final class Config extends BaseCollection {

    private static final Map<String, Config> mainConfigs = new HashMap<>();
    private static final Set<Config> saveOnExitConfigs = new HashSet<>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (Config config : saveOnExitConfigs) {
                config.save();
            }
        }));
    }

    public static Map<String, Config> getMainConfigs() {
        return Collections.unmodifiableMap(mainConfigs);
    }

    /**
     * Creates a new config builder for the specified mod.
     *
     * @param modID the ID of the mod creating the config
     */
    public static Builder builder(@NonNull String modID) {
        if (!ModList.get().isLoaded(modID)) {
            throw new IllegalArgumentException("Mod " + modID + " is not loaded");
        }
        return new Builder(modID);
    }

    private final ConfigSource source;

    private Config(ConfigSource source, LinkedHashSet<ConfigContainer> children) {
        super(TranslationIdentifier.from(source));
        this.source = source;
        resolve(children);
    }

    public ModContainer getMod() {
        return ModLoadingContext.get().getActiveContainer();
    }

    public TranslationIdentifier getTranslation(boolean includeBranch) {
        if (includeBranch) {
            return translation.append(source.getBranch());
        } else {
            return translation;
        }
    }

    private void load() {
        source.load(this);
    }

    public void save() {
        source.save(this);
    }

    @Log4j2
    public final static class Builder {

        private final String modID;
        private String[] branch = new String[0];
        private final LinkedHashSet<ConfigContainer> children = new LinkedHashSet<>();
        private boolean main;
        private boolean saveOnExit;

        private Builder(String modID) {
            this.modID = modID;
        }

        /**
         * Sets the branch. Every config of a mod needs a unique branch, therefore setting a branch is only required
         * when using more than one config.
         *
         * <p>The branch determines the location of the config's save file.
         *
         * @param branch the branch
         * @return this builder
         */
        public Builder setBranch(@NonNull String[] branch) {
            Arrays.stream(branch).forEach(Objects::requireNonNull);
            this.branch = branch;
            return this;
        }

        /**
         * Adds one or more containers to the config.
         *
         * @param containers one or more containers
         * @return this builder
         */
        public Builder add(@NonNull ConfigContainer... containers) {
            Arrays.stream(containers).forEach(Objects::requireNonNull);
            for (ConfigContainer container : containers) {
                if (!children.add(container)) {
                    throw new IllegalArgumentException("Duplicate container");
                }
            }
            return this;
        }

        /**
         * Sets a flag to save the config when the game closes.
         *
         * @return this builder
         */
        public Builder saveOnExit() {
            saveOnExit = true;
            return this;
        }

        /**
         * Registers the config as main mod config.
         *
         * @return this builder
         */
        public Builder main() {
            main = true;
            return this;
        }

        /**
         * Creates and loads the config.
         *
         * @return the created config
         */
        public Config build() {
            if (children.isEmpty()) {
                log.warn("[CompleteConfig] Mod " + modID + " tried to create an empty config");
                return null;
            }
            Config config = new Config(new ConfigSource(modID, branch), children);
            if (config.isEmpty()) {
                log.warn("[CompleteConfig] Config of " + config.source + " is empty");
                return null;
            }
            config.load();
            if (main || branch.length == 0 && !mainConfigs.containsKey(modID)) {
                mainConfigs.put(modID, config);
            }
            if (saveOnExit) {
                saveOnExitConfigs.add(config);
            }
            return config;
        }

    }

}

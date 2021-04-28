package io.github.how_bout_no.completeconfig.gui;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.how_bout_no.completeconfig.data.Config;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class ModMenuIntegration implements ModMenuApi {

    @Override
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
        Map<String, ConfigScreenFactory<?>> factories = new HashMap<>();
        for (Map.Entry<String, Config> entry : Config.getMainConfigs().entrySet()) {
            Optional<ConfigScreenBuilder> builder = ConfigScreenBuilder.getMain(entry.getKey());
            if (!builder.isPresent()) continue;
            factories.put(entry.getKey(), parentScreen -> builder.get().build(parentScreen, entry.getValue()));
        }
        return factories;
    }

}

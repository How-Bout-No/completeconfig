package io.github.how_bout_no.completeconfig.extensions;

import io.github.how_bout_no.completeconfig.gui.cloth.Provider;

import java.util.Collection;

public interface GuiExtension extends Extension {

    default Collection<Provider> getProviders() {
        return null;
    }

}

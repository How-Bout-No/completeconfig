package io.github.how_bout_no.completeconfig.extensions.clothbasicmath;

import io.github.how_bout_no.completeconfig.data.ColorEntry;
import io.github.how_bout_no.completeconfig.extensions.GuiExtension;
import io.github.how_bout_no.completeconfig.gui.cloth.Provider;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.math.Color;

import java.util.Collection;
import java.util.Collections;

final class ClothBasicMathGuiExtension implements GuiExtension {

    @Override
    public Collection<Provider> getProviders() {
        return Collections.singletonList(Provider.create(ColorEntry.class, (ColorEntry<Color> entry) -> ConfigEntryBuilder.create()
                        .startColorField(entry.getText(), entry.getValue())
                        .setAlphaMode(entry.isAlphaMode())
                        .setDefaultValue(entry.getDefaultValue().getColor())
                        .setTooltip(entry.getTooltip())
                        .setSaveConsumer2(entry::setValue),
                Color.class));
    }

}

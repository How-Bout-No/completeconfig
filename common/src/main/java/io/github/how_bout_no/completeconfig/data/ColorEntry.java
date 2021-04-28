package io.github.how_bout_no.completeconfig.data;

import io.github.how_bout_no.completeconfig.api.ConfigEntry;
import io.github.how_bout_no.completeconfig.data.entry.EntryOrigin;
import lombok.Getter;

public class ColorEntry<T> extends Entry<T> {

    @Getter
    private final boolean alphaMode;

    public ColorEntry(EntryOrigin origin, boolean alphaMode) {
        super(origin);
        this.alphaMode = alphaMode;
    }

    ColorEntry(EntryOrigin origin) {
        this(origin, origin.getAnnotation(ConfigEntry.Color.class).alphaMode());
    }

}

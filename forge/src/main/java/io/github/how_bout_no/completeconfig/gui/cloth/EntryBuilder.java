package io.github.how_bout_no.completeconfig.gui.cloth;

import io.github.how_bout_no.completeconfig.data.Entry;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.impl.builders.FieldBuilder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
@FunctionalInterface
public interface EntryBuilder<E extends Entry<?>> extends Function<E, FieldBuilder<?, ?>> {

    default AbstractConfigListEntry<?> build(E entry) {
        FieldBuilder<?, ?> builder = apply(entry);
        builder.requireRestart(entry.requiresRestart());
        return builder.build();
    }

}

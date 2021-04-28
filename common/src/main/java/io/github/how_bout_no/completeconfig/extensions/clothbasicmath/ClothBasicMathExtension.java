package io.github.how_bout_no.completeconfig.extensions.clothbasicmath;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import io.github.how_bout_no.completeconfig.data.ColorEntry;
import io.github.how_bout_no.completeconfig.data.entry.Transformation;
import io.github.how_bout_no.completeconfig.extensions.CompleteConfigExtension;
import io.github.how_bout_no.completeconfig.extensions.Extension;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.shedaniel.math.Color;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.util.Collection;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClothBasicMathExtension implements CompleteConfigExtension {

    @Override
    public TypeSerializerCollection getTypeSerializers() {
        return TypeSerializerCollection.builder()
                .registerExact(ColorSerializer.INSTANCE)
                .build();
    }

    @Override
    public Collection<Transformation> getTransformations() {
        return ImmutableList.of(
                Transformation.builder().byType(Color.class).transforms(origin -> new ColorEntry<>(origin, true))
        );
    }

    @Override
    public Set<Class<? extends Extension>> children() {
        return ImmutableSet.of(ClothBasicMathGuiExtension.class);
    }

}

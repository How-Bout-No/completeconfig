package io.github.how_bout_no.completeconfig.extensions;

import io.github.how_bout_no.completeconfig.data.entry.Transformation;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.util.Collection;

public interface CompleteConfigExtension extends Extension {

    default TypeSerializerCollection getTypeSerializers() {
        return null;
    }

    default Collection<Transformation> getTransformations() {
        return null;
    }

}

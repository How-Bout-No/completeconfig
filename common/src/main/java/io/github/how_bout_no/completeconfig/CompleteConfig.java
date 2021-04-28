package io.github.how_bout_no.completeconfig;

import io.github.how_bout_no.completeconfig.extensions.Extension;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CompleteConfig {
    private static final Set<Extension> extensions = new HashSet<>();

    public static <E extends Extension, T> Collection<T> collectExtensions(Class<E> extensionType, Function<E, T> function) {
        return extensions.stream().filter(extensionType::isInstance).map(extension -> function.apply((E) extension)).filter(Objects::nonNull).collect(Collectors.toSet());
    }
}

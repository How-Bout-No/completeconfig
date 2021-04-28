package io.github.how_bout_no.completeconfig.data.entry;

import io.github.how_bout_no.completeconfig.api.ConfigContainer;
import io.github.how_bout_no.completeconfig.data.text.TranslationIdentifier;
import io.github.how_bout_no.completeconfig.util.ReflectionUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
public final class EntryOrigin {

    @Getter
    protected final Field field;
    @Getter
    private final ConfigContainer parentObject;
    @Getter
    private final TranslationIdentifier parentTranslation;

    public Type getType() {
        return ReflectionUtils.getFieldType(field);
    }

    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return Objects.requireNonNull(field.getDeclaredAnnotation(annotationType), "Missing required transformation annotation");
    }

    public <A extends Annotation> Optional<A> getOptionalAnnotation(Class<A> annotationType) {
        return Optional.ofNullable(field.getDeclaredAnnotation(annotationType));
    }

}

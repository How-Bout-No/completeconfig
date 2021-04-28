package io.github.how_bout_no.completeconfig.gui.cloth;

import io.github.how_bout_no.completeconfig.data.Entry;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Type;
import java.util.function.Predicate;

@OnlyIn(Dist.CLIENT)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Provider {

    public static <E extends Entry<?>> Provider create(Class<E> entryType, EntryBuilder<? extends E> builder, Predicate<E> predicate, Type... types) {
        return new Provider(entry -> {
            if (entry.getClass() != (entryType != null ? entryType : Entry.class)) return false;
            if (types.length > 0 && !ArrayUtils.contains(types, entry.getType())) return false;
            return predicate.test((E) entry);
        }, builder);
    }

    public static <E extends Entry<?>> Provider create(Class<E> entryType, EntryBuilder<? extends E> builder, Type... types) {
        return create(entryType, builder, entry -> true, types);
    }

    public static Provider create(EntryBuilder<?> builder, Predicate<Entry<?>> predicate, Type... types) {
        return create(null, builder, predicate, types);
    }

    public static Provider create(EntryBuilder<?> builder, Type... types) {
        if (types.length == 0) {
            throw new IllegalArgumentException("Types must not be empty");
        }
        return create(builder, entry -> true, types);
    }

    private final Predicate<Entry<?>> predicate;
    @Getter
    private final EntryBuilder<?> builder;

    boolean test(Entry<?> entry) {
        return predicate.test(entry);
    }

}

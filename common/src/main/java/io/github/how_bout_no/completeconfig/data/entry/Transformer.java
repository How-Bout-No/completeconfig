package io.github.how_bout_no.completeconfig.data.entry;

import io.github.how_bout_no.completeconfig.data.Entry;

@FunctionalInterface
public interface Transformer {

    Entry<?> transform(EntryOrigin origin);

}

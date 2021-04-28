package io.github.how_bout_no.completeconfig.extensions;

import java.util.Set;

public interface Extension {

    default Set<Class<? extends Extension>> children() {
        return null;
    }

}

package io.github.how_bout_no.completeconfig.data.structure;

import org.spongepowered.configurate.CommentedConfigurationNode;

public interface DataPart {

    void apply(CommentedConfigurationNode node);

    void fetch(CommentedConfigurationNode node);

}

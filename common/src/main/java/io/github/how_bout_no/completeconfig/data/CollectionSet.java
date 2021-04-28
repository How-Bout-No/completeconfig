package io.github.how_bout_no.completeconfig.data;

import io.github.how_bout_no.completeconfig.api.ConfigGroup;
import io.github.how_bout_no.completeconfig.data.text.TranslationIdentifier;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class CollectionSet extends DataSet<Collection> {

    protected CollectionSet(TranslationIdentifier translation) {
        super(translation);
    }

    void resolve(ConfigGroup group) {
        String groupID = group.getID();
        Collection collection = new Collection(groupID, translation.append(groupID), group.getTooltipTranslationKeys(), group.getComment());
        collection.resolve(group);
        if (collection.isEmpty()) {
            log.warn("[CompleteConfig] Group " + groupID + " is empty");
            return;
        }
        add(collection);
    }

}

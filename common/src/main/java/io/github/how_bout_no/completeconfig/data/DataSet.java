package io.github.how_bout_no.completeconfig.data;

import io.github.how_bout_no.completeconfig.data.structure.DataPart;
import io.github.how_bout_no.completeconfig.data.text.TranslationIdentifier;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashSet;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class DataSet<T extends DataPart> extends LinkedHashSet<T> {

    protected final TranslationIdentifier translation;

}

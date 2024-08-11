package io.github.rxue.dictionary.vo;

import io.github.rxue.dictionary.jpa.entity.PartOfSpeech;

public interface Explanation {
    Long getId();
    PartOfSpeech getPartOfSpeech();
    String getDefinition();
}

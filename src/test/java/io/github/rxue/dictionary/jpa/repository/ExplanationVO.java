package io.github.rxue.dictionary.jpa.repository;

import io.github.rxue.dictionary.jpa.entity.PartOfSpeech;

record ExplanationVO(LexicalItemVO lexicalItemVO, String language, PartOfSpeech partOfSpeech, String definition) {
}

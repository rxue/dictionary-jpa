package io.github.rxue.dictionary.jpa.repository;

import io.github.rxue.dictionary.jpa.entity.PartOfSpeech;

import java.util.Locale;

record ExplanationVO(LexicalItemVO lexicalItemVO, Locale language, PartOfSpeech partOfSpeech, String definition) {
}

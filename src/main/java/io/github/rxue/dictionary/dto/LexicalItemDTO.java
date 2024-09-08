package io.github.rxue.dictionary.dto;

/**
 * Lexical Item data transfer object, which is used to be sent to RESTful API client
 */
public interface LexicalItemDTO {
    Long getId();
    String getLanguageTag();
    String getValue();
}

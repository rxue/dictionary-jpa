package io.github.rxue.dictionary.jpa.entity;

import io.github.rxue.dictionary.dto.LexicalItemDTO;
import io.github.rxue.dictionary.vo.Keyword;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;

import java.util.Locale;

@EqualsAndHashCode
@Entity
@Table(name = "lexical_item",
uniqueConstraints = {
        @UniqueConstraint(columnNames = {"language", "value"})
})
public class LexicalItem extends AbstractEntity implements Keyword, LexicalItemDTO {
    @Column(nullable=false)
    private Locale language;
    @Column(nullable=false)
    private String value;
    //Merely for testing purpose
    public LexicalItem(Long id) {
        this.id = id;
    }
    public LexicalItem() {
    }
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getLanguageTag() {
        return language.toLanguageTag();
    }

    @Override
    public Locale getLanguage() {
        return language;
    }

    @Override
    public String getValue() {
        return value;
    }

    public void setLanguage(Locale language) {
        this.language = language;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

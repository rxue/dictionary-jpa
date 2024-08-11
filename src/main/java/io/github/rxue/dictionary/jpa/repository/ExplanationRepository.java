package io.github.rxue.dictionary.jpa.repository;

import jakarta.persistence.EntityManager;
import io.github.rxue.dictionary.jpa.entity.ExplanationEntity;
import io.github.rxue.dictionary.vo.Keyword;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class ExplanationRepository {
    private final EntityManager entityManager;
    public ExplanationRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Find possible explanations on base of the given keyword and language
     *
     * @param keyword keyword
     * @param definitionLanguage language for the explanation
     * @return
     */
    public List<ExplanationEntity> findLike(Keyword keyword, Locale definitionLanguage) {
        String jpql = "select e from ExplanationEntity e where " +
                "e.lexicalItemEntity.language =: language and " +
                "e.lexicalItemEntity.value like :value and " +
                "e.language =: definitionLanguage";
        return entityManager.createQuery(jpql, ExplanationEntity.class)
                .setParameter("language", toLanguageLocale(keyword.getLanguage()))
                .setParameter("value", keyword.getValue() + "%")
                .setParameter("definitionLanguage", definitionLanguage)
                .getResultList();
    }

    public void cascadeUpdate(Collection<ExplanationEntity> explanationEntities) {
        explanationEntities.forEach(entityManager::merge);
    }

    public void deleteById(Long id) {
        ExplanationEntity managedExplanationEntity = entityManager.find(ExplanationEntity.class, id);
        entityManager.remove(managedExplanationEntity);
    }

    public void cascadeAdd(Collection<ExplanationEntity> explanationEntities) {
        explanationEntities.forEach(entityManager::merge);
    }
    private static Locale toLanguageLocale(String languageLocaleString) {
        String languageTag = languageLocaleString.replace("_", "-");
        return Locale.forLanguageTag(languageTag);
    }

}

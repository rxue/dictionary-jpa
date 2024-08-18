package io.github.rxue.dictionary.jpa.repository;

import jakarta.persistence.EntityManager;
import io.github.rxue.dictionary.jpa.entity.Explanation;
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
    public List<Explanation> findLike(Keyword keyword, Locale definitionLanguage) {
        String jpql = "select e from Explanation e where " +
                "e.lexicalItem.language =: language and " +
                "e.lexicalItem.value like :value and " +
                "e.language =: definitionLanguage";
        return entityManager.createQuery(jpql, Explanation.class)
                .setParameter("language", keyword.getLanguage())
                .setParameter("value", keyword.getValue() + "%")
                .setParameter("definitionLanguage", definitionLanguage)
                .getResultList();
    }

    public void cascadeUpdate(Collection<Explanation> explanationEntities) {
        explanationEntities.forEach(entityManager::merge);
    }

    public void deleteById(Long id) {
        Explanation managedExplanationEntity = entityManager.find(Explanation.class, id);
        entityManager.remove(managedExplanationEntity);
    }

    public void cascadeAdd(Collection<Explanation> explanationEntities) {
        explanationEntities.forEach(entityManager::merge);
    }
    private static Locale toLanguageLocale(String languageLocaleString) {
        String languageTag = languageLocaleString.replace("_", "-");
        return Locale.forLanguageTag(languageTag);
    }

}

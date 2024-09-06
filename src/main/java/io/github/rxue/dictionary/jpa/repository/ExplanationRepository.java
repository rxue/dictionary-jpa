package io.github.rxue.dictionary.jpa.repository;

import jakarta.persistence.EntityManager;
import io.github.rxue.dictionary.jpa.entity.Explanation;
import io.github.rxue.dictionary.vo.Keyword;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class ExplanationRepository {
    private static final String JPQL_EXPLANATION_ALIAS = "e";
    private static final String JPQL_SELECT_EXPLANATION = "select " + JPQL_EXPLANATION_ALIAS + " from Explanation " + JPQL_EXPLANATION_ALIAS;
    private static final String JPQL_EXPLANATION_LANGUAGE_MATCH = JPQL_EXPLANATION_ALIAS + ".language =: definitionLanguage";
    private final EntityManager entityManager;
    public ExplanationRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Finds possible explanations on base of the given keyword and language
     *
     * @param keyword keyword
     * @param definitionLanguage language for the explanation
     * @return list of explanations
     */
    public List<Explanation> lazyFindLike(Keyword keyword, Locale definitionLanguage) {
        String jpql = JPQL_SELECT_EXPLANATION +
                " where " +
                "e.lexicalItem.language =: language and " +
                "e.lexicalItem.value like :value and " +
                JPQL_EXPLANATION_LANGUAGE_MATCH;
        return query(jpql, keyword, definitionLanguage);
    }


    public List<Explanation> findLike(Keyword keyword, Locale definitionLanguage) {
        String jpql = JPQL_SELECT_EXPLANATION + " left join fetch e.lexicalItem l " +
                " where " +
                "l.language =: language and " +
                "l.value like :value and " +
                JPQL_EXPLANATION_LANGUAGE_MATCH;
        return query(jpql, keyword, definitionLanguage);
    }
    private List<Explanation> query(String jpql, Keyword keyword, Locale definitionLanguage) {
        return entityManager.createQuery(jpql, Explanation.class)
                .setParameter("language", keyword.getLanguage())
                .setParameter("value", keyword.getValue() + "%")
                .setParameter("definitionLanguage", definitionLanguage)
                .getResultList();
    }

    /**
     * Updates the given collection of explanations in cascade
     *
     * @param explanationEntities
     */
    public void cascadeUpdate(Collection<Explanation> explanationEntities) {
        explanationEntities.forEach(entityManager::merge);
    }

    /**
     * Deletes an explanation
     * @param id the id of the explanation to delete
     */
    public void deleteById(Long id) {
        Explanation managedExplanationEntity = entityManager.find(Explanation.class, id);
        entityManager.remove(managedExplanationEntity);
    }

    /**
     * Adds the given collection of explanations in cascade
     *
     * @param explanationEntities
     */
    public void cascadeAdd(Collection<Explanation> explanationEntities) {
        explanationEntities.forEach(entityManager::merge);
    }

}

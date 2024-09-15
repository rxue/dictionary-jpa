package io.github.rxue.dictionary.jpa.repository;

import io.github.rxue.dictionary.jpa.entity.LexicalItem;
import jakarta.persistence.EntityManager;
import io.github.rxue.dictionary.jpa.entity.Explanation;
import io.github.rxue.dictionary.Keyword;

import java.util.*;

/**
 * NOTE! all parameters in methods cannot be null
 */
public class ExplanationRepository {
    private static final String JPQL_EXPLANATION_ALIAS = "e";
    private static final String JPQL_SELECT_EXPLANATION = "select " + JPQL_EXPLANATION_ALIAS + " from Explanation " + JPQL_EXPLANATION_ALIAS;
    private static final String JPQL_EXPLANATION_LANGUAGE_MATCH = JPQL_EXPLANATION_ALIAS + ".language =: definitionLanguage";
    private final EntityManager entityManager;
    public ExplanationRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Lazily finds possible explanations on base of the given keyword and language
     *
     * @param keyword keyword
     * @param definitionLanguage language for the explanation
     * @return list of explanations, NOTE! that they are the proxies of JPA entity due to lazy fetch and would cause serialization error when using Jsonb to convert results to json
     */
    public List<Explanation> lazyFindLike(Keyword keyword, Locale definitionLanguage) {
        String jpql = JPQL_SELECT_EXPLANATION +
                " where " +
                "e.lexicalItem.language =: language and " +
                "e.lexicalItem.value like :value and " +
                JPQL_EXPLANATION_LANGUAGE_MATCH;
        return query(jpql, keyword, definitionLanguage);
    }

    /**
     * Eagerly finds possible explanations on base of the given keyword and language
     *
     * @param keyword keyword
     * @param definitionLanguage language for the explanation
     * @return list of explanations
     */
    public List<Explanation> findLike(Keyword keyword, Locale definitionLanguage) {
        String jpql = JPQL_SELECT_EXPLANATION + " left join fetch e.lexicalItem " +
                " where " +
                "e.lexicalItem.language =: language and " +
                "e.lexicalItem.value like :value and " +
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
     * Adds the given collection of explanations (for one lexical item) in cascade
     *
     * @param explanationEntities
     */
    public List<Explanation> cascadeAdd(Collection<Explanation> explanationEntities) {
        Objects.requireNonNull(explanationEntities);
        if (explanationEntities.isEmpty())
            throw new IllegalArgumentException("No sense to add an empty collection of Explanations to persistence store");
        preVerifyAdd(explanationEntities);
        List<Explanation> result = new ArrayList<>();
        for (Explanation e : explanationEntities)
            result.add(entityManager.merge(e));
        postVerifyAdd(result);
        return result;
    }
    private static void preVerifyAdd(Collection<Explanation> explanationEntities) {
        Explanation anyNewExplanation = explanationEntities.stream().findAny().get();
        assert anyNewExplanation.getId() == null : "explanations to add have to be all new";
        final LexicalItem anyLexicalItem = anyNewExplanation.getLexicalItem();
        explanationEntities.forEach(e -> {
            assert anyLexicalItem == e.getLexicalItem() : "All lexical items used of the given collection of new explanations have to be identical";
        });
    }
    private static void postVerifyAdd(List<Explanation> result) {
        Explanation firstPersistedExplanation = result.get(0);
        LexicalItem lexicalItem = firstPersistedExplanation.getLexicalItem();
        assert lexicalItem.getId() != null;
        assert firstPersistedExplanation.getId() != null;
    }

}

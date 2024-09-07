package io.github.rxue.dictionary.jpa.repository;

import io.github.rxue.Util;
import jakarta.persistence.EntityManager;
import jakarta.transaction.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import io.github.rxue.dictionary.jpa.entity.Explanation;
import io.github.rxue.dictionary.jpa.entity.LexicalItem;
import io.github.rxue.dictionary.jpa.entity.PartOfSpeech;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ExplanationEntityRepositoryCreateIT extends AbstractITConfiguration {
    @AfterEach
    public void truncateTables() {
        ITUtil.truncateTables(preparedStatementExecutor);
    }
    private static void beginTransaction(UserTransaction tx) {
        try {
            tx.begin();
        } catch (NotSupportedException | SystemException e) {
            throw new RuntimeException("Transaction fails to begin", e);
        }
    }
    private static void commitTransaction(UserTransaction tx) {
        try {
            tx.commit();
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SystemException e) {
            throw new RuntimeException("Transaction fails to commit", e);
        }
    }

    @Test
    public void testCreate_addNewLexicalItemWithOneExplanation() {
        //PREPARE
        LexicalItem l = new LexicalItem();
        l.setLanguage(Locale.ENGLISH);
        l.setValue("take");
        Explanation newExplanationEntity = new Explanation(l);
        newExplanationEntity.setLanguage(Locale.SIMPLIFIED_CHINESE);
        newExplanationEntity.setPartOfSpeech(PartOfSpeech.VT);
        newExplanationEntity.setDefinition("行动");
        //ACT
        UserTransaction tx = Util.userTransaction();
        List<Explanation> returnedExplanations;
        beginTransaction(tx);
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            ExplanationRepository out = new ExplanationRepository(entityManager);
            returnedExplanations = out.cascadeAdd(List.of(newExplanationEntity));
        }
        commitTransaction(tx);
        //VERIFY + ASSERT
        jsonb.toJson(returnedExplanations.get(0));
        assertEqualExplanations(ITUtil.getAllExplanations(preparedStatementExecutor, "take"), returnedExplanations);
    }
    @Test
    public void testCreate_addNewLexicalItemWith2Explanations() {
        //PREPARE
        LexicalItem l = new LexicalItem();
        l.setLanguage(Locale.ENGLISH);
        l.setValue("take");
        Explanation explanationEntity = new Explanation(l);
        explanationEntity.setLanguage(Locale.SIMPLIFIED_CHINESE);
        explanationEntity.setPartOfSpeech(PartOfSpeech.VT);
        explanationEntity.setDefinition("test explanation 1");
        Explanation explanationEntity2 = new Explanation(l);
        explanationEntity2.setLanguage(Locale.SIMPLIFIED_CHINESE);
        explanationEntity2.setPartOfSpeech(PartOfSpeech.N);
        explanationEntity2.setDefinition("test explanation 2");
        List<Explanation> explanationsToAdd = List.of(explanationEntity, explanationEntity2);
        //ACT
        UserTransaction tx = Util.userTransaction();
        List<Explanation> returnedExplanations;
        beginTransaction(tx);
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            ExplanationRepository out = new ExplanationRepository(entityManager);
            returnedExplanations = out.cascadeAdd(explanationsToAdd);
        }
        commitTransaction(tx);
        assertEqualExplanations(ITUtil.getAllExplanations(preparedStatementExecutor, "take"), returnedExplanations);
    }
    private static void assertEqualExplanations(List<Explanation> expected, List<Explanation> actual) {
        Iterator<Explanation> returnedExplanationsItr = expected.iterator();
        Iterator<Explanation> persistedExplanationsItr = actual.iterator();
        while (returnedExplanationsItr.hasNext() && persistedExplanationsItr.hasNext()) {
            Explanation persisted = persistedExplanationsItr.next();
            Explanation returned = returnedExplanationsItr.next();
            assertEquals(persisted, returned);
        }
    }

}

package io.github.rxue.dictionary.jpa.repository;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import io.github.rxue.dictionary.jpa.entity.LexicalItem;
import io.github.rxue.dictionary.jpa.entity.Explanation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExplanationEntityRepositoryUpdateIT extends AbstractITConfiguration {

    @BeforeEach
    public void insert() {
        ExplanationEntityRepositoryReadIT.insert();
    }

    @AfterEach
    public void truncateTables() {
        ITUtil.truncateTables(preparedStatementExecutor);
    }

    @Test
    public void cascadeUpdate_base() {
        //PREPARE
        final List<Explanation> explanationEntities = ITUtil.getAllExplanations(preparedStatementExecutor, "test");
        Explanation explanationEntityToUpdate = explanationEntities.stream().findAny().get();
        //ACT
        userTransactionExecutor.execute(entityManager -> {
            LexicalItem lexicalItem = explanationEntityToUpdate.getLexicalItem();
            lexicalItem.setValue("test after update");
            explanationEntityToUpdate.setDefinition("updated explanation");
            ExplanationRepository out = new ExplanationRepository(entityManager);
            out.cascadeUpdate(explanationEntities.stream().toList());
        });
        //ASSERT
        final List<Explanation> updatedExplanationEntities = ITUtil.getAllExplanations(preparedStatementExecutor, "test after update");
        assertEquals(3, updatedExplanationEntities.size());
        assertTrue(updatedExplanationEntities.stream().anyMatch(e -> "updated explanation".equals(e.getDefinition())));
    }
}

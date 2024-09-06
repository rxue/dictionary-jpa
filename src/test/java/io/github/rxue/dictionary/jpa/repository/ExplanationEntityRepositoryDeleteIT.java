package io.github.rxue.dictionary.jpa.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import io.github.rxue.dictionary.jpa.entity.Explanation;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExplanationEntityRepositoryDeleteIT extends AbstractITConfiguration {

    @BeforeAll
    public static void insert() {
        ExplanationEntityRepositoryReadIT.insert();
    }
    @Test
    public void deleteExplanationById() {
        final List<Explanation> explanationEntities = ITUtil.getAllExplanations(preparedStatementExecutor, "test");
        final int originalExplanationSize = explanationEntities.size();
        Explanation explanationEntityToDelete = explanationEntities.stream().findAny().get();
        //ACT
        userTransactionExecutor.execute(entityManager -> {
            ExplanationRepository out = new ExplanationRepository(entityManager);
            out.deleteById(explanationEntityToDelete.getId());
        });
        //ASSERT
        List<Explanation> explanationsAfterDelete = ITUtil.getAllExplanations(preparedStatementExecutor, "test");
        assertEquals(originalExplanationSize-1, explanationsAfterDelete.size());
    }
}

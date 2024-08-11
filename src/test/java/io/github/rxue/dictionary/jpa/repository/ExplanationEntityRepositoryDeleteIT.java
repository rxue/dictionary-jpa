package io.github.rxue.dictionary.jpa.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import io.github.rxue.dictionary.jpa.entity.ExplanationEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExplanationEntityRepositoryDeleteIT extends AbstractDatabaseConfiguration {

    @BeforeAll
    public static void insert() {
        ExplanationEntityRepositoryReadIT.insert();
    }
    @Test
    public void deleteExplanationById() {
        final List<ExplanationEntity> explanationEntities = ITUtil.getAllExplanations(preparedStatementExecutor, "test");
        final int originalExplanationSize = explanationEntities.size();
        ExplanationEntity explanationEntityToDelete = explanationEntities.stream().findAny().get();
        //ACT
        userTransactionExecutor.execute(entityManager -> {
            ExplanationRepository out = new ExplanationRepository(entityManager);
            out.deleteById(explanationEntityToDelete.getId());
        });
        //ASSERT
        List<ExplanationEntity> explanationsAfterDelete = ITUtil.getAllExplanations(preparedStatementExecutor, "test");
        assertEquals(originalExplanationSize-1, explanationsAfterDelete.size());
    }
}

package io.github.rxue.dictionary.jpa.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import io.github.rxue.dictionary.jpa.entity.Explanation;
import io.github.rxue.dictionary.jpa.entity.LexicalItem;
import io.github.rxue.dictionary.jpa.entity.PartOfSpeech;

import java.sql.ResultSet;
import java.util.*;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static io.github.rxue.dictionary.jpa.repository.ITUtil.*;

public class ExplanationEntityRepositoryCreateIT extends AbstractITConfiguration {
    @AfterEach
    public void truncateTables() {
        ITUtil.truncateTables(preparedStatementExecutor);
    }

    @Test
    public void testCreate_addNewLexicalItemWithOneExplanation() {
        //ACT
        userTransactionExecutor.execute(entityManager -> {
                    LexicalItem l = new LexicalItem();
                    l.setLanguage(Locale.ENGLISH);
                    l.setValue("take");
                    Explanation explanationEntity = new Explanation(null, l);
                    explanationEntity.setLanguage(Locale.SIMPLIFIED_CHINESE);
                    explanationEntity.setPartOfSpeech(PartOfSpeech.VT);
                    explanationEntity.setDefinition("行动");
            ExplanationRepository out = new ExplanationRepository(entityManager);
            out.cascadeAdd(List.of(explanationEntity));
        });
        //ASSERT
        final Explanation explanationEntity = ITUtil.getAllExplanations(preparedStatementExecutor, "take")
                .stream().findAny().get();
        LexicalItemVO expectedLexicalItem = new LexicalItemVO(Locale.ENGLISH, "take");
        assertEquals(new ExplanationVO(expectedLexicalItem, Locale.SIMPLIFIED_CHINESE, PartOfSpeech.VT, "行动"), toExplanationVO(explanationEntity));
    }
    @Test
    public void testCreate_addNewLexicalItemWith2Explanations() {
        //PREPARE
        Supplier<Collection<Explanation>> prepareExplanations = () -> {
            LexicalItem l = new LexicalItem();
            l.setLanguage(Locale.ENGLISH);
            l.setValue("take");
            Explanation explanationEntity = new Explanation(null, l);
            explanationEntity.setLanguage(Locale.SIMPLIFIED_CHINESE);
            explanationEntity.setPartOfSpeech(PartOfSpeech.VT);
            explanationEntity.setDefinition("test explanation 1");
            Explanation explanationEntity2 = new Explanation(null, l);
            explanationEntity2.setLanguage(Locale.SIMPLIFIED_CHINESE);
            explanationEntity2.setPartOfSpeech(PartOfSpeech.N);
            explanationEntity2.setDefinition("test explanation 2");
            return List.of(explanationEntity, explanationEntity2);
        };
        //ACT
        userTransactionExecutor.execute(entityManager -> {
            ExplanationRepository out = new ExplanationRepository(entityManager);
            out.cascadeAdd(prepareExplanations.get());
        });

        //ASSERT
        final List<LexicalItem> existingItems = preparedStatementExecutor.executeAndReturn("select * from lexical_item", preparedStatement -> {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<LexicalItem> createdItems = new ArrayList<>();
                while (resultSet.next()) {
                    LexicalItem lexicalItem = new LexicalItem(resultSet.getLong("id"));
                    lexicalItem.setLanguage(Locale.forLanguageTag(resultSet.getString("language")));
                    lexicalItem.setValue(resultSet.getString("value"));
                    createdItems.add(lexicalItem);
                }
                return createdItems;
            }
        });
        List<Explanation> addedExplanationEntities = ITUtil.getAllExplanations(preparedStatementExecutor, "take");
        LexicalItemVO expectedLexicalItem = new LexicalItemVO(Locale.ENGLISH, "take");
        ExplanationVO expectedExplanation = new ExplanationVO(expectedLexicalItem, Locale.SIMPLIFIED_CHINESE, PartOfSpeech.VT, "test explanation 1");

        ExplanationVO expectedExplanation2 = new ExplanationVO(expectedLexicalItem, Locale.SIMPLIFIED_CHINESE, PartOfSpeech.N, "test explanation 2");
        List<ExplanationVO> expectedExplanations = List.of(expectedExplanation, expectedExplanation2);
        int i = 0;
        for (ExplanationVO explanationEntity : expectedExplanations) {
                assertEquals(explanationEntity, toExplanationVO(addedExplanationEntities.get(i++)));
        }

    }

}

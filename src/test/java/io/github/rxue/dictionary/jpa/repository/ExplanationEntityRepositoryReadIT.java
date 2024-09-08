package io.github.rxue.dictionary.jpa.repository;

import jakarta.json.bind.JsonbException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import io.github.rxue.dictionary.jpa.entity.Explanation;
import io.github.rxue.dictionary.jpa.entity.PartOfSpeech;
import io.github.rxue.dictionary.Keyword;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static io.github.rxue.dictionary.jpa.repository.ITUtil.*;

public class ExplanationEntityRepositoryReadIT extends AbstractITConfiguration {
    @BeforeAll
    public static void insert() {
        final Long generatedId = generateLexicalItem(preparedStatementExecutor, Locale.ENGLISH, "test");
        preparedStatementExecutor.execute("insert into explanation (id, lexical_item_id, language, partOfSpeech, definition) value (NEXT VALUE FOR explanation_id_seq,?,?,?,?)", statement -> {
            statement.setLong(1, generatedId); // Set value for column1
            statement.setString(2, Locale.SIMPLIFIED_CHINESE.toString());
            statement.setString(3, "N");
            statement.setString(4, "测试 1");
            statement.addBatch();
            statement.setLong(1, generatedId); // Set value for column1
            statement.setString(2, Locale.SIMPLIFIED_CHINESE.toString());
            statement.setString(3, "N");
            statement.setString(4, "测试 2");
            statement.addBatch();
            statement.setLong(1, generatedId); // Set value for column1
            statement.setString(2, Locale.ENGLISH.toString());
            statement.setString(3, "N");
            statement.setString(4, "test in English");
            statement.addBatch();
            statement.executeBatch();
        });
        final Long generatedId2 = generateLexicalItem(preparedStatementExecutor, Locale.ENGLISH, "other");
        preparedStatementExecutor.execute("insert into explanation (id, lexical_item_id, language, partOfSpeech, definition) value (NEXT VALUE FOR explanation_id_seq,?,?,?,?)", statement -> {
            statement.setLong(1, generatedId2); // Set value for column1
            statement.setString(2, Locale.SIMPLIFIED_CHINESE.toString());
            statement.setString(3, "N");
            statement.setString(4, "其他");
            statement.execute();
        });


    }
    @Test
    public void lazyFindLike_base() {
        //ACT
        userTransactionExecutor.execute(entityManager -> {
            ExplanationRepository out = new ExplanationRepository(entityManager);
            Keyword keyword = new Keyword() {

                @Override
                public Locale getLanguage() {
                    return Locale.ENGLISH;
                }

                @Override
                public String getValue() {
                    return "test";
                }
            };
            List<Explanation> result = out.lazyFindLike(keyword, Locale.SIMPLIFIED_CHINESE);
            assertEquals(2, result.size());
            final Explanation firstExplanationEntity = result.get(0);
            LexicalItemVO expectedLexicalItem = new LexicalItemVO(Locale.ENGLISH, "test");
            assertEquals(new ExplanationVO(expectedLexicalItem,
                    Locale.SIMPLIFIED_CHINESE, PartOfSpeech.N, "测试 1"), toExplanationVO(firstExplanationEntity));
            assertThrows(JsonbException.class, () -> jsonb.toJson(result.get(0)));
        });
    }

    @Test
    public void findLike_base() {
        //ACT
        userTransactionExecutor.execute(entityManager -> {
            ExplanationRepository out = new ExplanationRepository(entityManager);
            Keyword keyword = new Keyword() {

                @Override
                public Locale getLanguage() {
                    return Locale.ENGLISH;
                }

                @Override
                public String getValue() {
                    return "test";
                }
            };
            List<Explanation> result = out.findLike(keyword, Locale.SIMPLIFIED_CHINESE);
            assertEquals(2, result.size());
            final Explanation firstExplanationEntity = result.get(0);
            LexicalItemVO expectedLexicalItem = new LexicalItemVO(Locale.ENGLISH, "test");
            assertEquals(new ExplanationVO(expectedLexicalItem,
                    Locale.SIMPLIFIED_CHINESE, PartOfSpeech.N, "测试 1"), toExplanationVO(firstExplanationEntity));
            assertNotNull(jsonb.toJson(result.get(0)));
        });
    }

}

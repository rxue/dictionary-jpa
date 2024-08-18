package io.github.rxue.dictionary.jpa.repository;

import io.github.rxue.dictionary.jpa.entity.Explanation;
import io.github.rxue.dictionary.jpa.entity.LexicalItem;
import io.github.rxue.dictionary.jpa.entity.PartOfSpeech;
import io.github.rxue.transaction.jdbc.PreparedStatementExecutor;

import java.sql.ResultSet;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ITUtil {
    private ITUtil() {
    }
    static Long generateLexicalItem(PreparedStatementExecutor preparedStatementExecutor, Locale language, String value) {
        return preparedStatementExecutor.executeAndReturn("insert into lexical_item (language,value) value (?,?)", statement -> {
            statement.setString(1, language.toString()); // Set value for column1
            statement.setString(2, value); // Set value for column2
            statement.executeUpdate(); //Execute the insert statement
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1); // the ID is a long (modify based on data type)
                } else {
                    throw new IllegalStateException("ID generation failure");
                }
            }
        });
    }

    static String languageLocaleStringToTag(String localeString) {
        return localeString.replace("_", "-");
    }

    static List<Explanation> getAllExplanations(PreparedStatementExecutor preparedStatementExecutor, String lexicalItemValue) {
        final LexicalItem existingItem = preparedStatementExecutor.executeAndReturn("select * from lexical_item where value = ?", preparedStatement -> {
            preparedStatement.setString(1, lexicalItemValue);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    LexicalItem lexicalItem = new LexicalItem(resultSet.getLong("id"));
                    String languageTag = languageLocaleStringToTag(resultSet.getString("language"));
                    lexicalItem.setLanguage(Locale.forLanguageTag(languageTag));
                    lexicalItem.setValue(resultSet.getString("value"));
                    return lexicalItem;
                }
                throw new IllegalArgumentException(preparedStatement + " does not return any result");
            }
        });
        return preparedStatementExecutor.executeAndReturn("select * from explanation where lexical_item_id = ?", preparedStatement -> {
            preparedStatement.setLong(1, existingItem.getId());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<Explanation> result = new ArrayList<>();
                while (resultSet.next()) {
                    Explanation explanationEntity = new Explanation(resultSet.getLong("id"), existingItem);
                    String languageTag = languageLocaleStringToTag(resultSet.getString("language"));
                    explanationEntity.setLanguage(Locale.forLanguageTag(languageTag));
                    explanationEntity.setPartOfSpeech(PartOfSpeech.valueOf(resultSet.getString("partOfSpeech")));
                    explanationEntity.setDefinition(resultSet.getString("definition"));
                    result.add(explanationEntity);
                }
                return result.stream().sorted(Comparator.comparing(Explanation::getDefinition))
                        .toList();
            }
        });
    }
    public static void truncateTables(PreparedStatementExecutor preparedStatementExecutor) {
        preparedStatementExecutor.execute("delete from explanation", statement -> {
            statement.executeUpdate();
        });
        preparedStatementExecutor.execute("delete from lexical_item", statement -> {
            statement.executeUpdate();
        });
        //ensure truncate success
        preparedStatementExecutor.execute("select * from lexical_item", preparedStatement -> {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                assertFalse(resultSet.next());
            }
        });
        preparedStatementExecutor.execute("select * from explanation", preparedStatement -> {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                assertFalse(resultSet.next());
            }
        });
    }
    static ExplanationVO toExplanationVO(Explanation explanationEntity) {
        LexicalItem lexicalItem = explanationEntity.getLexicalItem();
        LexicalItemVO lexicalItemVO = new LexicalItemVO(lexicalItem.getLanguage(), lexicalItem.getValue());
        return new ExplanationVO(lexicalItemVO, explanationEntity.getLanguage(), explanationEntity.getPartOfSpeech(), explanationEntity.getDefinition());
    }
}
package io.github.rxue.dictionary.jpa.repository;

//import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.utility.DockerImageName;
import io.github.rxue.Util;
import io.github.rxue.transaction.jdbc.PreparedStatementExecutor;
import io.github.rxue.transaction.UserTransactionExecutor;

import java.util.Collections;
import java.util.function.Consumer;

public abstract class AbstractITConfiguration {
    private static MariaDBContainer<?> db;
    protected static EntityManagerFactory entityManagerFactory;
    protected static PreparedStatementExecutor preparedStatementExecutor;
    protected static UserTransactionExecutor userTransactionExecutor;
    protected static Jsonb jsonb;

    @BeforeAll
    protected static void init() {
        db = new MariaDBContainer<>(DockerImageName.parse("mariadb:10.5.8"));
        System.setProperty("port", "3307");
        db.setPortBindings(Collections.singletonList(Util.getPortNumber() + ":3306"));
        db.start();
        entityManagerFactory = Persistence.createEntityManagerFactory("dictionary-mariadb-test");
        preparedStatementExecutor = new PreparedStatementExecutor(entityManagerFactory);
        userTransactionExecutor = new UserTransactionExecutor(entityManagerFactory);
        jsonb = JsonbBuilder.create();
    }


    public void execute(Consumer<EntityManager> operations) {
        userTransactionExecutor.execute(operations);
    }

    @AfterAll
    protected static void destroy() {
        if (entityManagerFactory != null) entityManagerFactory.close();
        db.stop();
        closeJsonb(jsonb);
    }

    private static void closeJsonb(Jsonb jsonb) {
        if (jsonb != null) {
            try {
                jsonb.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}

package io.github.rxue.transaction;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.transaction.*;
import io.github.rxue.ConfigurationUtil;

import java.util.function.Consumer;

public class UserTransactionExecutor {
    private final EntityManagerFactory entityManagerFactory;
    public UserTransactionExecutor(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public void execute(Consumer<EntityManager> operations) {
        UserTransaction tx = ConfigurationUtil.userTransaction();
        try {
            tx.begin();
        } catch (NotSupportedException | SystemException e) {
            throw new RuntimeException("Transaction fails to begin", e);
        }
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            operations.accept(entityManager);
        }
        try {
            tx.commit();
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SystemException e) {
            throw new RuntimeException("Transaction fails to commit", e);
        }
    }
}
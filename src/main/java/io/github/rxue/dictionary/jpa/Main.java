package io.github.rxue.dictionary.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Generate database schema
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Going to generate database schema!");
        try(EntityManagerFactory emf = Persistence.createEntityManagerFactory("dictionary-mariadb");
            EntityManager em = emf.createEntityManager()) {
        }
    }
}

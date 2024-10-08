dictionary-jpa
======================

![Apache License, Version 2.0, January 2004](https://img.shields.io/github/license/apache/maven.svg?label=License)
[![Maven Central](https://img.shields.io/badge/Maven%20Central-1.6-blue)](https://central.sonatype.com/artifact/io.github.rxue/dictionary-jpa)

*dictionary-jpa* generates 2 types of jars with different usage
* *uber-JAR* whose name ends with `-uber` after the version number: used to generate dictionary  database schema
* normal jar: used as a dependency for CRUD operations on a database with schema generated by the *uber-JAR* above

# How to Use
## How to use the *uber-JAR*
Pre-requisite: Generation of the database schema needs a database. The database should be configured either on base of https://github.com/rxue/dictionary-jpa/blob/main/src/main/resources/META-INF/persistence.xml or user should overwrite the properties in the `peristence.xml` through command line below

1. Download the *uber-JAR* from https://repo1.maven.org/maven2/io/github/rxue/dictionary-jpa/1.4/dictionary-jpa-1.4-uber.jar
2. run command in the dictionary of the *uber-JAR*: `java -Djakarta.persistence.schema-generation.database.action=create -jar dictionary-jpa-1.4-uber.jar` with a database configured on base of the default properties in [persistence.xml](https://github.com/rxue/dictionary-jpa/blob/main/src/main/resources/META-INF/persistence.xml) embedded inside the *uber-JAR*. NOTE that the value of `jakarta.persistence.schema-generation.action` has to be set to `create` since the default value `none` would not generate database schema

Example command of running the `dictionary-jpa-1.4-uber.jar` with properties overwriting the properties in the default [persistence.xml](https://github.com/rxue/dictionary-jpa/blob/main/src/main/resources/META-INF/persistence.xml) embedded inside the *uber-JAR*:
* `java -Djakarta.persistence.schema-generation.database.action=create -Djakarta.persistence.jdbc.password=1234 -jar dictionary-jpa-1.4-uber.jar` : password of the `root` user of the target database is `1234`

## How to use the normal jar in a Maven project with a database containing the schema generated by the *uber-JAR* above
add the following block to `pom.xml`

```
<dependency>
    <groupId>io.github.rxue</groupId>
    <artifactId>dictionary-jpa</artifactId>
    <version>1.6</version>
</dependency>
```

## Repository Design with JPA
## Interface design: `LexicalItemRepository` and `ExplanationRepository`

Think first from the frontend point of view:

**CREATE**
 * ~add a full *lexical entry* including *lexical item*, *defintion*, *part of speech*, *example sentences* etc.~
 * ~add a single *explanationEntity* to an existing *lexical entry*~

**READ** 

 * Given an keyword with value, language and definition language, get all the possible results along with all meanings
 * ~Given a *lexical item id* get the all the explanationEntities~ (no real case realized yet)

**UPDATE**

 * ~Given a *lexical item*, update it (explanationEntities can be added or removed)~
 * ~Given an *explanationEntity*, update it~

**DELETE**

 * Given a *lexical item* delete it (not implemented yet)
 * ~Given an *explanationEntity*, delete it~

**Unidirectional `@ManyToOne` Association (`Explanation` > `LexicalItem`)**

**CREATE**
 * add a full *lexical entry* including *lexical item*, *defintion*, *part of speech*, *example sentences* etc. : (can be done purely through `ExplanationRepository`)

**READ** 

 * Given an input, get all the possible results along with all meanings: OK (can be done purely through `ExplanationRepository`)
 * ~Given a *lexical item id* get the all the explanationEntities : OK but the query on `ExplanationRepository` is not trivial~

**UPDATE**

 * ~Given a *lexical item*, update it (explanationEntities can be added or removed) : need separate merge on both `LexicalItem` and `Explanation`~

**DELETE**

 * ~Given a *lexical item* delete it : need first `LexicalItemRepository.find`/`EntityManager.find` to search for the managed entity and thEN call `EntityManager.remove`~

~This design strategy cannot simply meet the repositories.~ 

## Current Design Conclusion on base of *prototyping* trial

After trying the bidirectional `@ManyToOne` association from `Explanation` back to `DictionaryEntry`, and then gradually tweaking back to unidirectional, unidirectional `@ManyToOne` association is eventually decided to be used


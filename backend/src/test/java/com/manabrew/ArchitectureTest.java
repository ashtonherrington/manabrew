package com.manabrew;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noMethods;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.library.GeneralCodingRules;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.transaction.annotation.Transactional;

/**
 * Architecture rules from backend/CONVENTIONS.md, enforced on every build.
 *
 * <p>Imports only production classes under com.manabrew (no dependency jars), once per run. The
 * timeout is a performance budget: if scanning ever exceeds it, move these tests to a separate
 * Gradle task instead of raising the limit.
 */
@Timeout(value = 5, unit = TimeUnit.SECONDS)
class ArchitectureTest {

  private static final String[] FEATURE_PACKAGES = {
    "com.manabrew.collection..",
    "com.manabrew.card..",
    "com.manabrew.deck..",
    "com.manabrew.simulation.."
  };

  private static JavaClasses classes;

  @BeforeAll
  static void importClasses() {
    classes =
        new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_JARS)
            .importPackages("com.manabrew");
  }

  // ---- Layers ----

  @Test
  void controllersDoNotAccessRepositories() {
    noClasses()
        .that()
        .haveSimpleNameEndingWith("Controller")
        .should()
        .dependOnClassesThat()
        .haveSimpleNameEndingWith("Repository")
        .because("controllers call services; services own data access")
        .check(classes);
  }

  @Test
  void repositoriesDoNotAccessServicesOrControllers() {
    noClasses()
        .that()
        .haveSimpleNameEndingWith("Repository")
        .should()
        .dependOnClassesThat()
        .haveSimpleNameEndingWith("Service")
        .orShould()
        .dependOnClassesThat()
        .haveSimpleNameEndingWith("Controller")
        .because("dependencies point one way: controller -> service -> repository")
        .check(classes);
  }

  // ---- Transactions ----

  @Test
  void transactionalOnlyOnServiceClasses() {
    noClasses()
        .that()
        .haveSimpleNameNotEndingWith("Service")
        .should()
        .beAnnotatedWith(Transactional.class)
        .because("transaction boundaries belong in the service layer")
        .check(classes);
  }

  @Test
  void transactionalMethodsOnlyInServices() {
    noMethods()
        .that()
        .areDeclaredInClassesThat()
        .haveSimpleNameNotEndingWith("Service")
        .should()
        .beAnnotatedWith(Transactional.class)
        .because("transaction boundaries belong in the service layer")
        .check(classes);
  }

  @Test
  void transactionalMethodsAreNotPrivate() {
    noMethods()
        .that()
        .areAnnotatedWith(Transactional.class)
        .should()
        .bePrivate()
        .because("Spring's proxy ignores @Transactional on private methods")
        .check(classes);
  }

  // ---- Feature boundaries (split-readiness) ----

  @Test
  void featurePackagesAreFreeOfCycles() {
    slices().matching("com.manabrew.(*)..").should().beFreeOfCycles().check(classes);
  }

  @Test
  void commonDoesNotDependOnFeatures() {
    noClasses()
        .that()
        .resideInAPackage("com.manabrew.common..")
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage(FEATURE_PACKAGES)
        .because("common/ will become a shared library and must not know about features")
        .check(classes);
  }

  @Test
  void simulationIsPureComputation() {
    noClasses()
        .that()
        .resideInAPackage("com.manabrew.simulation..")
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage(
            "org.springframework..", "org.jooq..", "java.sql..", "jakarta.servlet..")
        .because("simulation/ must be extractable as a plain library")
        .check(classes);
  }

  // ---- General coding rules ----

  @Test
  void noFieldInjection() {
    GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION.check(classes);
  }

  @Test
  void noStandardStreams() {
    GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS.check(classes);
  }

  @Test
  void noGenericExceptions() {
    GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS.check(classes);
  }

  @Test
  void noJavaUtilLogging() {
    GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING.check(classes);
  }
}

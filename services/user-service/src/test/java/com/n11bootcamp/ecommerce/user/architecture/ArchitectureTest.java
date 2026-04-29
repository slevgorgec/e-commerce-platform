package com.n11bootcamp.ecommerce.user.architecture;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArchitectureTest {

    private static final String BASE = "com.n11bootcamp.ecommerce.user";

    private final com.tngtech.archunit.core.domain.JavaClasses classes =
            new ClassFileImporter().importPackages(BASE);

    @Test
    void domain_shouldNotDependOnApplicationOrInfrastructureOrInterfaces() {
        ArchRule rule = noClasses()
                .that().resideInAPackage(BASE + ".domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage(
                        BASE + ".application..",
                        BASE + ".infrastructure..",
                        BASE + ".interfaces.."
                );
        rule.check(classes);
    }

    @Test
    void infrastructure_shouldNotBeAccessedFromDomain() {
        ArchRule rule = noClasses()
                .that().resideInAPackage(BASE + ".domain..")
                .should().dependOnClassesThat()
                .resideInAPackage(BASE + ".infrastructure..");
        rule.check(classes);
    }

    @Test
    void controllers_shouldNotAccessRepositoriesDirectly() {
        ArchRule rule = noClasses()
                .that().resideInAPackage(BASE + ".interfaces..")
                .should().dependOnClassesThat()
                .resideInAPackage(BASE + ".infrastructure.persistence..");
        rule.check(classes);
    }
}

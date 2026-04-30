package com.n11bootcamp.ecommerce.payment.architecture;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArchitectureTest {

    private static final String BASE = "com.n11bootcamp.ecommerce.payment";

    @Test
    void domainLayer_shouldNotDependOnSpring() {
        ArchRule rule = noClasses()
                .that().resideInAPackage(BASE + ".domain..")
                .should().dependOnClassesThat()
                .resideInAPackage("org.springframework..");

        rule.check(new ClassFileImporter().importPackages(BASE));
    }

    @Test
    void domainLayer_shouldNotDependOnInfrastructure() {
        ArchRule rule = noClasses()
                .that().resideInAPackage(BASE + ".domain..")
                .should().dependOnClassesThat()
                .resideInAPackage(BASE + ".infrastructure..");

        rule.check(new ClassFileImporter().importPackages(BASE));
    }

    @Test
    void applicationLayer_shouldNotDependOnInfrastructure() {
        ArchRule rule = noClasses()
                .that().resideInAPackage(BASE + ".application..")
                .should().dependOnClassesThat()
                .resideInAPackage(BASE + ".infrastructure..");

        rule.check(new ClassFileImporter().importPackages(BASE));
    }

    @Test
    void controllersLayer_shouldNotDependOnInfrastructure() {
        ArchRule rule = noClasses()
                .that().resideInAPackage(BASE + ".interfaces..")
                .should().dependOnClassesThat()
                .resideInAPackage(BASE + ".infrastructure..");

        rule.check(new ClassFileImporter().importPackages(BASE));
    }
}
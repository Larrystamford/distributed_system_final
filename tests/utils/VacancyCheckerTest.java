package utils;

import database.database;

class VacancyCheckerTest {

    private static database database;


    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        database = new database();
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

//    @org.junit.jupiter.api.Test
//    void isVacant() {
//        VacancyChecker.isVacant();
//    }
}
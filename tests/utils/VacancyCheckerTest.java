package utils;

import database.Database;

class VacancyCheckerTest {

    private static Database database;


    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        database = new Database();
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

//    @org.junit.jupiter.api.Test
//    void isVacant() {
//        VacancyChecker.isVacant();
//    }
}
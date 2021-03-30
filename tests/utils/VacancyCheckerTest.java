package utils;

import org.junit.jupiter.api.BeforeAll;
import server.ServerDB;

class VacancyCheckerTest {

    private static ServerDB serverDB;


    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        serverDB = new ServerDB();
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

//    @org.junit.jupiter.api.Test
//    void isVacant() {
//        VacancyChecker.isVacant();
//    }
}
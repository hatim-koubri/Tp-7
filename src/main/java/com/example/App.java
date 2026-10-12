package com.example;

import com.example.service.DataInitService;
import com.example.service.PerformanceTestService;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class App {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("mediathequePU");
        try {
            DataInitService initialiseur = new DataInitService(emf);
            PerformanceTestService tests = new PerformanceTestService(emf);

            initialiseur.initData();
            tests.runAllTests();
        } finally {
            emf.close();
        }
    }
}

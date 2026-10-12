package com.example.service;

import com.example.model.Auteur;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.List;

public class PerformanceTestService {
    private final EntityManagerFactory emf;

    public PerformanceTestService(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void runAllTests() {
        SessionFactory sessionFactory = emf.unwrap(SessionFactory.class);
        Statistics statistics = sessionFactory.getStatistics();

        mesurer("Chargement LAZY classique (N+1)", statistics, () -> charger(false, false));
        mesurer("Chargement avec JOIN FETCH", statistics, () -> charger(true, false));
        mesurer("Chargement avec Entity Graph", statistics, () -> charger(false, true));
        montrerCacheSecondNiveau(sessionFactory, statistics);
    }

    private void mesurer(String titre, Statistics statistics, Chargement chargement) {
        emf.unwrap(SessionFactory.class).getCache().evictAllRegions();
        statistics.clear();
        long debut = System.nanoTime();
        int nombreLivres = chargement.executer();
        long dureeMs = (System.nanoTime() - debut) / 1_000_000;

        System.out.println();
        System.out.println("=== " + titre + " ===");
        System.out.println("Auteurs chargés : 10 | livres parcourus : " + nombreLivres);
        System.out.println("Requêtes SQL préparées : " + statistics.getPrepareStatementCount()
                + " | durée : " + dureeMs + " ms");
    }

    private int charger(boolean joinFetch, boolean entityGraph) {
        EntityManager em = emf.createEntityManager();
        try {
            List<Auteur> auteurs;
            if (joinFetch) {
                auteurs = em.createQuery(
                        "SELECT DISTINCT a FROM Auteur a LEFT JOIN FETCH a.livres ORDER BY a.id",
                        Auteur.class).getResultList();
            } else {
                TypedQuery<Auteur> query = em.createQuery(
                        "SELECT a FROM Auteur a ORDER BY a.id", Auteur.class);
                if (entityGraph) {
                    EntityGraph<?> graph = em.getEntityGraph("Auteur.avecLivres");
                    query.setHint("javax.persistence.fetchgraph", graph);
                }
                auteurs = query.getResultList();
            }

            int totalLivres = 0;
            for (Auteur auteur : auteurs) {
                totalLivres += auteur.getLivres().size();
            }
            return totalLivres;
        } finally {
            em.close();
        }
    }

    private void montrerCacheSecondNiveau(SessionFactory sessionFactory, Statistics statistics) {
        sessionFactory.getCache().evictAllRegions();
        statistics.clear();

        EntityManager emInitial = emf.createEntityManager();
        Long auteurId;
        try {
            Auteur auteur = emInitial.createQuery(
                    "SELECT a FROM Auteur a WHERE a.nom = :nom", Auteur.class)
                    .setParameter("nom", "Nadia El Mansouri")
                    .getSingleResult();
            auteurId = auteur.getId();
        } finally {
            emInitial.close();
        }

        long sqlPremierAcces = statistics.getPrepareStatementCount();
        statistics.clear();

        EntityManager emDepuisCache = emf.createEntityManager();
        try {
            Auteur auteurRelu = emDepuisCache.find(Auteur.class, auteurId);
            System.out.println();
            System.out.println("=== Cache de second niveau ===");
            System.out.println("Auteur relu : " + auteurRelu.getNom());
        } finally {
            emDepuisCache.close();
        }

        System.out.println("Requêtes SQL au premier accès : " + sqlPremierAcces);
        System.out.println("Requêtes SQL au second accès : " + statistics.getPrepareStatementCount());
        System.out.println("Hits du cache d'entités : " + statistics.getSecondLevelCacheHitCount());
    }

    private interface Chargement {
        int executer();
    }
}

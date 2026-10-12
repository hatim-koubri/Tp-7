package com.example.service;

import com.example.model.Auteur;
import com.example.model.Livre;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class DataInitService {
    private final EntityManagerFactory emf;

    public DataInitService(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void initData() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            if (em.createQuery("SELECT COUNT(a) FROM Auteur a", Long.class).getSingleResult() == 0) {
                String[] auteurs = {
                        "Nadia El Mansouri", "Youssef Ait Lahcen", "Salma Berrada",
                        "Omar Chraibi", "Lina Tazi", "Amine Fikri",
                        "Meryem Kadiri", "Rayan Bennani", "Sanae Idrissi", "Mehdi Alaoui"
                };
                String[][] titres = {
                        {"Les ruelles de Fès", "Carnet des saisons", "Le jardin bleu", "Une ville en partage"},
                        {"Atlas en hiver", "La route des arganiers", "L'ombre du phare", "Voyage au sud"},
                        {"Le fil des jours", "La fenêtre ouverte", "Rêves de papier", "La dernière page"},
                        {"Chroniques du port", "Le murmure des vagues", "La carte ancienne", "Nuits de Rabat"},
                        {"Au bord du désert", "L'atelier des mots", "Le secret du patio", "L'heure dorée"},
                        {"Les passagers", "Une autre rive", "Le carnet rouge", "L'écho des collines"},
                        {"Poussière d'étoiles", "La maison d'argile", "Les heures lentes", "Le marché du matin"},
                        {"Le chemin des cèdres", "Les voix du souk", "Le dernier train", "Une saison nouvelle"},
                        {"La couleur des murs", "Petites aventures", "Le pont de pierre", "L'île tranquille"},
                        {"La bibliothèque des vents", "Les jours de pluie", "Le vieux cinéma", "L'encre et la mer"}
                };

                for (int i = 0; i < auteurs.length; i++) {
                    Auteur auteur = new Auteur(auteurs[i]);
                    for (String titre : titres[i]) {
                        auteur.ajouterLivre(new Livre(titre));
                    }
                    em.persist(auteur);
                }
            }
            em.getTransaction().commit();
            System.out.println("Données prêtes : 10 auteurs, chacun avec 4 livres.");
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}

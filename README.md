## Captures des résultats

### 1. Chargement LAZY et problème N+1

Le chargement classique récupère les 10 auteurs et leurs 40 livres en 11 requêtes SQL.

<img width="1422" height="977" alt="2" src="https://github.com/user-attachments/assets/56503904-a4a9-41a5-8a38-62b2571141b6" />

### 2. Optimisation avec JOIN FETCH

`JOIN FETCH` récupère les auteurs et leurs livres en une seule requête SQL.

<img width="1332" height="990" alt="3" src="https://github.com/user-attachments/assets/31a22669-1f7f-424c-b777-1d547d78c166" />

### 3. Entity Graph et cache de second niveau

L’Entity Graph charge les auteurs et leurs livres en une requête. Au second accès, l’auteur est retrouvé dans le cache : aucune requête SQL supplémentaire et un hit du cache.

<img width="1442" height="987" alt="4" src="https://github.com/user-attachments/assets/006f7baa-1430-45d4-8d39-3289b8738b28" />

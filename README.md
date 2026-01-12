# Dash

> Un jeu de plateforme arcade développé avec [LibGDX](https://libgdx.com/).

[![Status](https://img.shields.io/badge/Status-Playable-brightgreen)](https://github.com/raphaelbz/Dash) [![GitHub](https://img.shields.io/badge/GitHub-Repo-181717?logo=github)](https://github.com/raphaelbz/Dash)

## 📖 Description

**Dash** est un jeu de type "runner" où le joueur doit éviter des obstacles et survivre le plus longtemps possible. Le projet met en avant l'utilisation du framework LibGDX ainsi qu'une architecture modulaire facilitant l'extension du jeu (nouveaux niveaux, mécaniques, etc.).

### Fonctionnalités principales
*   **Moteur de jeu** : Basé sur LibGDX pour une compatibilité multiplateforme et de hautes performances.
*   **Gestion des niveaux** : Utilisation de [Tiled](https://www.mapeditor.org/) pour la création et le chargement dynamique des cartes (.tmx).
*   **Architecture** : Code structuré séparant la logique de jeu, le rendu et les entrées utilisateur.

---

## 🎮 Commandes

| Action | Touche / Contrôle |
| :--- | :--- |
| **Sauter** | `Espace` ou `Clic Gauche` |
| **Quitter** | `Alt + F4` |

---

## 🚀 Installation et Lancement

Ce projet utilise **Gradle** pour la gestion des dépendances et la compilation.

### Prérequis
*   JDK 17 ou version supérieure.

### Lancer le jeu

Ouvrez un terminal à la racine du projet et exécutez la commande suivante :

**Sous Windows :**
```powershell
.\gradlew.bat lwjgl3:run
```

**Sous macOS / Linux :**
```bash
./gradlew lwjgl3:run
```

---

## 🛠️ Structure du Projet

*   `core/` : Contient toute la logique du jeu (indépendant de la plateforme).
*   `lwjgl3/` : Launcher pour la version Desktop (Windows, Mac, Linux).
*   `assets/` : Ressources graphiques et sonores.

---

*Développé par [Votre Nom/Équipe].*

# Dash

Un hommage à **Geometry Dash** réalisé en Java avec [libGDX](https://libgdx.com/). Courez, sautez, évitez les obstacles et terminez chaque niveau le plus proprement possible grâce à des menus animés, un suivi de progression et plusieurs cartes réalisées sous Tiled.

## Fonctionnalités
- Menu principal et sélection de niveaux animés.
- 3 niveaux jouables (`mapRaf`, `mapMel`, `mapRoy`) chargés depuis des cartes **.tmx**.
- HUD avec pourcentage de progression, écrans de mort/victoire et redémarrage automatique du niveau.
- Contrôles clavier **et** souris, avec retour sonore sur le saut.
- Architecture libGDX classique : module `core` pour la logique, `lwjgl3` pour le lancement desktop.

## Contrôles
**Menus**
- Haut/Bas : naviguer dans le menu principal
- Gauche/Droite : choisir un niveau
- Entrée ou Espace : valider
- Échap : retour ou quitter

**En jeu**
- Espace **ou** clic gauche : sauter
- Échap : revenir au menu principal

## Démarrer rapidement
Prérequis : JDK 21+ et une connexion internet pour le premier téléchargement des dépendances.

```bash
# Lancer le jeu (desktop)
./gradlew lwjgl3:run

# Générer un JAR exécutable (lwjgl3/build/libs)
./gradlew lwjgl3:jar

# Lancer les tests éventuels
./gradlew test
```

## Structure du projet
- `core/` : logique de jeu (monde, entités, contrôleurs, rendu).
- `lwjgl3/` : lanceur desktop.
- `assets/` : cartes Tiled (`assets/maps/*.tmx`), sons et textures.

## Ajouter ou éditer des niveaux
1. Ouvrez les cartes `.tmx` existantes dans Tiled (dossier `assets/maps`).
2. Créez ou modifiez votre propre carte.
3. Référencez-la dans `LevelSelectScreen` pour qu’elle apparaisse dans le menu de sélection.

## Ressources
- [libGDX](https://libgdx.com/)
- Projet initial généré avec [gdx-liftoff](https://github.com/libgdx/gdx-liftoff)

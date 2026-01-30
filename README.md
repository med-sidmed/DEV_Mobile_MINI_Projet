# Shop Manager - Android Modernization Project 🚀

Bienvenue dans le projet **Shop Manager**, une application Android Java de gestion de boutique entièrement modernisée, sécurisée et optimisée.

## 📋 Résumé du Projet
Ce projet a consisté en une refonte complète d'une application existante pour répondre aux standards de qualité, de performance et de sécurité de 2026. L'objectif était de transformer un prototype legacy en une application robuste et prête pour la production.

---

## ✨ Améliorations Majeures

### 🎨 1. Modernisation UI/UX (Material Design 3)
- **Interface Premium** : Migration vers les composants Material 3 (`TextInputLayout`, `MaterialButton`, `MaterialCardView`).
- **Expérience Utilisateur** : Implémentation d'une navigation fluide via `BottomNavigationView`.
- **Détails Produits** : Ajout d'un header parallaxe avec `CollapsingToolbarLayout` et d'un bouton d'action flottant (FAB).
- **Consistance** : Standardisation de la palette de couleurs et du thème global.

### 🛡️ 2. Sécurité & Hardening
- **Protection des Données** : Implémentation du hachage sécurisé (BCrypt) pour les mots de passe utilisateurs.
- **Sécurité Réseau** : Configuration de `network_security_config.xml` pour forcer le HTTPS et bloquer le trafic clair (Man-in-the-Middle protection).
- **Protection du Code** : Activation de **R8/Proguard** pour l'obfuscation et la réduction de la taille de l'APK.
- **Manifeste Sécurisé** : Désactivation des sauvegardes (`allowBackup="false"`) et restriction de l'exposition des composants.

### 🚀 3. Optimisation des Performances
- **Réactivité UI** : Migration de toutes les opérations de base de données vers des threads asynchrones (`AppExecutors`).
- **Rapidité DB** : Création d'index SQL sur les colonnes clés (favoris, produits actifs) pour des recherches instantanées.
- **Listes Fluides** : Utilisation de `DiffUtil` dans `RecyclerView` pour éviter les scintillements lors de la mise à jour des listes.
- **Chargement d'Images** : Intégration de **Glide** avec gestion intelligente du cache et redimensionnement automatique.

---

## 🏗️ Architecture Mobile
- **Root Layouts** : Passage massif aux `ConstraintLayout` pour aplatir la hiérarchie des vues et accélérer le rendu.
- **Code Clean-up** : Suppression des fuites de mémoire dans `DBHelper` et sécurisation des `NullPointerException`.
- **Roadmap MVVM** : Plan de migration vers Kotlin, Room et LiveData prêt pour la prochaine phase.

---

## 🛠️ Stack Technique
- **Langage** : Java (JDK 17)
- **UI** : XML / Material Components
- **Persistence** : SQLite
- **Image Loading** : Glide
- **Analyse Statique** : Android Lint
- **Build System** : Gradle (Kotlin DSL)

---

## 📖 Rapports Détaillés (Internes)
Pour plus de détails techniques, consultez les rapports d'audit générés durant le développement :
- `security_audit.md` : Détails des vulnérabilités corrigées.
- `ui_ux_audit.md` : Analyse de l'expérience utilisateur.
- `performance_report.md` : Métriques et optimisations DB/UI.
- `refactoring_plan.md` : Stratégie de migration vers MVVM et Kotlin.

---

## 👨‍💻 Installation
1. Cloner le repository.
2. Ouvrir avec **Android Studio Ladybug** ou une version plus récente.
3. Synchroniser Gradle.
4. Lancer sur un émulateur ou un appareil physique (API 24+).

---

## 🔧 Environnement de Développement

Ce projet utilise **[mise](https://mise.jdx.dev/)** pour gérer les versions des outils de développement.

### Prérequis
- [mise](https://mise.jdx.dev/) installé sur votre machine

### Configuration
```powershell
# Installer les outils (JDK 17, Gradle 8.6)
mise install

# Activer l'environnement (PowerShell)
mise activate powershell | Out-String | Invoke-Expression

# Vérifier les versions
mise current
```

### Versions Gérées
| Outil | Version | Description |
|-------|---------|-------------|
| Java | Temurin 17 | JDK requis pour Android |
| Gradle | 8.6 | Build system |

> **Note**: Android Studio utilisera automatiquement le JDK géré par mise si `JAVA_HOME` est correctement configuré.

---

*Projet modernisé avec ❤️ par Antigravity (Advanced Agentic Coding).*

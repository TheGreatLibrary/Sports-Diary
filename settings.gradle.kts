pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "SportsDiary"
include(":app")
include(":core:database")
include(":core:domain")
include(":core:datastore")
include(":features:home")
include(":core:ui")
include(":features:menu")
include(":features:settings")
include(":features:statistic")
include(":features:statisticDiagram")
include(":features:trainings")
include(":features:proportions")
include(":features:exercises")
include(":features:routines")
include(":features:pageProportion")
include(":features:pageTraining")
include(":features:pageRoutine")
include(":features:pageExercise")
include(":features:pageExercise")
include(":features:editExercise")
include(":features:backup")
include(":features:calculator")
include(":features:health")
include(":features:notes")
include(":widgets:calendar")
include(":core:health")

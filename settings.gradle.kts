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

rootProject.name = "MishinApp"

include(":core:database")
include(":core:domain")
include(":core:data")
include(":core:ui")
include(":core:common")
include(":feature:dashboard")
include(":feature:orders")
include(":feature:menu")
include(":feature:inventory")
include(":feature:reservations")
include(":feature:games")
include(":feature:cats")
include(":feature:reports")
include(":feature:settings")

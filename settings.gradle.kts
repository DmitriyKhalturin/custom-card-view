@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

  repositories {
    google()
    mavenCentral()
    maven("https://jitpack.io")
  }

  versionCatalogs {
    create("versionCatalog") {
      from(files("versionCatalog.toml"))
    }
  }
}

pluginManagement {
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
  }

  plugins {
    // Old implementation.
  }
}

rootProject.name = "CustomCardView"

include(":app")
include(":customcardview")

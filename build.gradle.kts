// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  alias(versionCatalog.plugins.android.application) apply false
  alias(versionCatalog.plugins.android.library) apply false
  alias(versionCatalog.plugins.kotlin.android) apply false
}

tasks.create<Delete>("clean") {
  delete = setOf(
    rootProject.layout.buildDirectory
  )
}

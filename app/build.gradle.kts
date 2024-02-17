plugins {
  alias(versionCatalog.plugins.android.application)
  alias(versionCatalog.plugins.kotlin.android)
}

android {
  namespace = "easy.peasy.cardview.example"
  compileSdk = versionCatalog.versions.compileSdk.get().toInt()

  defaultConfig {
    applicationId = "easy.peasy.cardview.example"

    minSdk = versionCatalog.versions.minSdk.get().toInt()
    targetSdk = versionCatalog.versions.targetSdk.get().toInt()

    versionCode = versionCatalog.versions.versionCode.get().toInt()
    versionName = versionCatalog.versions.versionName.get()
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  kotlinOptions {
    jvmTarget = "1.8"
  }
}

dependencies {
  implementation(versionCatalog.androidCore)
  implementation(versionCatalog.appcompat)
  implementation(versionCatalog.activity)
  implementation(versionCatalog.material)
  implementation(versionCatalog.constraintlayout)

  implementation(project(":customcardview"))
}

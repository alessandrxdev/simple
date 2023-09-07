pluginManagement {
  repositories {
    google()
    mavenCentral()
    maven { setUrl ("https://jitpack.io") }
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
    maven { setUrl ("https://jitpack.io") }
  }
}

rootProject.name = "Application"

include(":app")
include(":preference")
include(":bugsend")
include(":fingerprint")
include(":photopicker")
include(":ussd")

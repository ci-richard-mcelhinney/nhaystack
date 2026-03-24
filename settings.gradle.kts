/*
 * Copyright 2025 CIT. All Rights Reserved.
 */

import com.tridium.gradle.plugins.settings.MultiProjectExtension
import com.tridium.gradle.plugins.settings.LocalSettingsExtension

pluginManagement {
  val niagaraHome: Provider<String> = providers.gradleProperty("niagara_home").orElse(
    providers.systemProperty("niagara_home").orElse(
      providers.environmentVariable("NIAGARA_HOME").orElse(
        providers.environmentVariable("niagara_home")
      )
    )
  )

  val gradlePluginHome: String = providers.gradleProperty("gradlePluginHome").orElse(
    providers.environmentVariable("GRADLE_PLUGIN_HOME").orElse (
      niagaraHome.map { "$it/etc/m2/repository" }
    )
  ).orNull ?: throw InvalidUserDataException(buildString {
    val isWindows = providers.systemProperty("os.name").map { it.toLowerCase(java.util.Locale.ENGLISH) }.get().contains("windows")
    val propsFile = File(rootDir, "gradle.properties")

    appendLine("************************************************************")
    appendLine("ERROR: Invalid project configuration: Cannot derive value of 'gradlePluginHome'.")
    appendLine()
    if (propsFile.exists()) {
      appendLine("You can set it by editing the properties file at:")
    } else {
      appendLine("You can set it by creating a properties file at:")
    }
    appendLine()
    appendLine("  $propsFile")
    appendLine()
    appendLine("and adding 'gradlePluginHome':")
    appendLine()
    if (isWindows) {
      appendLine("  gradlePluginHome=C:\\\\path\\\\to\\\\plugins")
    } else {
      appendLine("  gradlePluginHome=/path/to/plugins")
    }
    appendLine()
    appendLine("You can also set it by defining the 'GRADLE_PLUGIN_HOME' environment varaible:")
    appendLine()
    if (isWindows) {
      appendLine("  set GRADLE_PLUGIN_HOME=C:\\\\path\\\\to\\\\plugins")
    } else {
      appendLine("  export GRADLE_PLUGIN_HOME=/path/to/plugins")
    }
    appendLine()
    appendLine("------------------------------------------------------------")
    appendLine()
    append("If you are using the plugins shipped with the version of Niagara you are building against, ")
    if (propsFile.exists()) {
      appendLine("you can edit the properties file at:")
    } else {
      appendLine("you can create a properties file at:")
    }
    appendLine()
    appendLine("  $propsFile")
    appendLine()
    appendLine("and add 'niagara_home':")
    appendLine()
    if (isWindows) {
      appendLine("  niagara_home=C:\\\\Niagara\\\\Niagara-4.x.y.z")
    } else {
      appendLine("  niagara_home=/opt/Niagara-4.x.y.z")
    }
    appendLine()
    appendLine("You can also set it by defining the 'NIAGARA_HOME' environment variable:")
    appendLine()
    if (isWindows) {
      appendLine("  set NIAGARA_HOME=C:\\\\Niagara\\\\Niagara-4.x.y.z")
    } else {
      appendLine("  export NIAGARA_HOME=/opt/Niagara-4.x.y.z")
    }
    appendLine()
    appendLine("************************************************************")
  })

  val gradlePluginRepoUrl = "file:///${gradlePluginHome.replace('\\', '/')}"

  val gradlePluginVersion: String = "7.6.22"
  val settingsPluginVersion: String = "7.6.3"

  repositories {
    maven(url = "$gradlePluginRepoUrl")
    mavenLocal()
    gradlePluginPortal()
  }

  plugins {
    id("com.tridium.settings.multi-project") version (settingsPluginVersion)
    id("com.tridium.settings.local-settings-convention") version (settingsPluginVersion)

    id("com.tridium.niagara") version (gradlePluginVersion)
    id("com.tridium.vendor") version (gradlePluginVersion)
    id("com.tridium.niagara-module") version (gradlePluginVersion)
    id("com.tridium.niagara-signing") version (gradlePluginVersion)

    id("com.tridium.convention.niagara-home-repositories") version (gradlePluginVersion)
  }
}

plugins {
  // Discover all subprojects in this build
  id("com.tridium.settings.multi-project")

  // Apply local settings from local/my-niagara.gradle(.kts) if they
  // are present
  id("com.tridium.settings.local-settings-convention")
}

configure<LocalSettingsExtension> {
  loadLocalSettings()
}

configure<MultiProjectExtension> {
  // Note: If you have specific subfolder(s) to include, you can pass their relative
  // path as an argument:
  //
  //   findProjects("folder1")
  //   findProjects("folder2")
  //
  // Otherwise, this will find all projects under the root directory with the following layout:
  //
  //    project-rt/project-rt.gradle.kts
  //    project-rt/project-rt.gradle
  //    project-rt/build.gradle.kts
  //    project-rt/build.gradle
  findProjects()
}

rootProject.name = "nhaystack"

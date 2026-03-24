/*
 * Copyright 2025 CIT. All Rights Reserved.
 */

import com.tridium.gradle.plugins.bajadoc.task.Bajadoc
import com.tridium.gradle.plugins.module.util.ModulePart.RuntimeProfile.*

plugins {
  // The Niagara Module plugin configures the "moduleManifest" extension and the
  // "jar" and "moduleTestJar" tasks.
  id("com.tridium.niagara-module")

  // The signing plugin configures the correct signing of modules. It requires
  // that the plugin also be applied to the root project.
  id("com.tridium.niagara-signing")

  // The bajadoc plugin configures the generation of Bajadoc for a module.
  id("com.tridium.bajadoc")

  // Configures JaCoCo for the "niagaraTest" task of this module.
  id("com.tridium.niagara-jacoco")

  // The Annotation processors plugin adds default dependencies on ":nre"
  // for the "annotationProcessor" and "moduleTestAnnotationProcessor"
  // configurations by creating a single "niagaraAnnotationProcessor"
  // configuration they extend from. This value can be overridden by explicitly
  // declaring a dependency for the "niagaraAnnotationProcessor" configuration.
  id("com.tridium.niagara-annotation-processors")

  // The niagara_home repositories convention plugin configures !bin/ext and
  // !modules as flat-file Maven repositories so that projects in this build can
  // depend on already-installed Niagara modules.
  id("com.tridium.convention.niagara-home-repositories")
}

description = "NHaystack"

moduleManifest {
  moduleName.set("nhaystack")
  runtimeProfile.set(rt)
}

configurations {
  create("externalLibs")
}

// See documentation at module://docDeveloper/doc/build.html#dependencies for the supported
// dependency types
dependencies {
//  implementation(files("c:\\dev\\nhaystack\\lib\\haystack-java.jar"))
  uberjar(files("c:\\dev\\nhaystack\\nhaystack-rt\\lib\\haystack-java.jar"))

  // NRE dependencies
  nre(":nre")

  // Niagara module dependencies
  api(":baja")
  api(":alarm-rt")
  api(":bql-rt")
  api(":control-rt")
  api(":driver-rt")
  api(":fox-rt")
  api(":haystack-rt")
  api(":history-rt")
  api(":net-rt")
  api(":niagaraDriver-rt")
  api(":schedule-rt")
  api(":tagdictionary-rt")
  api(":web-rt")

  // Test Niagara module dependencies
  moduleTestImplementation(":test-se")
  moduleTestImplementation(":haystack-rt")
  moduleTestImplementation(":kitControl-rt")
  moduleTestImplementation(":jetty-rt")
  moduleTestImplementation(":bacnet-rt")
  moduleTestImplementation(":test-wb")

  testImplementation("org.testng:testng:6.10")
  testImplementation("Tridium:alarm-rt")

  uberjar("javax.servlet:javax.servlet-api:3.0.1")

  testUberjar("com.e-movimento.tinytools:privilegedaccessor:1.2.2")
  testUberjar("org.mockito:mockito-inline:2.24.0")
}

tasks.named<Jar>("jar") {
  from("src") {
    include("nhaystack/res/*.csv")
    include("nhaystack/res/*.txt")
    include("nhaystack/res/equip-points/*.txt")
    include("nhaystack/icons/*.*")
  }
}

//tasks.named<Bajadoc>("bajadoc") {
  // Each of the packages you wish to include in your module's API documentation must be
  // enumerated below
//  includePackage("com.cit.nblankmodule")
//  }

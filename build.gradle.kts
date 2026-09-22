/*
 * Copyright 2025 CIT. All Rights Reserved.
 */

plugins {
  // Base Niagara plugin
  id("com.tridium.niagara")

  // The vendor plugin provides the vendor {} extension to set the default group
  // for Maven publishing; the default vendor attribute for installable
  // manifests; and the default module and dist version for their respective
  // manifests
  id("com.tridium.vendor")

  // The signing plugin configures signing of all executables, modules, and
  // dists. It also registers a factory only on the root project to avoid
  // overhead from managing signing profiles on all subprojects
  id("com.tridium.niagara-signing")

  // The niagara_home repositories convention plugin configures !bin/ext and
  // !modules as flat-file Maven repositories to allow modules to compile against
  // Niagara
  id("com.tridium.convention.niagara-home-repositories")
  id("com.tridium.niagara-module") apply false
}


vendor {
  // defaultVendor sets the "vendor" attribute on module and dist files; it's
  // what's shown in Niagara when viewing a module or dist.
  defaultVendor("Project Haystack")

  // defaultModuleVersion sets the "vendorVersion" attribute on all modules
  defaultModuleVersion("4.0.0.0")
}


////////////////////////////////////////////////////////////////
// Dependencies and configurations... configuration
////////////////////////////////////////////////////////////////

subprojects {
  repositories {
    mavenCentral()
  }
}


////////////////////////////////////////////////////////////////
// Module signing
////////////////////////////////////////////////////////////////

signingServices {
  // Disable the use of the default profile; this will cause build failures instead
  // of silently falling back to the default
  signingProfileFactory {
    allowDefaultProfile.set(false)
  }
}

niagaraSigning {
  val certAlias: String =
    providers.gradleProperty("certAlias").orElse(providers.environmentVariable("CERT_ALIAS").orElse("none")).get()
  aliases.set(listOf(certAlias))

  val signingProfile: String = 
    providers.gradleProperty("niagara.signing.profile").orElse(providers.environmentVariable("SIGNING_PROFILE").orElse("none")).get()      
  signingProfileFile.set(File(signingProfile))
}

val niagaraHome: Provider<String> = providers.gradleProperty("niagara_home")
val smctlKeypairAlias: Provider<String> = providers.environmentVariable("SMCTL_KEYPAIR_ALIAS")

// signReleaseModules is release-only and is not part of the normal build/test loop --
// run it explicitly with `gradlew signReleaseModules`. Local/dev builds are signed
// automatically by the niagaraSigning plugin (see niagaraSigning {} above) as part of
// each module's jar task.
val signReleaseModules by tasks.registering {
  group = "signing"
  description = "Signs the nhaystack module jars installed in niagara_home/modules for release, using smctl"

  // These tasks install their jars into niagara_home/modules as part of their own
  // execution, so signing can only happen once they've completed.
  dependsOn(":nhaystack-rt:jar", ":nhaystack-wb:jar", ":nhaystack-rt:moduleTestJar")

  doLast {
    val modulesDir = File(niagaraHome.get(), "modules")
    listOf("nhaystack-rt.jar", "nhaystack-wb.jar", "nhaystack-rtTest.jar").forEach { jarName ->
      val jarFile = File(modulesDir, jarName)
      val output = java.io.ByteArrayOutputStream()
      exec {
        commandLine("smctl", "sign", "--keypair-alias=${smctlKeypairAlias.get()}", "--input", jarFile.absolutePath, "--simple")
        standardOutput = output
        errorOutput = output
      }
      val outputText = output.toString()
      print(outputText)
      // smctl always exits 0, even on failure, so success has to be checked from its output.
      if (outputText.contains("FAILED")) {
        throw GradleException("smctl failed to sign $jarFile")
      }
    }
  }
}

subprojects {
  tasks.matching { it.name == "niagaraTest" }.configureEach {
    // Skip niagaraTest when it's only pulled in transitively by build/check; only run
    // it when explicitly requested, e.g. `gradlew niagaraTest` or
    // `gradlew nhaystack-rt:niagaraTest`.
    onlyIf {
      gradle.startParameter.taskNames.any { requested ->
        requested == "niagaraTest" || requested.endsWith(":niagaraTest")
      }
    }
  }
}

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
}


vendor {
  // defaultVendor sets the "vendor" attribute on module and dist files; it's
  // what's shown in Niagara when viewing a module or dist.
  defaultVendor("CIT")

  // defaultModuleVersion sets the "vendorVersion" attribute on all modules
  defaultModuleVersion("1.0")
}


////////////////////////////////////////////////////////////////
// Dependencies and configurations... configuration
////////////////////////////////////////////////////////////////

subprojects {
  repositories {
    mavenCentral()
  }
}

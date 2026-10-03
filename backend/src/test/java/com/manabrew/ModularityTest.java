package com.manabrew;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

/**
 * Spring Modulith: each top-level package under com.manabrew is a module. Other modules may use
 * only its base-package public types, never its sub-packages (internals).
 */
class ModularityTest {

  private static final ApplicationModules MODULES =
      ApplicationModules.of(ManabrewApplication.class);

  @Test
  void modulesRespectBoundaries() {
    MODULES.verify();
  }

  /** Writes module diagrams (PlantUML) to build/spring-modulith-docs. */
  @Test
  void writeModuleDocumentation() {
    new Documenter(MODULES).writeDocumentation();
  }
}

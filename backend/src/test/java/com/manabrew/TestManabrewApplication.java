package com.manabrew;

import org.springframework.boot.SpringApplication;

public class TestManabrewApplication {

  public static void main(String[] args) {
    SpringApplication.from(ManabrewApplication::main)
        .with(TestcontainersConfiguration.class)
        .run(args);
  }
}

package com.bridge.medic.config.local;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

public class DotenvPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String profile = environment.getProperty("spring.profiles.active");
        if (profile == null || !profile.contains("local")) {
            return;
        }

        Dotenv dotenv = Dotenv.configure()
                .filename(".env")
                .ignoreIfMissing()
                .load();

        dotenv.entries().forEach(entry ->
                environment.getSystemProperties().putIfAbsent(entry.getKey(), entry.getValue())
        );
    }
}
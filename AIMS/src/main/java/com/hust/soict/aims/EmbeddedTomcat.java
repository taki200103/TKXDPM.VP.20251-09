package com.hust.soict.aims;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
public class EmbeddedTomcat {
    private static ConfigurableApplicationContext applicationContext;

    public static ConfigurableApplicationContext startAndGetContext(String[] args) {
        if (applicationContext != null) {
            return applicationContext;
        }

        CompletableFuture<ConfigurableApplicationContext> contextFuture = new CompletableFuture<>();
        Thread t = new Thread(() -> {
            SpringApplication app = new SpringApplication(EmbeddedTomcat.class);
            // Disable headless mode to allow Swing GUI
            app.setHeadless(false);
            var ctx = app.run(args);
            applicationContext = ctx;
            contextFuture.complete(ctx);
        }, "spring-boot-thread");
        t.setDaemon(true);
        t.start();

        try {
            return contextFuture.get(); // Wait for context to be ready
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to start Spring context", e);
        }
    }

    public static ConfigurableApplicationContext getContext() {
        return applicationContext;
    }
}

package org.cs4471.helloworld;

import org.cs4471.helloworld_registry.shared.RegistryStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class Registry {
    private static WebClient client;

    public static void Set(String url) {
        client = WebClient.builder().baseUrl(url).build();
    }

    public static RegistryStatus.REGISTRY_STATUS Register() {
        String result = client.get().uri("/register").retrieve().bodyToMono(String.class)
                .timeout(Duration.ofSeconds(10))
                .onErrorResume(Exception.class, ex -> Mono.just(RegistryStatus.REGISTRY_STATUS.FAILURE.toString()))
                .block();

        if (result == null) return RegistryStatus.REGISTRY_STATUS.FAILURE;

        switch (result) {
            case "SUCCESS":
                return RegistryStatus.REGISTRY_STATUS.SUCCESS;
            case "BUSY":
                return RegistryStatus.REGISTRY_STATUS.BUSY;
        }

        return RegistryStatus.REGISTRY_STATUS.FAILURE;
    }

    public static void Deregister() {
        client.get().uri("/deregister").retrieve().bodyToMono(Boolean.class);
    }
}

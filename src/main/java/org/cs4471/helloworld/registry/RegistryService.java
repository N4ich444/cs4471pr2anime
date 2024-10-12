package org.cs4471.helloworld.registry;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component("registryService")
public class RegistryService {
    private String serviceName;
    private WebClient client;

    public void Set(String serviceName, String url) {
        this.serviceName = serviceName;
        client = WebClient.builder().baseUrl(url).build();
    }

    public RegistryStatus.REGISTRY_STATUS Register() {
        String send = String.format("/register?service=%s", serviceName);
        String result = client.get().uri(send).retrieve().bodyToMono(String.class)
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

    public void Deregister() {
        client.get().uri(String.format("/deregister?service=%s", serviceName)).retrieve().bodyToMono(Boolean.class);
    }
}

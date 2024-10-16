package org.cs4471.helloworld.controller;

import org.cs4471.helloworld.Response;
import org.cs4471.helloworld.registry.RegistryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    @Autowired
    private RegistryService registryService;

    @GetMapping("/")
    public Response root() {
        return new Response(200, "HelloWorld", "Hello World!");
    }

    @GetMapping("/heartbeat")
    public Response heartbeat() {
        System.out.println("HelloWorld : Received heartbeat ping from registry");
        return new Response(200, "HelloWorld", "");

    }

    @GetMapping("/exit")
    public void exit() {
        System.out.println("HelloWorld : Exiting in 30 seconds...");
        // Deregister and exit
        registryService.Deregister();
        System.out.println("HelloWorld : Exiting...");
        System.exit(1);
    }
}

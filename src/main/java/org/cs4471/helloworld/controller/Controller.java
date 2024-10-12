package org.cs4471.helloworld.controller;

import org.cs4471.helloworld.registry.RegistryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    @Autowired
    private RegistryService registryService;

    @GetMapping("/")
    public String root() {
        return "Hello World";
    }

    @GetMapping("/heartbeat")
    public boolean heartbeat() {
        System.out.println("HelloWorld : Received heartbeat ping from registry");
        return true;
    }

    @GetMapping("/exit")
    public void exit() {
        System.out.println("HelloWorld : Exiting...");
        // Deregister and exit
        registryService.Deregister();
        System.exit(1);
    }
}

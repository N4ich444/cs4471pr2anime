package org.cs4471.helloworld.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.cs4471.helloworld.Registry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    @GetMapping("/url")
    public String getServiceURL(HttpServletRequest request) {
        return request.getRequestURL().toString();
    }

    @GetMapping("/")
    public String HelloWorld() {
        return "Hello World";
    }

    @GetMapping("/heartbeat")
    public boolean Heartbeat() {
        return true;
    }

    @GetMapping("/exit")
    public void Exit() {
        // Deregister and exit
        Registry.Deregister();
        System.exit(1);
    }
}

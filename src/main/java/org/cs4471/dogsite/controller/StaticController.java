package org.cs4471.dogsite.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.HashMap;

@Controller
public class StaticController {
    @GetMapping("/")
    public String testpage(Model model) {
        String response = WebClient.builder().baseUrl("https://dog.ceo/api/breeds/image/random").build().get().retrieve().bodyToMono(String.class)
                .timeout(Duration.ofSeconds(10))
                .onErrorResume(Exception.class, ex -> Mono.just(""))
                .block();

        Type type = new TypeToken<HashMap<String, String>>(){}.getType();
        HashMap<String, String> mapping = new Gson().fromJson(response, type);

        model.addAttribute("imgurl", mapping.get("message"));
        return "hello";
    }
}

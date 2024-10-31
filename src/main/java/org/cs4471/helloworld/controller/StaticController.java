package org.cs4471.helloworld.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaticController {
    // For Thymeleaf pages!!!
    @GetMapping("/testPage")
    public String testpage(Model model) {
        model.addAttribute("myvalue", "test value 123");
        return "hello";
    }
}

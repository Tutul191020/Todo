package com.example.todo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {
    @GetMapping("/hi")
    public String healthCheck() {
        return "All is well";
    }

    @GetMapping("/check")
    public String check() {
        return "Am  I Just checking ?";
    }
}
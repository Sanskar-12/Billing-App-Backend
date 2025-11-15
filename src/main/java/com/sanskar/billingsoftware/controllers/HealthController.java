package com.sanskar.billingsoftware.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/ok")
    public String healthCheck() {
        return "Okay";
    }
}

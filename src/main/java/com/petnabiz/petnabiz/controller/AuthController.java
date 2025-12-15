package com.petnabiz.petnabiz.controller;

import com.petnabiz.petnabiz.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password) {
        return userService.authenticate(email, password)
                .map(u -> "OK " + u.getRole())   // istersen userId döndür
                .orElse("FAIL");
    }

    @GetMapping("/ping")
    public String ping() { return "OK"; }

}


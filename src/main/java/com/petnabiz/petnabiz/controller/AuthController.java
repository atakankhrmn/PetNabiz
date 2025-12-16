package com.petnabiz.petnabiz.controller;

import com.petnabiz.petnabiz.dto.request.user.AuthRequestDTO;
import com.petnabiz.petnabiz.dto.response.user.AuthResponseDTO;
import com.petnabiz.petnabiz.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO req) {
        AuthResponseDTO res = userService.authenticate(req);

        return res.isAuthenticated()
                ? ResponseEntity.ok(res)
                : ResponseEntity.status(401).body(res);
    }

    @GetMapping("/ping")
    public String ping() {
        return "OK";
    }
}

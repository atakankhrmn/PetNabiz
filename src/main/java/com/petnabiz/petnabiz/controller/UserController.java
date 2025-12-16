package com.petnabiz.petnabiz.controller;

import com.petnabiz.petnabiz.dto.request.user.*;
import com.petnabiz.petnabiz.dto.response.user.AuthResponseDTO;
import com.petnabiz.petnabiz.dto.response.user.UserResponseDTO;
import com.petnabiz.petnabiz.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserResponseDTO>> getUsersByRole(@PathVariable String role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    @GetMapping("/active")
    public ResponseEntity<List<UserResponseDTO>> getActiveUsers() {
        return ResponseEntity.ok(userService.getActiveUsers());
    }

    @GetMapping("/inactive")
    public ResponseEntity<List<UserResponseDTO>> getInactiveUsers() {
        return ResponseEntity.ok(userService.getInactiveUsers());
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserCreateRequestDTO dto) {
        return ResponseEntity.ok(userService.createUser(dto));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable String userId,
            @RequestBody UserUpdateRequestDTO dto
    ) {
        return ResponseEntity.ok(userService.updateUser(userId, dto));
    }

    @PutMapping("/{userId}/password")
    public ResponseEntity<UserResponseDTO> updatePassword(
            @PathVariable String userId,
            @RequestBody UserPasswordUpdateRequestDTO dto
    ) {
        return ResponseEntity.ok(userService.updatePassword(userId, dto));
    }

    @PutMapping("/{userId}/status")
    public ResponseEntity<UserResponseDTO> setActiveStatus(
            @PathVariable String userId,
            @RequestParam boolean active
    ) {
        return ResponseEntity.ok(userService.setActiveStatus(userId, active));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponseDTO> authenticate(@RequestBody AuthRequestDTO dto) {
        AuthResponseDTO res = userService.authenticate(dto);
        return res.isAuthenticated()
                ? ResponseEntity.ok(res)
                : ResponseEntity.status(401).body(res);
    }
}

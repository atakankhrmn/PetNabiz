package com.petnabiz.petnabiz.controller;

import com.petnabiz.petnabiz.model.User;
import com.petnabiz.petnabiz.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // Constructor injection
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 1) Tüm kullanıcıları getir
     * GET /api/users
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * 2) ID'ye göre user getir
     * GET /api/users/{userId}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable String userId) {
        Optional<User> userOpt = userService.getUserById(userId);
        return userOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 3) Email'e göre user getir
     * GET /api/users/email/{email}
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        Optional<User> userOpt = userService.getUserByEmail(email);
        return userOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 4) Role göre user listesi
     * GET /api/users/role/{role}
     * Ör: /api/users/role/ADMIN
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable String role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    /**
     * 5) Aktif kullanıcılar
     * GET /api/users/active
     */
    @GetMapping("/active")
    public ResponseEntity<List<User>> getActiveUsers() {
        return ResponseEntity.ok(userService.getActiveUsers());
    }

    /**
     * 6) Pasif kullanıcılar
     * GET /api/users/inactive
     */
    @GetMapping("/inactive")
    public ResponseEntity<List<User>> getInactiveUsers() {
        return ResponseEntity.ok(userService.getInactiveUsers());
    }

    /**
     * 7) Yeni user oluştur
     * POST /api/users
     *
     * Body ör:
     * {
     *   "userId": "U001",
     *   "email": "test@test.com",
     *   "password": "123456",
     *   "role": "OWNER",
     *   "active": true
     * }
     */
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User created = userService.createUser(user);
        return ResponseEntity.ok(created);
    }

    /**
     * 8) User güncelle (email/role/active vs)
     * PUT /api/users/{userId}
     */
    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(
            @PathVariable String userId,
            @RequestBody User updatedUser
    ) {
        User updated = userService.updateUser(userId, updatedUser);
        return ResponseEntity.ok(updated);
    }

    /**
     * 9) User şifresi güncelle
     * PUT /api/users/{userId}/password
     *
     * Body:
     * { "newPassword": "yeniSifre123" }
     */
    @PutMapping("/{userId}/password")
    public ResponseEntity<User> updatePassword(
            @PathVariable String userId,
            @RequestBody Map<String, String> body
    ) {
        String newPassword = body.get("newPassword");
        User updated = userService.updatePassword(userId, newPassword);
        return ResponseEntity.ok(updated);
    }

    /**
     * 10) User aktif/pasif durumu değiştir
     * PUT /api/users/{userId}/status?active=true
     */
    @PutMapping("/{userId}/status")
    public ResponseEntity<User> setActiveStatus(
            @PathVariable String userId,
            @RequestParam boolean active
    ) {
        User updated = userService.setActiveStatus(userId, active);
        return ResponseEntity.ok(updated);
    }

    /**
     * 11) User sil
     * DELETE /api/users/{userId}
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 12) Basit authenticate (email + password)
     * POST /api/users/authenticate
     *
     * Body:
     * {
     *   "email": "test@test.com",
     *   "password": "123456"
     * }
     *
     * NOT: Şu an plain text check, production için JWT + encoder ile geliştirilir.
     */
    @PostMapping("/authenticate")
    public ResponseEntity<User> authenticate(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        Optional<User> userOpt = userService.authenticate(email, password);

        return userOpt
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(401).build());
    }
}

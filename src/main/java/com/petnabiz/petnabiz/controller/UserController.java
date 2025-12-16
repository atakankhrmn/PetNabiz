package com.petnabiz.petnabiz.controller;

import com.petnabiz.petnabiz.dto.request.user.UserCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.user.UserPasswordUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.request.user.UserUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.user.UserResponseDTO;
import com.petnabiz.petnabiz.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ADMIN
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // SELF
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN','OWNER','CLINIC')")
    public ResponseEntity<UserResponseDTO> getMe() {
        return ResponseEntity.ok(userService.getMe());
    }

    // ADMIN or SELF (id ile erişim IDOR olmasın)
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @userService.isSelf(#userId, authentication.name)")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    // email ile user çekmek privacy -> ADMIN
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getUsersByRole(@PathVariable String role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getActiveUsers() {
        return ResponseEntity.ok(userService.getActiveUsers());
    }

    @GetMapping("/inactive")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getInactiveUsers() {
        return ResponseEntity.ok(userService.getInactiveUsers());
    }

    // user create admin işi (register endpointlerin owner/clinic/admin create’lerde zaten user yaratıyorsun)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserCreateRequestDTO dto) {
        UserResponseDTO created = userService.createUser(dto);
        URI location = URI.create("/api/users/" + created.getUserId());
        return ResponseEntity.created(location).body(created);
    }

    // ADMIN (role/email gibi alanlar admin işi)
    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable String userId,
            @RequestBody UserUpdateRequestDTO dto
    ) {
        return ResponseEntity.ok(userService.updateUser(userId, dto));
    }

    // SELF password change
    @PutMapping("/me/password")
    @PreAuthorize("hasAnyRole('ADMIN','OWNER','CLINIC')")
    public ResponseEntity<UserResponseDTO> updateMyPassword(@RequestBody UserPasswordUpdateRequestDTO dto) {
        return ResponseEntity.ok(userService.updateMyPassword(dto));
    }

    // ADMIN can reset others
    @PutMapping("/{userId}/password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> updatePassword(
            @PathVariable String userId,
            @RequestBody UserPasswordUpdateRequestDTO dto
    ) {
        return ResponseEntity.ok(userService.updatePassword(userId, dto));
    }

    @PutMapping("/{userId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> setActiveStatus(
            @PathVariable String userId,
            @RequestParam boolean active
    ) {
        return ResponseEntity.ok(userService.setActiveStatus(userId, active));
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}

package com.petnabiz.petnabiz.controller;

import com.petnabiz.petnabiz.model.User;
import com.petnabiz.petnabiz.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // CREATE
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    // GET ALL
    @GetMapping
    public List<User> getAll() {
        return userService.getAllUsers();
    }

    // GET BY ID
    @GetMapping("/{id}")
    public User getById(@PathVariable String id) {
        return userService.getById(id);
    }

    // UPDATE
    @PutMapping("/{id}")
    public User update(@PathVariable String id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    // SOFT DELETE
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        userService.softDelete(id);
    }

    // ✔ LOGIN BURADA
    public static class LoginRequest {
        public String email;
        public String password;
    }

    @PostMapping("/login")
    public User login(@RequestBody LoginRequest request) {
        return userService.login(request.email, request.password);
    }
}


package com.petnabiz.petnabiz.service;

import com.petnabiz.petnabiz.model.User;
import com.petnabiz.petnabiz.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private String generateUserId() {
        return "USR-" + UUID.randomUUID().toString().substring(0, 6);
    }

    public User createUser(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already in use!");
        }

        if (user.getUserId() == null || user.getUserId().isEmpty()) {
            user.setUserId(generateUserId());
        }

        user.setActive(true);
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User updateUser(String id, User data) {
        User existing = getById(id);

        existing.setEmail(data.getEmail());
        existing.setPassword(data.getPassword());
        existing.setRole(data.getRole());
        existing.setActive(data.isActive());

        return userRepository.save(existing);
    }

    public void softDelete(String id) {
        User u = getById(id);
        u.setActive(false);
        userRepository.save(u);
    }

    // ✔ LOGIN (Artık UserController üzerinden çağırılacak)
    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email"));

        if (!user.isActive())
            throw new RuntimeException("Account is not active");

        if (!user.getPassword().equals(password))
            throw new RuntimeException("Invalid password");

        return user;
    }
}

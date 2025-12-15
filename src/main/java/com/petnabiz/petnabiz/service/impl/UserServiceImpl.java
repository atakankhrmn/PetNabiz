package com.petnabiz.petnabiz.service.impl;

import com.petnabiz.petnabiz.model.User;
import com.petnabiz.petnabiz.repository.UserRepository;
import com.petnabiz.petnabiz.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(String userId) {
        return userRepository.findByUserId(userId);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> getUsersByRole(String role) {
        return userRepository.findByRoleIgnoreCase(role);
    }

    @Override
    public List<User> getActiveUsers() {
        return userRepository.findByIsActiveTrue();
    }

    @Override
    public List<User> getInactiveUsers() {
        return userRepository.findByIsActiveFalse();
    }

    @Override
    public User createUser(User user) {

        // Email dolu mu?
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email boş olamaz.");
        }

        // Password dolu mu?
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password boş olamaz.");
        }

        // Aynı email'e sahip kullanıcı var mı?
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalStateException("Bu email ile zaten bir kullanıcı kayıtlı: " + user.getEmail());
        }

        // ID çakışması kontrolü
        if (userRepository.existsByUserId(user.getUserId())) {
            throw new IllegalStateException("Bu userId zaten mevcut: " + user.getUserId());
        }

        return userRepository.save(user);
    }

    @Override
    public User updateUser(String userId, User updatedUser) {
        User existing = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User bulunamadı: " + userId));

        // Email değişiyorsa, çakışma kontrolü yap
        if (updatedUser.getEmail() != null) {

            Optional<User> found = userRepository.findByEmail(updatedUser.getEmail());

            // Başka bir kullanıcı aynı email'i kullanıyor mu?
            if (found.isPresent() && !found.get().getUserId().equals(userId)) {
                throw new IllegalStateException("Bu email başka bir kullanıcı tarafından kullanılıyor.");
            }

            existing.setEmail(updatedUser.getEmail());
        }

        // Role güncelle
        if (updatedUser.getRole() != null) {
            existing.setRole(updatedUser.getRole());
        }

        // Active durumu güncelle
        existing.setActive(updatedUser.isActive());

        return userRepository.save(existing);
    }

    @Override
    public User updatePassword(String userId, String newPassword) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User bulunamadı: " + userId));

        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Yeni password boş olamaz.");
        }

        user.setPassword(newPassword);

        return userRepository.save(user);
    }

    @Override
    public User setActiveStatus(String userId, boolean active) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User bulunamadı: " + userId));

        user.setActive(active);
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(String userId) {
        boolean exists = userRepository.existsByUserId(userId);
        if (!exists) {
            throw new IllegalArgumentException("Silinmek istenen user bulunamadı: " + userId);
        }

        userRepository.deleteById(userId);
    }


    //SIKINTI BURASI KARDESIM YA
    @Override
    public Optional<User> authenticate(String email, String password) {
        Optional<User> opt = userRepository.findByEmail(email);
        if (opt.isEmpty()) return Optional.empty();

        User u = opt.get();

        // aktif değilse geçmesin
        if (!u.isActive()) return Optional.empty();

        // düz şifre kıyas
        if (u.getPassword() == null) return Optional.empty();

        return u.getPassword().equals(password) ? Optional.of(u) : Optional.empty();
    }
}

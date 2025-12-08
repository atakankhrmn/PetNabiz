package com.petnabiz.petnabiz.service;

import com.petnabiz.petnabiz.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    // Tüm kullanıcılar
    List<User> getAllUsers();

    // ID ile bul
    Optional<User> getUserById(String userId);

    // Email ile bul
    Optional<User> getUserByEmail(String email);

    // Role göre kullanıcılar (ADMIN, OWNER, VET vs.)
    List<User> getUsersByRole(String role);

    // Aktif kullanıcılar
    List<User> getActiveUsers();

    // Pasif kullanıcılar
    List<User> getInactiveUsers();

    // Yeni kullanıcı oluştur
    User createUser(User user);

    // Kullanıcı güncelle
    User updateUser(String userId, User updatedUser);

    // Şifre güncelle
    User updatePassword(String userId, String newPassword);

    // Aktiflik değiştir
    User setActiveStatus(String userId, boolean active);

    // Kullanıcı sil
    void deleteUser(String userId);

    // Login kontrolü (email + password match)
    Optional<User> authenticate(String email, String password);
}

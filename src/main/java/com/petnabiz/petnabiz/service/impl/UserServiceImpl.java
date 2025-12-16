package com.petnabiz.petnabiz.service.impl;

import com.petnabiz.petnabiz.dto.request.user.*;
import com.petnabiz.petnabiz.dto.response.user.AuthResponseDTO;
import com.petnabiz.petnabiz.dto.response.user.UserResponseDTO;
import com.petnabiz.petnabiz.mapper.UserMapper;
import com.petnabiz.petnabiz.model.User;
import com.petnabiz.petnabiz.repository.UserRepository;
import com.petnabiz.petnabiz.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    public UserResponseDTO getUserById(String userId) {
        User u = userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User bulunamadı: " + userId));
        return userMapper.toResponse(u);
    }

    @Override
    public UserResponseDTO getUserByEmail(String email) {
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User bulunamadı (email): " + email));
        return userMapper.toResponse(u);
    }

    @Override
    public List<UserResponseDTO> getUsersByRole(String role) {
        return userRepository.findByRoleIgnoreCase(role).stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    public List<UserResponseDTO> getActiveUsers() {
        return userRepository.findByIsActiveTrue().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    public List<UserResponseDTO> getInactiveUsers() {
        return userRepository.findByIsActiveFalse().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public UserResponseDTO createUser(UserCreateRequestDTO dto) {

        if (dto.getUserId() == null || dto.getUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("userId boş olamaz.");
        }
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email boş olamaz.");
        }
        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password boş olamaz.");
        }
        if (dto.getRole() == null || dto.getRole().trim().isEmpty()) {
            throw new IllegalArgumentException("Role boş olamaz.");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalStateException("Bu email ile zaten bir kullanıcı kayıtlı: " + dto.getEmail());
        }
        if (userRepository.existsByUserId(dto.getUserId())) {
            throw new IllegalStateException("Bu userId zaten mevcut: " + dto.getUserId());
        }

        User u = new User();
        u.setUserId(dto.getUserId());
        u.setEmail(dto.getEmail());
        u.setPassword(dto.getPassword()); // TODO hash
        u.setRole(dto.getRole());
        u.setActive(dto.isActive());

        User saved = userRepository.save(u);
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public UserResponseDTO updateUser(String userId, UserUpdateRequestDTO dto) {

        User existing = userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User bulunamadı: " + userId));

        if (dto.getEmail() != null) {
            Optional<User> found = userRepository.findByEmail(dto.getEmail());
            if (found.isPresent() && !found.get().getUserId().equals(userId)) {
                throw new IllegalStateException("Bu email başka bir kullanıcı tarafından kullanılıyor.");
            }
            existing.setEmail(dto.getEmail());
        }

        if (dto.getRole() != null) {
            existing.setRole(dto.getRole());
        }

        // en büyük bug: eskiden updatedUser.isActive() ile false basıyordun.
        if (dto.getActive() != null) {
            existing.setActive(dto.getActive());
        }

        User saved = userRepository.save(existing);
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public UserResponseDTO updatePassword(String userId, UserPasswordUpdateRequestDTO dto) {

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User bulunamadı: " + userId));

        if (dto.getNewPassword() == null || dto.getNewPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Yeni password boş olamaz.");
        }

        user.setPassword(dto.getNewPassword()); // TODO hash
        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public UserResponseDTO setActiveStatus(String userId, boolean active) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User bulunamadı: " + userId));

        user.setActive(active);
        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Override
    public void deleteUser(String userId) {
        boolean exists = userRepository.existsByUserId(userId);
        if (!exists) {
            throw new IllegalArgumentException("Silinmek istenen user bulunamadı: " + userId);
        }
        userRepository.deleteById(userId);
    }

    @Override
    public AuthResponseDTO authenticate(AuthRequestDTO dto) {

        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new IllegalArgumentException("email zorunlu.");
        }
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new IllegalArgumentException("password zorunlu.");
        }

        Optional<User> opt = userRepository.findByEmail(dto.getEmail());
        if (opt.isEmpty()) {
            AuthResponseDTO res = new AuthResponseDTO();
            res.setAuthenticated(false);
            res.setUser(null);
            return res;
        }

        User u = opt.get();

        if (!u.isActive()) {
            AuthResponseDTO res = new AuthResponseDTO();
            res.setAuthenticated(false);
            res.setUser(null);
            return res;
        }

        boolean ok = (u.getPassword() != null) && u.getPassword().equals(dto.getPassword());

        AuthResponseDTO res = new AuthResponseDTO();
        res.setAuthenticated(ok);
        res.setUser(ok ? userMapper.toResponse(u) : null);
        return res;
    }
}

package com.petnabiz.petnabiz.service.impl;

import com.petnabiz.petnabiz.dto.request.user.UserCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.user.UserPasswordUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.request.user.UserUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.user.UserResponseDTO;
import com.petnabiz.petnabiz.mapper.UserMapper;
import com.petnabiz.petnabiz.model.Admin;
import com.petnabiz.petnabiz.model.User;
import com.petnabiz.petnabiz.repository.UserRepository;
import com.petnabiz.petnabiz.service.AdminService;
import com.petnabiz.petnabiz.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service("userService") // SpEL için
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AdminService adminService;
    private final ClinicServiceImpl clinicService;
    private final PetOwnerServiceImpl petOwnerServiceImpl;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper, AdminService adminService, ClinicServiceImpl clinicService, PetOwnerServiceImpl petOwnerServiceImpl) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.adminService = adminService;
        this.clinicService = clinicService;
        this.petOwnerServiceImpl = petOwnerServiceImpl;
    }

    private String currentEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) throw new IllegalStateException("Authentication yok.");
        return auth.getName(); // email
    }

    @Override
    public boolean isSelf(String userId, String email) {
        if (userId == null || userId.isBlank() || email == null || email.isBlank()) return false;
        User u = userRepository.findByUserId(userId).orElse(null);
        if (u == null || u.getEmail() == null) return false;
        return email.equalsIgnoreCase(u.getEmail());
    }

    @Override
    public UserResponseDTO getMe() {
        String email = currentEmail();
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User bulunamadı (me): " + email));
        return userMapper.toResponse(u);
    }

    @Override
    @Transactional
    public UserResponseDTO updateMyPassword(UserPasswordUpdateRequestDTO dto) {
        String email = currentEmail();
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User bulunamadı (me): " + email));

        if (dto.getNewPassword() == null || dto.getNewPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Yeni password boş olamaz.");
        }

        u.setPassword(dto.getNewPassword()); // TODO hash
        User saved = userRepository.save(u);
        return userMapper.toResponse(saved);
    }

    // --------- ADMIN OPS ---------

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
    @Transactional
    public void deleteUser(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Silinmek istenen user bulunamadı: " + userId));
        if(user.getRole().equals("ROLE_ADMIN")) {
            adminService.deleteAdmin(user.getUserId());
        }
        else if(user.getRole().equals("ROLE_CLINIC")){
            clinicService.deleteClinic(user.getUserId());
        }
        else if(user.getRole().equals("ROLE_OWNER")){
            petOwnerServiceImpl.deletePetOwner(user.getUserId());
        }
        userRepository.delete(user);
    }
}

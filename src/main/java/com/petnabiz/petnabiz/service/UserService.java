package com.petnabiz.petnabiz.service;

import com.petnabiz.petnabiz.dto.request.user.UserCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.user.UserPasswordUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.request.user.UserUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.user.UserResponseDTO;

import java.util.List;

public interface UserService {

    // ADMIN
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO getUserById(String userId);
    UserResponseDTO getUserByEmail(String email);
    List<UserResponseDTO> getUsersByRole(String role);
    List<UserResponseDTO> getActiveUsers();
    List<UserResponseDTO> getInactiveUsers();

    UserResponseDTO createUser(UserCreateRequestDTO dto);
    UserResponseDTO updateUser(String userId, UserUpdateRequestDTO dto);
    UserResponseDTO setActiveStatus(String userId, boolean active);
    void deleteUser(String userId);

    // SELF / ADMIN
    UserResponseDTO getMe();
    UserResponseDTO updateMyPassword(UserPasswordUpdateRequestDTO dto);

    // Security helper (SpEL)
    boolean isSelf(String userId, String email);
    UserResponseDTO updatePassword(String userId, UserPasswordUpdateRequestDTO dto);
}

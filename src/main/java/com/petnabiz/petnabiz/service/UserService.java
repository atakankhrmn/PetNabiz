package com.petnabiz.petnabiz.service;

import com.petnabiz.petnabiz.dto.request.user.*;
import com.petnabiz.petnabiz.dto.response.user.AuthResponseDTO;
import com.petnabiz.petnabiz.dto.response.user.UserResponseDTO;

import java.util.List;

public interface UserService {

    List<UserResponseDTO> getAllUsers();

    UserResponseDTO getUserById(String userId);

    UserResponseDTO getUserByEmail(String email);

    List<UserResponseDTO> getUsersByRole(String role);

    List<UserResponseDTO> getActiveUsers();

    List<UserResponseDTO> getInactiveUsers();

    UserResponseDTO createUser(UserCreateRequestDTO dto);

    UserResponseDTO updateUser(String userId, UserUpdateRequestDTO dto);

    UserResponseDTO updatePassword(String userId, UserPasswordUpdateRequestDTO dto);

    UserResponseDTO setActiveStatus(String userId, boolean active);

    void deleteUser(String userId);

    AuthResponseDTO authenticate(AuthRequestDTO dto);
}

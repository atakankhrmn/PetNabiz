package com.petnabiz.petnabiz.mapper;

import com.petnabiz.petnabiz.dto.response.user.UserResponseDTO;
import com.petnabiz.petnabiz.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponseDTO toResponse(User u) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setUserId(u.getUserId());
        dto.setEmail(u.getEmail());
        dto.setActive(u.isActive());
        dto.setRole(u.getRole());
        return dto;
    }
}

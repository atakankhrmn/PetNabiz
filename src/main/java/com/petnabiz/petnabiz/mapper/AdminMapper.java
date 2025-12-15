package com.petnabiz.petnabiz.mapper;

import com.petnabiz.petnabiz.dto.response.admin.AdminResponseDTO;
import com.petnabiz.petnabiz.model.Admin;
import org.springframework.stereotype.Component;

@Component
public class AdminMapper {

    public AdminResponseDTO toResponse(Admin admin) {
        AdminResponseDTO dto = new AdminResponseDTO();
        dto.setAdminId(admin.getAdminId());
        dto.setEmail(admin.getUser() != null ? admin.getUser().getEmail() : null);
        dto.setName(admin.getFullName());
        dto.setActive(admin.getUser() != null && admin.getUser().isActive());
        return dto;
    }
}

package com.petnabiz.petnabiz.service.impl;

import com.petnabiz.petnabiz.dto.request.auth.RegisterOwnerRequestDTO;
import com.petnabiz.petnabiz.dto.response.auth.AuthOwnerResponseDTO;
import com.petnabiz.petnabiz.model.PetOwner;
import com.petnabiz.petnabiz.model.User;
import com.petnabiz.petnabiz.repository.PetOwnerRepository;
import com.petnabiz.petnabiz.repository.UserRepository;
import com.petnabiz.petnabiz.service.AuthService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PetOwnerRepository petOwnerRepository;

    public AuthServiceImpl(UserRepository userRepository, PetOwnerRepository petOwnerRepository) {
        this.userRepository = userRepository;
        this.petOwnerRepository = petOwnerRepository;
    }

    @Override
    @Transactional
    public AuthOwnerResponseDTO registerOwner(RegisterOwnerRequestDTO dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalStateException("Email already exists");
        }

        // 1) userId üret (MapsId için ownerId = userId olacak)
        String userId = generateOwnerUserId();

        // 2) User oluştur
        User user = new User();
        user.setUserId(userId);
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword()); // şimdilik plain (Sizde {noop} var)
        user.setActive(true);
        user.setRole("ROLE_OWNER"); // hasRole('OWNER') ile uyumlu

        User savedUser = userRepository.save(user);

        // 3) PetOwner oluştur (MapsId -> owner_id = user_id)
        PetOwner owner = new PetOwner();
        owner.setUser(savedUser);
        owner.setFirstName(dto.getFirstName());
        owner.setLastName(dto.getLastName());
        owner.setPhone(dto.getPhone());
        owner.setAddress(dto.getAddress());

        PetOwner savedOwner = petOwnerRepository.save(owner);

        return toOwnerAuthResponse(savedOwner);
    }

    @Override
    public AuthOwnerResponseDTO me(String email) {
        PetOwner owner = petOwnerRepository.findByUser_Email(email)
                .orElseThrow(() -> new IllegalStateException("Owner profile not found for email: " + email));
        return toOwnerAuthResponse(owner);
    }

    private AuthOwnerResponseDTO toOwnerAuthResponse(PetOwner owner) {
        AuthOwnerResponseDTO res = new AuthOwnerResponseDTO();
        res.setUserId(owner.getUser().getUserId());
        res.setOwnerId(owner.getOwnerId());

        res.setEmail(owner.getUser().getEmail());
        res.setRole(owner.getUser().getRole());

        res.setFirstName(owner.getFirstName());
        res.setLastName(owner.getLastName());
        res.setPhone(owner.getPhone());
        res.setAddress(owner.getAddress());
        return res;
    }

    private String generateOwnerUserId() {
        return "O-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}

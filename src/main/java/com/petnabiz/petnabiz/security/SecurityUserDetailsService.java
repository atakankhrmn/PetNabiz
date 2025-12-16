package com.petnabiz.petnabiz.security;

import com.petnabiz.petnabiz.model.User;
import com.petnabiz.petnabiz.repository.UserRepository;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SecurityUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public SecurityUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        if (!u.isActive()) {
            throw new DisabledException("User is inactive: " + email);
        }

        // Sizde password plain text. O yüzden {noop} ile Spring’e “encoder yok” diyoruz.
        return org.springframework.security.core.userdetails.User
                .withUsername(u.getEmail())
                .password("{noop}" + u.getPassword())
                .authorities(u.getRole()) // ör: ROLE_ADMIN / ROLE_OWNER / ROLE_CLINIC
                .build();
    }
}

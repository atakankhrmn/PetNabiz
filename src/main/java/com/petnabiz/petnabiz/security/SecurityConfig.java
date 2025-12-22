package com.petnabiz.petnabiz.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.AuthenticationEntryPoint;
import java.nio.charset.StandardCharsets;


@Configuration
@EnableMethodSecurity // @PreAuthorize aktif
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(auth -> auth

                .requestMatchers(
                        "/", "/index.html",
                        "/favicon.ico",
                        "/assets/**",
                        "/static/**",
                        "/css/**", "/js/**", "/img/**"
                ).permitAll()


                // Auth endpoints (login/register vs) -> açık
                .requestMatchers("/auth/**", "/api/auth/**").permitAll()

                .requestMatchers("/uploads/**").permitAll()

                /**
                 * CLINIC APPLICATIONS
                 * - create (public): permitAll
                 * - list/approve/reject (admin): ADMIN
                 */
                .requestMatchers("/api/clinic-applications/**").permitAll()

                // AdminController -> sadece ADMIN (controller seviyesinde de var ama burada da kaba kural dursun)
                .requestMatchers("/api/admins/**").hasRole("ADMIN")

                /**
                 * USERS
                 * Controller içinde:
                 * - GET /me -> ADMIN/OWNER/CLINIC
                 * - GET /{id} -> ADMIN or SELF
                 * Ama sen önceki configte hepsini ADMIN yapıp bozuyordun.
                 */
                .requestMatchers("/api/users/**").hasAnyRole("ADMIN", "OWNER", "CLINIC")

                /**
                 * CLINICS
                 * Controller içinde GET'ler owner'a da açık, /my sadece clinic, create admin.
                 * Bu yüzden burada geniş tutuyoruz, detayları @PreAuthorize hallediyor.
                 */
                .requestMatchers("/api/clinics/**").hasAnyRole("ADMIN", "CLINIC", "OWNER")

                /**
                 * VETERINARIES
                 * Controller: ADMIN/CLINIC (clinic owner check method’da var)
                 */
                .requestMatchers("/api/veterinaries/**").hasAnyRole("ADMIN", "CLINIC")

                /**
                 * PET OWNERS
                 * Controller: ADMIN her şey, OWNER kendi profil vs.
                 */
                .requestMatchers("/api/pet-owners/**").hasAnyRole("ADMIN", "OWNER")

                /**
                 * PETS
                 * Controller: ADMIN tüm petler; OWNER /my, get/update/delete kendi pet’i (method’da check var)
                 * O yüzden burada ADMIN+OWNER yeter.
                 */
                .requestMatchers("/api/pets/**").hasAnyRole("ADMIN", "OWNER", "CLINIC")

                /**
                 * SLOTS
                 * Controller: generate admin/clinic, available admin/clinic/owner, book admin/owner
                 */
                .requestMatchers("/api/slots/**").hasAnyRole("ADMIN", "CLINIC", "OWNER")

                /**
                 * APPOINTMENTS
                 * Controller: ADMIN/CLINIC çoğu, OWNER /my ve ownership check’ler var
                 */
                .requestMatchers("/api/appointments/**").hasAnyRole("ADMIN", "CLINIC", "OWNER")

                /**
                 * MEDICINES
                 * Controller: READ admin/clinic/owner, WRITE admin/clinic, DELETE admin
                 */
                .requestMatchers("/api/medicines/**").hasAnyRole("ADMIN", "CLINIC", "OWNER")

                /**
                 * MEDICATIONS
                 * Controller: GET by id/pet/record ownera da (ownership check ile), write admin/clinic
                 */
                .requestMatchers("/api/medications/**").hasAnyRole("ADMIN", "CLINIC", "OWNER")

                /**
                 * MEDICAL RECORDS
                 * Controller: owner bazı GET’lere ownership ile girebiliyor, write admin/clinic
                 */
                .requestMatchers("/api/medical-records/**").hasAnyRole("ADMIN", "CLINIC", "OWNER")

                // Geri kalan her şey login ister
                .anyRequest().authenticated()
        );

        // Siz plain-text {noop} ile basic auth yapıyordunuz; burayı öyle bırakıyorum
        AuthenticationEntryPoint noPopupEntryPoint = (request, response, authException) -> {
            response.setStatus(401);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType("application/json");
            // ⚠️ WWW-Authenticate header koymuyoruz -> browser popup çıkarmıyor
            response.getWriter().write("{\"error\":\"UNAUTHORIZED\",\"message\":\"Login required\"}");
        };

        http.httpBasic(basic -> basic.authenticationEntryPoint(noPopupEntryPoint));


        return http.build();
    }
}

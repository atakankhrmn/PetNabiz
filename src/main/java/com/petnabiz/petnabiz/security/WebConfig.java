package com.petnabiz.petnabiz.security; // Kendi paket yoluna göre düzenle

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Tarayıcıdan http://localhost:8080/uploads/pets/dosya.jpg
        // şeklinde gelen istekleri fiziksel diskteki uploads/ klasörüne yönlendirir.
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
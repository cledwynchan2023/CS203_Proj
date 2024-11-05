package com.codewithcled.fullstack_backend_proj1.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class ApplicationConfig {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);

    private static final String[] PUBLIC_URLS = {"/login/**", "/auth/**"};
    private static final String ADMIN_URL = "/admin/**";
    private static final String[] USER_URLS = {"/user/**", "/m/**"};

    /**
     * Configures the security filter chain.
     * This configuration sets the session creation policy to STATELESS, allows public access to login and auth URLs,
     * restricts access to admin URLs to the ADMIN role, restricts access to user URLs to the USER and ADMIN roles,
     * adds a JWT token validation filter before the BasicAuthenticationFilter, sets a custom authentication entry point,
     * disables CSRF protection, and enables CORS with custom configuration.
     *
     * @param http the HttpSecurity to modify
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs
     */
    @SuppressWarnings("deprecation")
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring security filter chain");

        http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeRequests(authorize -> authorize
                .requestMatchers(PUBLIC_URLS).permitAll() // Allow public access to login and auth URLs
                .requestMatchers(ADMIN_URL).hasRole("ADMIN") // Restrict access to admin URLs to ADMIN role
                .requestMatchers(USER_URLS).hasAnyRole("USER", "ADMIN") // Restrict access to user URLs to USER and ADMIN roles
                .anyRequest().permitAll() // Allow all other requests
            )
            .addFilterBefore(new JwtTokenValidator(), BasicAuthenticationFilter.class) // Add JWT token validation filter
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint()) // Custom entry point for authentication exceptions
            )
            .csrf(csrf -> csrf.disable()) // Disable CSRF protection
            .cors(cors -> cors.configurationSource(corsConfigurationSource())); // Enable CORS with custom configuration

        return http.build();
    }

    /**
     * Configures CORS settings.
     * This configuration allows all origins, all HTTP methods, and all headers.
     *
     * @return the configured CorsConfigurationSource
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("*")); // Allow all origins
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Allow specified HTTP methods
        configuration.setAllowedHeaders(Collections.singletonList("*")); // Allow all headers
        return request -> configuration;
    }

    /**
     * Creates a BCrypt password encoder.
     *
     * @return the BCrypt password encoder
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Value("${rest.base.url}")
    private String baseUrl;

    /**
     * Creates a RestTemplate with a custom URI template handler.
     *
     * @return the RestTemplate
     */
    @Bean
    RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(baseUrl));
        return restTemplate;
    }

}
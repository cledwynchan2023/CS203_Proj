package com.codewithcled.fullstack_backend_proj1.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Called when an unauthenticated user tries to access a protected resource.
     * Redirects the user to the login page.
     * 
     * @param request       the request being processed
     * @param response      the response to be sent
     * @param authException the exception that caused the authentication failure
     * @throws IOException      if an I/O error occurs
     * @throws ServletException if a servlet error occurs
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        response.sendRedirect("/"); // Redirect to the login page
    }
}

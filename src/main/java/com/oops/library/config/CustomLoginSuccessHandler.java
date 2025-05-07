package com.oops.library.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		boolean isLibrarian = authentication.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_LIBRARIAN"));

        if (isLibrarian) {
            response.sendRedirect("/facade");
        } else {
            response.sendRedirect("/home");
        }

	}

}

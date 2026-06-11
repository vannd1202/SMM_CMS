package com.example.smm_cms.config;

import com.example.smm_cms.common.Role;
import com.example.smm_cms.entity.User;
import com.example.smm_cms.repository.UserRepository;
import com.example.smm_cms.security.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Value("${app.oauth2.redirect-success}")
    private String redirectSuccessUrl;

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String googleId = oAuth2User.getAttribute("sub");

        if (email == null || googleId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Google account info is missing");
            return;
        }

        User user = userRepository.findByUsername(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setUsername(email);
                    newUser.setRole(Role.ROLE_USER);
                    newUser.setPassword(null);
                    return userRepository.save(newUser);
                });

         String token = jwtTokenProvider.generateToken(user.getUsername());
         String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);

         response.sendRedirect(redirectSuccessUrl + "?token=" + encodedToken);
    }
}

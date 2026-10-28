
package com.trackNest.trackNest_App.common.security;

import com.trackNest.trackNest_App.auth.dto.LoginResponseDto;
import com.trackNest.trackNest_App.auth.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTTokenGenerator jwtTokenGenerator;
    private final UserService userService;

    public JWTAuthenticationFilter(
            JWTTokenGenerator jwtTokenGenerator,
            UserService userService) {
        this.jwtTokenGenerator = jwtTokenGenerator;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String token = null;

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwttoken".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }


        if (token != null
                && !token.isBlank()
                && SecurityContextHolder.getContext()
                .getAuthentication() == null) {

            if (jwtTokenGenerator.validateToken(token)) {

                String userEmail =
                        jwtTokenGenerator.getUsernameFromToken(token);
                LoginResponseDto loginResponseDto=userService.findUserByEmail(userEmail);
                UUID userId = loginResponseDto == null ? null : loginResponseDto.getUserId();
                if (userId != null) {

                    var authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userId,
                                    null,
                                    Collections.emptyList()
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);
                }
            }
        }


        filterChain.doFilter(request, response);
    }
}

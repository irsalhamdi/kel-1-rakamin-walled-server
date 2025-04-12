package com.odp.walled.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.odp.walled.dto.APIResponse;
import com.odp.walled.model.User;
import com.odp.walled.repository.UserRepository;

import java.io.IOException;
import java.security.Key;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final String SECRET_KEY = "YourSecretKeyYourSecretKeyYourSecretKey";
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                Optional<User> userOpt = userRepository.findByEmail(username);

                if (userOpt.isPresent()) {
                    User user = userOpt.get();

                    if (token.equals(user.getToken())) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                username, null, List.of());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    } else {
                        sendErrorResponse(response, "Token mismatch or already logged out",
                                HttpServletResponse.SC_UNAUTHORIZED);
                        return;
                    }
                }
            }

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            sendErrorResponse(response, "JWT expired", HttpServletResponse.SC_UNAUTHORIZED);
        } catch (JwtException | IllegalArgumentException e) {
            sendErrorResponse(response, "Invalid JWT token", HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private void sendErrorResponse(HttpServletResponse response, String message, int statusCode) throws IOException {
        response.setContentType("application/json");
        response.setStatus(statusCode);

        APIResponse<Object> apiResponse = new APIResponse<>("error", message, null);
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}

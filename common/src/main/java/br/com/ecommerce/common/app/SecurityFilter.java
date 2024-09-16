package br.com.ecommerce.common.app;

import java.io.IOException;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request, 
        @NonNull HttpServletResponse response, 
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        var token = this.getToken(request);
        if (token != null) {
            Long userId = this.getUserId(request);
            String userUsername = this.getUserUsername(request);
            String userRole = this.getUserRoles(request);
            System.out.println("USER DATA: " + userId + userUsername + userRole);
            UserDetails user = new UserDetailsImpl(userId, userUsername, userRole);

            var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Authorization"))
            .map(t -> t.replace("Bearer ", ""))
            .orElse(null);
    }

    private Long getUserId(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("X-auth-user-id"))
            .map(i -> Long.valueOf(i))
            .orElseThrow(() -> new RuntimeException("Missing header 'X-auth-user-id'"));
    }

    private String getUserUsername(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("X-auth-user-username"))
            .orElseThrow(() -> new RuntimeException("Missing header 'X-auth-user-username'"));
    }

    private String getUserRoles(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("X-auth-user-role"))
            .orElseThrow(() -> new RuntimeException("Missing header 'X-auth-user-role'"));
    }
}
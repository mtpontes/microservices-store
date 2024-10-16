package br.com.ecommerce.common.filter;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import br.com.ecommerce.common.exception.CustomForbiddenException;
import br.com.ecommerce.common.exception.InvalidTokenException;
import br.com.ecommerce.common.jwt.TokenDecoderService;
import br.com.ecommerce.common.user.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * A Spring Security filter that validates JWT tokens and sets up the security context.
 */
@Component
public class SecurityFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(SecurityFilter.class);
    private final TokenDecoderService tokenService;

    /**
     * Creates a new instance of `SecurityFilter`.
     *
     * @param tokenService the token service used to validate JWT tokens
     */
    public SecurityFilter(TokenDecoderService tokenService) {
        this.tokenService = tokenService;
    }

    /**
     * Validates the JWT token from the request header and sets up the security context.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @param filterChain the filter chain
     */
    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request, 
        @NonNull HttpServletResponse response, 
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String token = this.getToken(request);
        if (token != null) {
            DecodedJWT jwtDecoded = this.tokenService.validateToken(token);
            System.out.println("JWT: " + jwtDecoded.getClaims());
            UserDetails user = this.createUserRepresentation(jwtDecoded);
            log.debug("USER DATA: {}", user);

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

    private String getUserId(DecodedJWT jwtDecoded) {
        return Optional.ofNullable(jwtDecoded.getClaim("userId"))
            .map(Claim::asString)
            .orElseThrow(() -> new InvalidTokenException("User ID not found in JWT claim"));
    }

    private String getUserUsername(DecodedJWT jwtDecoded) {
        return Optional.ofNullable(jwtDecoded.getSubject())
            .orElseThrow(() -> new InvalidTokenException("User username not found in JWT claim"));
    }

    private String getUserRoles(DecodedJWT jwtDecoded) {
        return Optional.ofNullable(jwtDecoded.getClaim("roles"))
            .map(Claim::asString)
            .orElseThrow(() -> new CustomForbiddenException("User roles not found in JWT claim"));
    }

    private UserDetails createUserRepresentation(DecodedJWT jwt) {
        String id = this.getUserId(jwt);
        String username = this.getUserUsername(jwt);
        String roles = this.getUserRoles(jwt);
        log.debug("ATTRIBUTES RETRIEVED FROM JWT: {}, {}, {}", id, username, roles);

        return new UserDetailsImpl(id, username, roles);
    }
}
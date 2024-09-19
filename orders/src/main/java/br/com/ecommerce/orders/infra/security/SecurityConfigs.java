package br.com.ecommerce.orders.infra.security;

import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.ecommerce.common.app.SecurityFilter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfigs {

    @Value("${api.security.ips.allowed}") 
    private Set<String> alloweInternaldIps;


    @Bean
    public OncePerRequestFilter securityFilter() {
        return new SecurityFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/admin/orders/**").hasRole("ADMIN")
                .requestMatchers("/orders/**").hasRole("CLIENT")
                .requestMatchers("/internal/orders").access((authentication, requestContext) -> {
                    String remote = requestContext.getRequest().getRemoteAddr();
                    boolean isMatch = alloweInternaldIps.stream()
                        .peek(ip -> log.debug("Allowed IP: {} | Client IP: {}", ip, remote))
                        .anyMatch(ip -> ip.equalsIgnoreCase(remote));
                    return new AuthorizationDecision(isMatch);
                })
            )
            .addFilterBefore(this.securityFilter(), UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
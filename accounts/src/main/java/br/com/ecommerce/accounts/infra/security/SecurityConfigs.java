package br.com.ecommerce.accounts.infra.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.ecommerce.common.filter.SecurityFilter;
import br.com.ecommerce.common.jwt.TokenDecoderService;

@Configuration
@EnableWebSecurity
public class SecurityConfigs {

    @Value("${api.security.token.secret}")
    private String secret;


    @Bean
    public TokenDecoderService tokenService() {
        return new TokenDecoderService(secret);
    }

    @Bean
    public OncePerRequestFilter securityFilter() {
        return new SecurityFilter(this.tokenService());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.POST, "/admin/account/employee/**").hasAnyRole("ADMIN", "EMPLOYEE")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/client/account").permitAll()
                .requestMatchers("/client/account/**").hasRole("CLIENT")
                .requestMatchers("/account").authenticated()
                .requestMatchers(HttpMethod.POST, "/auth").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(this.securityFilter(), UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
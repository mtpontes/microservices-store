package br.com.ecommerce.gateway.filter;

import java.util.Optional;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@AllArgsConstructor
public class GlobalFilterImpl implements GlobalFilter {

    private final WebClient.Builder webClienBuilder;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return Optional.ofNullable(exchange.getRequest().getHeaders().get("Authorization"))
            .filter(headers -> !headers.isEmpty())
            .map(headers -> headers.get(0).replace("Bearer ", ""))
            .stream().peek(token -> log.debug("TOKEN VALUE: " + token)).findFirst()
            .map(userKey -> webClienBuilder.build()
                .get()
                .uri("lb://auth-ms/auth")
                .header("Authorization", userKey)
                .retrieve()
                .bodyToMono(UserDTO.class)
                .doOnNext(body -> log.debug("USER DATA: " + body))
                .flatMap(userData -> {
                    ServerWebExchange newEchange = exchange.mutate()
                        .request(exchange.getRequest().mutate()
                            .headers(header -> header.add("X-auth-user-id", userData.id()))
                            .headers(header -> header.add("X-auth-user-username", userData.username()))
                            .headers(header -> header.add("X-auth-user-role", userData.role()))
                            .build())
                        .build();
                    return chain.filter(newEchange);
                })
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.error("Authentication error: " + ex.getResponseBodyAsString(), ex);
                    
                    exchange.getResponse().setStatusCode(ex.getStatusCode());
                    DataBuffer buffer = exchange.getResponse().bufferFactory()
                        .wrap(ex.getResponseBodyAsString().getBytes());
                    return exchange.getResponse().writeWith(Mono.just(buffer));
                }))
            .orElse(chain.filter(exchange));
    }

    private record UserDTO(String id, String username, String role){};
}
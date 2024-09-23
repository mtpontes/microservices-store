package br.com.ecommerce.gateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AddHeaderGlobalFilterImpl implements GlobalFilter {

    @Value("${api.security.gateway.name}")
    private String gatewayName;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerWebExchange newEchange = exchange.mutate()
            .request(exchange.getRequest().mutate()
                .headers(header -> header.add("X-Forwarded-By", gatewayName))
                .build())
            .build();
        return chain.filter(newEchange);
    }
}
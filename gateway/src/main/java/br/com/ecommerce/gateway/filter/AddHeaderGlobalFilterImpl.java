package br.com.ecommerce.gateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Global filter that intercepts all requests passing through the Gateway.
 * 
 * <p>
 * The purpose of this class is to add the `"X-Forwarded-By"` header to all requests that pass through the Gateway,
 * allowing subsequent microservices to identify that the request was forwarded by the company's internal Gateway
 * application. This is particularly useful for endpoints that need to block external calls, such as those starting
 * with "/internal", ensuring that only internal calls are accepted.
 * </p>
 *
 * <p>
 * The `"X-Forwarded-By"` header is dynamically set to the gateway value, defined by the property 
 * `api.security.gateway.name` in the configuration file. This approach helps microservices differentiate
 * internal calls from external ones and apply appropriate security policies, such as blocking external requests
 * to certain endpoints.
 * </p>
 */
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
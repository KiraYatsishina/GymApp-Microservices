package micro.apigateway.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Component
public class AuthenticationFilter implements GatewayFilter {

    @Autowired
    private RouterValidator routerValidator;

    @Autowired
    private WebClient.Builder webClientBuilder;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (routerValidator.isSecured.test(request)) {
            if (authMissing(request)) {
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            final String jwt = request.getHeaders().getFirst("Authorization");
            if (jwt == null) {
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            return validateToken(jwt)
                    .flatMap(isValid -> {
                        if (!isValid) {
                            return onError(exchange, HttpStatus.UNAUTHORIZED);
                        }
                        return chain.filter(exchange);
                    });
            }
         return chain.filter(exchange);
    }

    private Mono<Boolean> validateToken(String token) {
        return webClientBuilder.build()
                .get()
                .uri("http://AUTH-SERVER/auth/validate")
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(Boolean.class);
    }
    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    private boolean authMissing(ServerHttpRequest request) {
        return !request.getHeaders().containsKey("Authorization");
    }
}
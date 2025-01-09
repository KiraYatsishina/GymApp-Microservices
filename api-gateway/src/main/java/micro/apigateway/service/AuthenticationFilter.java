package micro.apigateway.service;

import micro.apigateway.Util.JwtUtils;
import micro.apigateway.Util.LogoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AuthenticationFilter implements GatewayFilter {

    @Autowired
    private RouterValidator validator;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private LogoutService invalidTokenService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (validator.isSecured.test(request)) {
            if (authMissing(request)) return onError(exchange, HttpStatus.UNAUTHORIZED);

            final String token = request.getHeaders().getOrEmpty("Authorization").get(0).substring(7);

            if (invalidTokenService.isTokenInvalid(token) || jwtUtils.isExpired(token)) return onError(exchange, HttpStatus.UNAUTHORIZED);

            List<String> roles;
            try {
               roles = jwtUtils.getRoles(token);
            }catch (Exception e) {
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }
            String path = request.getURI().getPath();

            if (path.startsWith("/workload/trainer") && !roles.contains("ROLE_TRAINER"))
                return onError(exchange, HttpStatus.FORBIDDEN);
            if (path.startsWith("/gym-app/trainer") && !roles.contains("ROLE_TRAINER"))
                return onError(exchange, HttpStatus.FORBIDDEN);
            if (path.startsWith("/gym-app/trainee") && !roles.contains("ROLE_TRAINEE"))
                return onError(exchange, HttpStatus.FORBIDDEN);

            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("Authorization", "Bearer " + token)
                    .build();

            ServerWebExchange modifiedExchange = exchange.mutate().request(modifiedRequest).build();
            return chain.filter(modifiedExchange);
        }
        return chain.filter(exchange);
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
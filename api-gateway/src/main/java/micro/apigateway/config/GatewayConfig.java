package micro.apigateway.config;

import micro.apigateway.service.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Autowired
    private AuthenticationFilter filter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("trainers-workload", r -> r.path("/workload/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://trainers-workload"))
                .route("gym-app", r -> r.path("/gym-app/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://gym-app"))
                .build();

    }
}

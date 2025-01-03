package micro.apigateway.service;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouterValidator {

    public static final List<String> openApiEndpoints = List.of(
            "/gym-app/token",
            "/gym-app/trainer/signup",
            "/gym-app/trainee/signup",
            "/workload/h2-ui/**",
            "/workload/trainer/update-workload",
            "/workload/trainer/monthly-workload",
            "/eureka"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));
}
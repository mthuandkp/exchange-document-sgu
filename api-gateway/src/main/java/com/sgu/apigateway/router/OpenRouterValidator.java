package com.sgu.apigateway.router;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class OpenRouterValidator {

    public static final List<String> openApiEndpoints= List.of(
            "/api/v1/login",
            "/api/v1/refresh-token",
            "/api/v1/logout",
            "/api/v1/register",
            "/api/v1/profile",
            "/api/v1/categories",
            "/api/v1/posts",
            "/api/v1/accounts/otp",
            "/api/v1/accounts/active",
            "/api/v1/accounts/forget-password",
            "/api/v1/sliders"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));

}

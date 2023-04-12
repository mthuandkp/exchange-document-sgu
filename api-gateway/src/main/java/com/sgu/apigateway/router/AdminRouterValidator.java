package com.sgu.apigateway.router;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class AdminRouterValidator {

    public static final List<String> adminEndpoints= List.of(
            "/api/v1/accounts",
            "/api/v1/admin/category",
            "/api/v1/admin/posts",
            "/api/v1/accounts/block-account",
            "/api/v1/accounts/unblock-account",
            "/api/v1/admin/sliders"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> adminEndpoints
                    .stream()
                    .anyMatch(uri -> request.getURI().getPath().contains(uri));

}

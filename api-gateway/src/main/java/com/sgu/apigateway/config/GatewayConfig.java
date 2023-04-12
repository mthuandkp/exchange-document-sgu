package com.sgu.apigateway.config;

import com.sgu.apigateway.filter.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
@EnableDiscoveryClient
public class GatewayConfig {

    @Autowired
    AuthenticationFilter filter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {

//        return builder.routes()
//                .route("auth-service", r -> r.path(
//                                "/api/v1/login",
//                                "/api/v1/refresh-token",
//                                "/api/v1/logout"
//                        )
//                        .uri("http://AUTH-SERVICE:8081"))
//                .route("user-service", r -> r.path(
//                                "/api/v1/register",
//                                "/api/v1/profile/**",
//                                "/api/v1/user/**",
//                                "/api/v1/accounts/**"
//                        )
//                        .filters(f -> f.filter(filter))
//                        .uri("http://USER-SERVICE:8082"))
//                .route("category-service", r -> r.path(
//                                "/api/v1/categories/**",
//                                "/api/v1/admin/category/**"
//                        )
//                        .filters(f -> f.filter(filter))
//                        .uri("http://CATEGORY-SERVICE:8083"))
//                .route("posts-service", r -> r.path(
//                                "/api/v1/posts/**",
//                                "/api/v1/admin/posts/**",
//                                "/api/v1/auth/posts/**"

//                        )
//                        .filters(f -> f.filter(filter))
//                        .uri("http://POSTS-SERVICE:8084"))
//        .route("order-service", r -> r.path(
//                        "/api/v1/orders/**",
//                        "/api/v1/admin/orders/**"
//                )
//                .filters(f -> f.filter(filter))
//                .uri("http://ORDER-SERVICE:8086"))
//                .build();
        return builder.routes()
                .route("auth-service", r -> r.path(
                                "/api/v1/login",
                                "/api/v1/refresh-token",
                                "/api/v1/logout"
                        )
                        .uri("http://localhost:8081"))
                .route("user-service", r -> r.path(
                                "/api/v1/register",
                                "/api/v1/profile/**",
                                "/api/v1/user/**",
                                "/api/v1/accounts/**"
                        )
                        .filters(f -> f.filter(filter))
                        .uri("http://localhost:8082"))
                .route("category-service", r -> r.path(
                                "/api/v1/categories/**",
                                "/api/v1/admin/category/**"
                        )
                        .filters(f -> f.filter(filter))
                        .uri("http://localhost:8083"))
                .route("posts-service", r -> r.path(
                        "/api/v1/posts/**",
                                "/api/v1/admin/posts/**",
                                "/api/v1/auth/posts/**"
                        )
                        .filters(f -> f.filter(filter))
                        .uri("http://localhost:8084"))
                .route("slider-service", r -> r.path(
                                "/api/v1/sliders/**",
                                "/api/v1/admin/sliders/**"
                        )
                        .filters(f -> f.filter(filter))
                        .uri("http://localhost:8085"))
                .route("order-service", r -> r.path(
                                "/api/v1/orders/**",
                                "/api/v1/admin/orders/**"
                        )
                        .filters(f -> f.filter(filter))
                        .uri("http://localhost:8086"))
                .build();
    }

    @Bean
    public CorsWebFilter corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>();
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

}

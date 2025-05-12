package com.SE2.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("employee_route", r -> r
                .path("/api/employee/**")
                .filters(f -> f.stripPrefix(1))
                .uri("http://localhost:8200"))
            .route("manager_route", r -> r
                .path("/api/manager/**")
                .filters(f -> f.stripPrefix(1))
                .uri("http://localhost:8201"))
            .build();
    }
}
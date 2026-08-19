package com.henrique.nookio_gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouterFunction<ServerResponse> customRoutes() {
        return route("core")
                .route(path("/core/api/v1/**", "/docs", "/docs/**", "/v3/api-docs", "/v3/api-docs/**"), http())
                .filter(lb("NOOKIO-API"))
                .add(route("analytics")
                        .route(path("/logs"), http())
                        .filter(lb("NOOKIO-ANALYTICS-API"))
                        .build())
                .build();
    }
}

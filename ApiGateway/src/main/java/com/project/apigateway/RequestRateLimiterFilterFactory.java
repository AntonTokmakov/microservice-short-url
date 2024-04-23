package com.project.apigateway;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.stereotype.Component;

@Component
public class RequestRateLimiterFilterFactory extends
        AbstractGatewayFilterFactory<RequestRateLimiterFilterFactory.Config> {

    public RequestRateLimiterFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            exchange.getAttributes().computeIfAbsent(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR,
                    k -> exchange.getRequest().getURI());
            return chain.filter(exchange);
        };
    }
    public static class Config {
    }
}

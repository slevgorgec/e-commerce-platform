package com.n11bootcamp.ecommerce.gateway.filter;

import com.n11bootcamp.ecommerce.gateway.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtTokenProvider jwtTokenProvider;

    // Method + path prefix çiftleri olarak public endpoint'ler
    private static final List<PublicRoute> PUBLIC_ROUTES = List.of(
            new PublicRoute(HttpMethod.POST, "/api/auth"),
            new PublicRoute(HttpMethod.GET,  "/api/products"),
            new PublicRoute(HttpMethod.GET,  "/api/categories"),
            new PublicRoute(HttpMethod.GET,  "/actuator")
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (isPublicRoute(request)) {
            // Client'ın gönderdiği X-User-Id/X-User-Role header'larını her durumda sil
            ServerHttpRequest stripped = stripTrustedHeaders(request).build();
            return chain.filter(exchange.mutate().request(stripped).build());
        }

        String token = extractBearerToken(request);
        if (token == null) {
            return rejectUnauthorized(exchange, "Missing Authorization header");
        }

        try {
            Claims claims = jwtTokenProvider.validateAndExtractClaims(token);
            String userId = claims.getSubject();
            String role   = claims.get("role", String.class);

            // Önce client header'larını sil, ardından JWT'den doğrulanmış değerleri ekle
            ServerHttpRequest mutatedRequest = stripTrustedHeaders(request)
                    .header("X-User-Id",   userId)
                    .header("X-User-Role", role)
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (Exception e) {
            log.warn("JWT validation failed for path {}: {}", request.getPath(), e.getMessage());
            return rejectUnauthorized(exchange, "Invalid or expired token");
        }
    }

    private ServerHttpRequest.Builder stripTrustedHeaders(ServerHttpRequest request) {
        return request.mutate()
                .headers(headers -> {
                    headers.remove("X-User-Id");
                    headers.remove("X-User-Role");
                });
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private boolean isPublicRoute(ServerHttpRequest request) {
        String path   = request.getPath().value();
        HttpMethod method = request.getMethod();

        return PUBLIC_ROUTES.stream()
                .anyMatch(route -> route.method().equals(method)
                        && path.startsWith(route.pathPrefix()));
    }

    private String extractBearerToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private Mono<Void> rejectUnauthorized(ServerWebExchange exchange, String reason) {
        log.debug("Rejecting request to {}: {}", exchange.getRequest().getPath(), reason);
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    private record PublicRoute(HttpMethod method, String pathPrefix) {}
}

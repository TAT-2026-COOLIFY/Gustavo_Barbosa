package com.ironfit.api.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Limita tentativas de login por IP (anti brute-force). Bucket em memória é suficiente
 * para uma instância única do backend; um deployment multi-instância trocaria isso por
 * um contador compartilhado (ex.: Redis) sem alterar o resto da aplicação.
 */
@Component
public class LoginRateLimitFilter extends OncePerRequestFilter {

    private final LoginRateLimitProperties properties;
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    public LoginRateLimitFilter(LoginRateLimitProperties properties) {
        this.properties = properties;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        if (isLoginRequest(request) && !resolveBucket(request).tryConsume(1)) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter()
                    .write("{\"message\":\"Muitas tentativas de login. Tente novamente em alguns minutos.\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isLoginRequest(HttpServletRequest request) {
        return "POST".equalsIgnoreCase(request.getMethod()) && "/api/auth/login".equals(request.getRequestURI());
    }

    private Bucket resolveBucket(HttpServletRequest request) {
        return buckets.computeIfAbsent(request.getRemoteAddr(), ip -> Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(properties.getCapacity())
                        .refillIntervally(properties.getCapacity(), Duration.ofMinutes(properties.getRefillMinutes()))
                        .build())
                .build());
    }
}

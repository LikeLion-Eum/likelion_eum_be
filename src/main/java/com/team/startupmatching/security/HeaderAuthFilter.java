package com.team.startupmatching.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 해커톤용 라이트 인증 필터.
 * - 헤더 "X-User-Id" 로 현재 사용자 식별
 * - (선택) demo.api-key 가 설정되어 있으면 "X-Demo-Key" 도 일치해야 인증됨
 * - 성공 시 SecurityContext 에 CurrentUser 주입 → 컨트롤러에서 @AuthenticationPrincipal 사용 가능
 */
@Component
public class HeaderAuthFilter extends OncePerRequestFilter {

    @Value("${demo.api-key:}")
    private String demoApiKey; // 비어있으면 데모 키 검증 생략

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 이미 인증되어 있으면 패스
        Authentication existing = SecurityContextHolder.getContext().getAuthentication();
        if (existing != null && existing.isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }

        // 1) X-User-Id 헤더 읽기 (없으면 무시하고 다음 필터로)
        String userIdHeader = request.getHeader("X-User-Id");
        if (userIdHeader == null || userIdHeader.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2) 데모 키가 설정돼 있으면 X-Demo-Key 확인 (불일치 시 인증 안 함)
        if (demoApiKey != null && !demoApiKey.isBlank()) {
            String provided = request.getHeader("X-Demo-Key");
            if (!demoApiKey.equals(provided)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        // 3) userId 파싱 후 SecurityContext 설정
        try {
            Long userId = Long.valueOf(userIdHeader.trim());
            CurrentUser principal = CurrentUser.ofId(userId);

            List<SimpleGrantedAuthority> authorities = principal.getRoles().stream()
                    .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(principal, null, authorities);
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);

        } catch (NumberFormatException ignore) {
            // 잘못된 X-User-Id면 인증 생략하고 계속 진행
        }

        filterChain.doFilter(request, response);
    }
}

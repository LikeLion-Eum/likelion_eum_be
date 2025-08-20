// com.team.startupmatching.config.SecurityConfig
package com.team.startupmatching.config;

import com.team.startupmatching.security.HeaderAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, HeaderAuthFilter headerAuthFilter) throws Exception {
        http
                // API 서버: 세션 안 씀
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // JSON API라 CSRF 끔
                .csrf(csrf -> csrf.disable())
                // spring.mvc.cors 설정 사용
                .cors(cors -> {})
                // 경로별 권한
                .authorizeHttpRequests(auth -> auth
                        // 정적/헬스
                        .requestMatchers("/uploads/**", "/error", "/actuator/health").permitAll()

                        // ★ 러브콜 관련: 인증 필수
                        .requestMatchers(
                                "/api/recruitments/*/love-calls",
                                "/api/me/love-calls/**"
                        ).authenticated()

                        // 그 외는 전부 허용 (기존 기능 안 막힘)
                        .anyRequest().permitAll()
                )
                // 기본 로그인/로그아웃 비활성
                .httpBasic(b -> b.disable())
                .formLogin(f -> f.disable())
                .logout(l -> l.disable())
                // 우리의 헤더 인증 필터 장착
                .addFilterBefore(headerAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

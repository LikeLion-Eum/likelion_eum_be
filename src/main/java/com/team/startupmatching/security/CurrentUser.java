package com.team.startupmatching.security;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Security 컨텍스트에 넣어두는 "현재 사용자" 객체.
 * 컨트롤러에서 @AuthenticationPrincipal(expression = "id") 로 userId를 바로 꺼낼 수 있다.
 */
public class CurrentUser implements Serializable {
    private final Long id;
    private final String username;          // 선택
    private final List<String> roles;       // 예: ["USER"]

    public CurrentUser(Long id, String username, List<String> roles) {
        this.id = Objects.requireNonNull(id, "id");
        this.username = username;
        this.roles = roles == null ? List.of() : List.copyOf(roles);
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public List<String> getRoles() { return roles; }

    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }

    /** 해커톤용: id만 아는 경우 빠르게 생성 */
    public static CurrentUser ofId(Long id) {
        return new CurrentUser(id, null, List.of("USER"));
    }
}

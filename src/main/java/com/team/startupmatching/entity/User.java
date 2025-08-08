package com.team.startupmatching.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Entity
@Setter
@Table(name = "`user`")
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK

    @Column(nullable = false)
    private String name; // 이름 (필수)

    @Column(nullable = false)
    private String email; // 이메일 (필수)

    @Column(columnDefinition = "TEXT")
    private String introduction; // 자기소개 (길이 제한 없음, 선택)

    @Column
    private String skills; // 기술 스택 (선택)

    @Column
    private String career; // 경력 (선택)

    @Column(nullable = false)
    private String location; // 지역 (필수)

    @Column
    private String resumeUrl; // 이력서 링크 (선택)
}
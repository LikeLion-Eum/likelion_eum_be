package com.team.startupmatching.entity;

import jakarta.persistence.*;
import lombok.*;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 기본 생성자
@AllArgsConstructor
@Builder
@Entity
public class SharedOffice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private Long roomCount;

    @Column(nullable = false)
    private Long size;

    @Column(nullable = false)
    private Long maxCount;

    @Column
    private String description;
}

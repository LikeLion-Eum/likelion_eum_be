package com.team.startupmatching.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Space {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;       // 공간 이름
    private String location;   // 위치
    private Long size;         // 면적 (null 가능)
    private Long price;        // 가격 (null 가능)
    private String type;       // 공간 형태 (예: 공유오피스, 카페 등)

}

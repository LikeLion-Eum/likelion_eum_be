package com.team.startupmatching.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecruitmentRequest {

    private String title;          // 모집글 제목 (예: "스터디 팀원 모집")
    private String content;        // 모집글 본문 내용 (모집 목적, 시간, 조건 등)
    private String writer;         // 작성자 이름 또는 닉네임
    private String contact;        // 연락처 (전화번호 또는 이메일)
    private String spaceName;      // 사용자가 모집하려는 공간 이름 (예: "카페 A")
    private String spaceLocation;  // 공간 위치 (예: "충남 아산시 신창면")


}
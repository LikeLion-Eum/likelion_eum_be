USE startup;

-- 외래키 잠깐 해제
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE shared_office;
TRUNCATE TABLE incubation_center;
TRUNCATE TABLE recruitment;
TRUNCATE TABLE `user`;  -- ✅ user는 예약어라 backtick 필요

SET FOREIGN_KEY_CHECKS = 1;

-- SharedOffice 5개
INSERT INTO shared_office (name, description, room_count, size, location, max_count) VALUES
('공유오피스 A', '아산시 중심부에 위치한 최신 공유오피스', 10, 200, '충남 아산시 중앙로 123', 50),
('공유오피스 B', '조용한 분위기의 스터디 및 사무공간', 5, 120, '충남 아산시 배방로 45', 20),
('공유오피스 C', '회의실과 카페가 함께 있는 오피스', 8, 150, '충남 아산시 온천대로 200', 40),
('공유오피스 D', '대형 주차장을 보유한 공유 사무실', 12, 300, '충남 아산시 신창면 88', 60),
('공유오피스 E', '창업팀을 위한 맞춤형 사무공간', 7, 180, '충남 아산시 배방읍 광로 55', 35);

-- IncubationCenter 5개
INSERT INTO incubation_center (description, region, site_url) VALUES
('창업 지원 및 멘토링 제공', '충남 아산시', 'http://incubation-a.com'),
('기술 창업 전문 센터', '충남 아산시', 'http://incubation-b.com'),
('청년 창업 활성화를 위한 공간', '충남 아산시', 'http://incubation-c.com'),
('정부 지원 창업 보육 센터', '충남 아산시', 'http://incubation-d.com'),
('지역 특화 스타트업 지원 센터', '충남 아산시', 'http://incubation-e.com');

-- User 5개 (예약어 backtick)
INSERT INTO `user` (name, email, introduction, skills, career, location, resume_url) VALUES
('김철수', 'chulsoo@example.com', '웹 개발자 지망생', 'Java, Spring Boot', '신입', '충남 아산시', 'http://resume-chulsoo.com'),
('이영희', 'younghee@example.com', 'UI/UX 디자이너', 'Figma, Adobe XD', '3년', '충남 아산시', 'http://resume-younghee.com'),
('박민수', 'minsoo@example.com', '데이터 분석가', 'Python, SQL', '2년', '충남 아산시', 'http://resume-minsoo.com'),
('최지현', 'jihyun@example.com', 'AI 모델 개발자', 'Python, TensorFlow', '4년', '충남 아산시', 'http://resume-jihyun.com'),
('정우성', 'woosung@example.com', '백엔드 개발자', 'Node.js, MySQL', '신입', '충남 아산시', 'http://resume-woosung.com');


INSERT INTO recruitment
(title, location, `position`, skills, career, recruit_count, content, is_closed, created_at, user_id, target_space_type)
VALUES
('신창 카페 앱 프론트엔드 모집', '충남 아산시 신창', 'Frontend', 'React, TypeScript', '무관', 2,
 '주 2회 대면, 반응형 컴포넌트 개발 및 간단한 상태관리. 협업 툴은 GitHub 사용.', 0, NOW() - INTERVAL 10 DAY, 1, 'SHARED_OFFICE'),

('프로토타입 개선 UI/UX 디자이너 구인', '충남 아산시', 'Designer', 'Figma, Adobe XD', '3년 이상', 1,
 '사용자 테스트 기반 와이어프레임/프로토타입 개선. 멘토링 프로그램 연계 희망.', 0, NOW() - INTERVAL 7 DAY, 2, 'INCUBATION_CENTER'),

('데이터 대시보드 제작 팀원 모집', '충남 아산시 중앙로', 'Data Analyst', 'Python, SQL, Pandas', '2년 이상', 2,
 '서비스 로그 기반 KPI 대시보드 제작. 주 1회 회의, 간단한 ETL 스크립트 작성.', 0, NOW() - INTERVAL 3 DAY, 3, 'SHARED_OFFICE'),

('AI 모델 튜닝 사이드 프로젝트', '충남 아산시', 'ML Engineer', 'TensorFlow, Scikit-learn', '4년 이상', 3,
 '수요 예측 모델 고도화 및 실험 관리. 정부/학교 지원 프로그램과 연계 희망.', 1, NOW() - INTERVAL 1 DAY, 4, 'INCUBATION_CENTER'),

('백엔드(Spring) API 개발자 모집', '충남 아산시 배방', 'Backend', 'Spring Boot, JPA, MySQL', '신입/경력', 2,
 '공간 검색/추천 API 개발, 코드 리뷰 및 단위 테스트 작성 권장.', 0, NOW() - INTERVAL 30 MINUTE, 5, 'SHARED_OFFICE');
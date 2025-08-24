package com.team.startupmatching.support;

import java.util.*;

public final class LocationKeywordUtil {
    private LocationKeywordUtil(){}

    private static final Map<String, List<String>> SIDO_ALIAS = Map.ofEntries(
            Map.entry("서울", List.of("서울특별시","서울시")),
            Map.entry("부산", List.of("부산광역시","부산시")),
            Map.entry("대구", List.of("대구광역시","대구시")),
            Map.entry("인천", List.of("인천광역시","인천시")),
            Map.entry("광주", List.of("광주광역시","광주시")),
            Map.entry("대전", List.of("대전광역시","대전시")),
            Map.entry("울산", List.of("울산광역시","울산시")),
            Map.entry("세종", List.of("세종특별자치시","세종시","세종")),
            Map.entry("경기", List.of("경기도")),
            Map.entry("강원", List.of("강원도")),
            Map.entry("충북", List.of("충청북도")),
            Map.entry("충남", List.of("충청남도")),
            Map.entry("전북", List.of("전라북도")),
            Map.entry("전남", List.of("전라남도")),
            Map.entry("경북", List.of("경상북도")),
            Map.entry("경남", List.of("경상남도")),
            Map.entry("제주", List.of("제주특별자치도","제주도","제주"))
    );

    private static String stripAdminSuffix(String s){
        String[] suf = {"특별자치도","광역시","특별시","자치시","자치구","자치군","도","시","군","구","읍","면","동","리"};
        for (String x : suf) {
            if (s.endsWith(x)) return s.substring(0, s.length()-x.length());
        }
        return s;
    }

    /** "전남 나주시" -> [전남, 전라남도, 나주시, 나주] 식으로 확장 */
    public static List<String> buildKeywords(String input){
        if (input == null) return List.of();
        String norm = input.trim().replaceAll("\\s+"," ");
        if (norm.isEmpty()) return List.of();

        LinkedHashSet<String> out = new LinkedHashSet<>();
        for (String tk : norm.split("[\\s,]+")) {
            if (tk.isBlank()) continue;

            out.add(tk); // 원형
            String stripped = stripAdminSuffix(tk);
            out.add(stripped); // 접미사 제거

            // 시·도 약칭 -> 정식명
            out.addAll(SIDO_ALIAS.getOrDefault(stripped, List.of()));

            // 정식명 -> 약칭 (역방향)
            SIDO_ALIAS.forEach((shortName, fulls) -> {
                if (fulls.contains(tk)) out.add(shortName);
            });
        }

        // 노이즈 제거
        out.removeIf(s -> s.length() <= 1);
        return List.copyOf(out);
    }
}

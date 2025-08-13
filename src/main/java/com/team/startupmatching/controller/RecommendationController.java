package com.team.startupmatching.controller;

import com.team.startupmatching.ai.client.AiClient;
import com.team.startupmatching.ai.dto.RecommendRequest;
import com.team.startupmatching.ai.dto.UserRecommendationItem;
import com.team.startupmatching.ai.dto.IncubationCenterRecommendationItem;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final AiClient aiClient;

    /** 유저 추천 */
    @PostMapping("/users")
    public ResponseEntity<List<UserRecommendationItem>> recommendUsers(@RequestBody RecommendRequest req) {
        var items = aiClient.recommendUsers(req);
        return ResponseEntity.ok(items);
    }

    /** 지원사업(IncubationCenter) 추천 */
    @PostMapping("/incubation-centers")
    public ResponseEntity<List<IncubationCenterRecommendationItem>> recommendIncubationCenters(
            @RequestBody RecommendRequest req
    ) {
        var items = aiClient.recommendIncubationCenters(req);
        return ResponseEntity.ok(items);
    }
}

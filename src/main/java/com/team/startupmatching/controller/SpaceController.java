package com.team.startupmatching.controller;

import com.team.startupmatching.dto.SpaceResponse;
import com.team.startupmatching.dto.SpaceSearchRequest;
import com.team.startupmatching.entity.Space;
import com.team.startupmatching.service.SpaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SpaceController {

    private final SpaceService spaceService;

    @GetMapping("/api/space/search")
    public List<SpaceResponse> searchSpaces(@ModelAttribute SpaceSearchRequest request) {
        return spaceService.searchSpaces(request);
    }
}


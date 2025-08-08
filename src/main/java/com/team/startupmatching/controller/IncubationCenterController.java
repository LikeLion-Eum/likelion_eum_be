package com.team.startupmatching.controller;

import com.team.startupmatching.dto.IncubationCenterCreateRequest;
import com.team.startupmatching.dto.IncubationCenterResponse;
import com.team.startupmatching.service.IncubationCenterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/incubation-centers")
public class IncubationCenterController {

    private final IncubationCenterService incubationCenterService;

    @PostMapping
    public IncubationCenterResponse create(@RequestBody IncubationCenterCreateRequest request) {
        return incubationCenterService.create(request);
    }

    @GetMapping
    public List<IncubationCenterResponse> list() {
        return incubationCenterService.list();
    }
}
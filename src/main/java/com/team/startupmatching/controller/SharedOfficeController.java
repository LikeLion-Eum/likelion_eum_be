package com.team.startupmatching.controller;


import com.team.startupmatching.dto.SharedOfficeCreateRequest;
import com.team.startupmatching.dto.SharedOfficeResponse;
import com.team.startupmatching.service.SharedOfficeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shared-offices")
public class SharedOfficeController {

    private final SharedOfficeService sharedOfficeService;

    @PostMapping
    public SharedOfficeResponse create(@RequestBody SharedOfficeCreateRequest request) {
        return sharedOfficeService.create(request);
    }

    @GetMapping
    public List<SharedOfficeResponse> list() {
        return sharedOfficeService.list();
    }
}
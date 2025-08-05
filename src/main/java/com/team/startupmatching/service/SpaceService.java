package com.team.startupmatching.service;

import com.team.startupmatching.Specification.SpaceSpecification;
import com.team.startupmatching.dto.SpaceResponse;
import com.team.startupmatching.dto.SpaceSearchRequest;
import com.team.startupmatching.entity.Space;
import com.team.startupmatching.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpaceService {
    private final SpaceRepository spaceRepository;

    public List<SpaceResponse> searchSpaces(SpaceSearchRequest request) {
        Specification<Space> spec = SpaceSpecification.search(request);
        List<Space> spaces = spaceRepository.findAll(spec);

        System.out.println("Location = " + request.getLocation());
        System.out.println("TYPES = " + request.getType());

        return spaces.stream()
                .map(space -> SpaceResponse.builder()
                        .id(space.getId())
                        .name(space.getName())
                        .location(space.getLocation())
                        .size(space.getSize())
                        .price(space.getPrice())
                        .type(space.getType())
                        .build())
                .collect(Collectors.toList());
    }


}

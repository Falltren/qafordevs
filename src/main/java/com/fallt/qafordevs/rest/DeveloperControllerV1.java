package com.fallt.qafordevs.rest;

import com.fallt.qafordevs.dto.DeveloperDto;
import com.fallt.qafordevs.entity.DeveloperEntity;
import com.fallt.qafordevs.service.DeveloperService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/developers")
public class DeveloperControllerV1 {

    private final DeveloperService developerService;

    @PostMapping
    public DeveloperDto createDeveloper(@RequestBody DeveloperDto dto) {
        DeveloperEntity entity = dto.toEntity();
        DeveloperEntity createdDeveloper = developerService.saveDeveloper(entity);
        return DeveloperDto.toDto(createdDeveloper);
    }

    @PutMapping
    public DeveloperDto updateDeveloper(@RequestBody DeveloperDto dto) {
        DeveloperEntity entity = dto.toEntity();
        DeveloperEntity updatedEntity = developerService.updateDeveloper(entity);
        return DeveloperDto.toDto(updatedEntity);
    }

    @GetMapping("/{id}")
    public DeveloperDto getDeveloperById(@PathVariable("id") Integer id) {
        DeveloperEntity entity = developerService.getDeveloperById(id);
        return DeveloperDto.toDto(entity);
    }

    @GetMapping
    public List<DeveloperDto> getAllDevelopers() {
        List<DeveloperEntity> entities = developerService.getAllDevelopers();
        return DeveloperDto.toListDto(entities);
    }

    @GetMapping("/speciality/{speciality}")
    public List<DeveloperDto> getAllDevelopersBySpeciality(@PathVariable String speciality) {
        List<DeveloperEntity> entities = developerService.getAllActiveBySpeciality(speciality);
        return DeveloperDto.toListDto(entities);
    }

    @DeleteMapping("/{id}")
    public void deleteDeveloperById(@PathVariable Integer id, @RequestParam(value = "isHard", defaultValue = "false") boolean isHard) {
        if (isHard) {
            developerService.hardDeleteById(id);
        } else {
            developerService.softDeleteById(id);
        }
    }
}

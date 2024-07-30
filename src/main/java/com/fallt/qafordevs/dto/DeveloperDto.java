package com.fallt.qafordevs.dto;

import com.fallt.qafordevs.entity.DeveloperEntity;
import com.fallt.qafordevs.entity.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeveloperDto {

    private Integer id;

    private String firstName;

    private String lastName;

    private String email;

    private String speciality;

    private Status status;

    public DeveloperEntity toEntity() {
        return DeveloperEntity.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .speciality(speciality)
                .status(status)
                .build();
    }

    public static DeveloperDto toDto(DeveloperEntity entity) {
        return DeveloperDto.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .speciality(entity.getSpeciality())
                .status(entity.getStatus())
                .build();
    }

    public static List<DeveloperDto> toListDto(List<DeveloperEntity> entities) {
        List<DeveloperDto> listDto = new ArrayList<>();
        for (DeveloperEntity entity : entities) {
            DeveloperDto dto = toDto(entity);
            listDto.add(dto);
        }
        return listDto;
    }
}

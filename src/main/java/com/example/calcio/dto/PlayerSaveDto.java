package com.example.calcio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class PlayerSaveDto {

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "position is required")
    private String position;

    @NotNull(message = "clubId is required")
    private Integer clubId;
}

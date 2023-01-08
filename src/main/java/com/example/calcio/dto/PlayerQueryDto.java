package com.example.calcio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerQueryDto {
    @NotBlank(message = "position is required")
    private String position;

    @NotNull(message = "clubId is required")
    private Integer clubId;

    @NotNull(message = "page is required")
    private Integer page;

    @NotNull(message = "size is required")
    private Integer size;

}

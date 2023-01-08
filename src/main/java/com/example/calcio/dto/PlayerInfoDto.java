package com.example.calcio.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class PlayerInfoDto {

    private Integer id;
    private String name;
    private String position;
    private String clubName;
}

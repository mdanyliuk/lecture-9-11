package com.example.calcio.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Club {

    private Integer id;

    private String name;

}

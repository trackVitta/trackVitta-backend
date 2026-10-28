package com.trackNest.trackNest_App.members.dto;

import lombok.*;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RowErrorDto {

    private Integer rowNumber;

    private String fullName;

    private String reason;

    private List<String> missingFields;
}

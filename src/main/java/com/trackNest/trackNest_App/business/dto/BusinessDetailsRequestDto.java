package com.trackNest.trackNest_App.business.dto;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BusinessDetailsRequestDto {
    private UUID businessId;
    private UUID ownerId;
}

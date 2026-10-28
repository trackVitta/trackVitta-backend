package com.trackNest.trackNest_App.business.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BusinessUpdateDto {
    private UUID businessId;
    private UUID ownerId;
    private String businessName;
    private String businessDesc;
}

package com.trackNest.trackNest_App.business.dto;

import com.trackNest.trackNest_App.business.BusinessTypes;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BusinessResponseDto {
    private UUID businessId;
    private UUID userId;
    private String businessName;
    private BusinessTypes businessType;
    private String businessDesc;
    private LocalDateTime createdAt;
}

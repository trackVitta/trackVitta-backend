package com.trackNest.trackNest_App.business.dto;

import com.trackNest.trackNest_App.auth.dto.LoginResponseDto;
import com.trackNest.trackNest_App.business.BusinessTypes;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BusinessCreateRequestDto {
    private String name;
    private String desc;
    @Enumerated(EnumType.STRING)
    private BusinessTypes businessType;
    private LoginResponseDto loginResponseDto;
}

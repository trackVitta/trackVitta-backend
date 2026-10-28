package com.trackNest.trackNest_App.auth.dto;

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
public class LoginResponseDto {
    private UUID userId;
    private String email;
    private LocalDateTime createdAt;
    private Boolean isActive;
}

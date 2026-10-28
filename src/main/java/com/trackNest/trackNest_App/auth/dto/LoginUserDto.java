package com.trackNest.trackNest_App.auth.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginUserDto {

    private String email;
    private String password;
    private LocalDateTime createdAt;
}

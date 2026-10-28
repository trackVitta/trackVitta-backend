package com.trackNest.trackNest_App.auth.dto;

import lombok.*;
import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class RegisterUserDto {

    private String email;

    private String password;

    private String firstName;

    private String lastName;

    private String phone;

    private String profilePhotoUrl;

    private Boolean emailVerified;

    private Boolean phoneVerified;

    private Boolean isActive;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String responseMessage;

}

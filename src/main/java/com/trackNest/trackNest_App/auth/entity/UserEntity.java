package com.trackNest.trackNest_App.auth.entity;

import com.trackNest.trackNest_App.auth.AuthProvider;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String fullName;

    private String phone;

    private String profilePhotoUrl;

    private String providerId;

    private Boolean emailVerified;

    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    private Boolean phoneVerified;
    @Column(nullable = false)
    private Boolean isActive;
    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


}
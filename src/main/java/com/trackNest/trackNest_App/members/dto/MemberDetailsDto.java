package com.trackNest.trackNest_App.members.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberDetailsDto {
    private UUID businessId;
    private UUID ownerId;
    private UUID memberId;
}

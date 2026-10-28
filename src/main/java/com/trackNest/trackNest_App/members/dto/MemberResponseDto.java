package com.trackNest.trackNest_App.members.dto;

import com.trackNest.trackNest_App.business.dto.BusinessResponseDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MemberResponseDto {
    private UUID member_id;
    private String member_name;
    private UUID businessId;
    private LocalDateTime created_at;
}

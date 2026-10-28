package com.trackNest.trackNest_App.common.authService;

import com.trackNest.trackNest_App.members.service.MemberService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MemberAuthService {
    private final MemberService memberService;
    private final UserAuthService userAuthService;
    public MemberAuthService(MemberService memberService, UserAuthService userAuthService) {

        this.memberService = memberService;
        this.userAuthService = userAuthService;
    }

    public boolean doesMemberExistAndBelongToBusinessOfCurrentUser(UUID memberId, UUID businessId, UUID ownerId) {
        if (memberId==null || businessId==null || ownerId==null)
        {
            return false;
        }
        return (userAuthService.verifyCurrentUser(ownerId)
                &&memberService.existsByIdAndBusinessEntity_IdAndBusinessEntity_UserEntity_Id
                (memberId, businessId, ownerId));
    }
}

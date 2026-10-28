package com.trackNest.trackNest_App.common.authService;


import com.trackNest.trackNest_App.business.service.BusinessService;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BusinessAuthService {

    private final BusinessService businessService;
    private final UserAuthService userAuthService;

    public BusinessAuthService(BusinessService businessService, UserAuthService userAuthService) {

        this.businessService = businessService;
        this.userAuthService = userAuthService;
    }
    public boolean doesBusinessExistsAndBelongCurrentUser(UUID businessId, UUID ownerId) {
        if ((businessId==null) || ownerId==null) {
            return false;
        }
        return (!userAuthService.verifyCurrentUser(ownerId)
                || !businessService.existsByBusinessIdAndOwnerId(businessId, ownerId));
    }

}

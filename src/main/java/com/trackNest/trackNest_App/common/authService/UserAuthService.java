package com.trackNest.trackNest_App.common.authService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.util.UUID;

@Service
public class UserAuthService {


    public boolean verifyCurrentUser(UUID userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return !(authentication == null) && !(userId == null)
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof UUID currentUserId
                && currentUserId.equals(userId);
    }
}

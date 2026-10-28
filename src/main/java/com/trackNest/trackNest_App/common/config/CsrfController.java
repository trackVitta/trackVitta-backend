package com.trackNest.trackNest_App.common.config;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CsrfController {

    @GetMapping("/auth/csrf")
    public CsrfToken csrf(CsrfToken csrfToken) {
        return csrfToken;

    }
}
package com.trackNest.trackNest_App.auth.controller;

import com.trackNest.trackNest_App.auth.dto.LoginResponseDto;
import com.trackNest.trackNest_App.auth.dto.LoginUserDto;
import com.trackNest.trackNest_App.auth.dto.RegisterUserDto;
import com.trackNest.trackNest_App.common.security.JWTTokenGenerator;
import com.trackNest.trackNest_App.auth.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



import java.time.Duration;
import java.time.LocalDateTime;

@RestController
public class UserController {

    private  final UserService userService;

   private final JWTTokenGenerator jwtTokenService;

    public UserController(UserService userService, JWTTokenGenerator jwtTokenService) {
        this.userService = userService;
        this.jwtTokenService = jwtTokenService;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<String> registerUser( @RequestBody RegisterUserDto registerUserDto) {

        int res=userService.saveUser(RegisterUserDto.builder()
                .phone(registerUserDto.getPhone())
                .email(registerUserDto.getEmail())
                .firstName(registerUserDto.getFirstName())
                .lastName(registerUserDto.getLastName())
                .password(registerUserDto.getPassword())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .profilePhotoUrl(registerUserDto.getProfilePhotoUrl())
                .emailVerified(false) //will send verification email later using kafka
                .phoneVerified(false)
                .build());
        if (res==1) {
            return ResponseEntity.status(HttpStatus.OK).body("Registration Successful");

        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration Failed");
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> loginUserByEmail(@RequestBody LoginUserDto loginUserDto) {
        boolean userExists =userService.existsUserByEmail(loginUserDto.getEmail());
        if (
                !userExists
        ) {
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials");
        } else {
            String jwtToken= jwtTokenService.generateJwtToken(loginUserDto.getEmail());
            LoginResponseDto loginResponseDto=userService.verifyUserLogin(loginUserDto);
            if (loginResponseDto==null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            ResponseCookie cookie = ResponseCookie.from("jwttoken", jwtToken)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(Duration.ofMinutes(15))
                    .sameSite("Lax")
                    .build();
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(loginResponseDto);
        }
    };

    // Delete User Account here
//    @GetMapping("/dashboard")
//    public String dashboard(){
//        return "dashboard";
//    }

}

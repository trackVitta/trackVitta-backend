package com.trackNest.trackNest_App.auth.service;

import com.trackNest.trackNest_App.auth.AuthProvider;
import com.trackNest.trackNest_App.auth.dto.LoginResponseDto;
import com.trackNest.trackNest_App.auth.dto.LoginUserDto;
import com.trackNest.trackNest_App.auth.dto.RegisterUserDto;
import com.trackNest.trackNest_App.auth.entity.UserEntity;
import com.trackNest.trackNest_App.auth.repository.UsersRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean existsUserByEmail(String email) {
        return usersRepository.existsByEmail(email);
    }

    public int saveUser(RegisterUserDto registerUserDto) {

        boolean userEntityDb =usersRepository.existsByEmail(registerUserDto.getEmail());
        if (!userEntityDb){
            UserEntity userEntity = UserEntity.builder()
                    .email(registerUserDto.getEmail())
                    .phone(registerUserDto.getPhone())
                    .fullName(registerUserDto.getFirstName() + " " + registerUserDto.getLastName())
                    .createdAt(registerUserDto.getCreatedAt())
                    .isActive(true)
                    .emailVerified(registerUserDto.getEmailVerified())
                    .updatedAt(registerUserDto.getUpdatedAt())
                    .passwordHash(
                            passwordEncoder.encode(registerUserDto.getPassword())
                    )
                    .authProvider(AuthProvider.LOCAL)
                    .profilePhotoUrl(registerUserDto.getProfilePhotoUrl())
                    .build();

            usersRepository.save(userEntity);
            return 1;

        }
        return 0;
    }
    public LoginResponseDto findUserByEmail(String email){
        UserEntity userEntity =usersRepository.findUserByEmail(email);
        if (userEntity ==null){
            return null;
        }else  {
            return LoginResponseDto.builder()
                    .email(userEntity.getEmail())
                    .userId(userEntity.getId())
                    .createdAt(userEntity.getCreatedAt())
                    .isActive(userEntity.getIsActive())
                    .build();
        }
    }
    public LoginResponseDto verifyUserLogin(LoginUserDto loginUserDto) {
        UserEntity userEntity =usersRepository.findUserByEmail(loginUserDto.getEmail());
        if (userEntity !=null){
            if (passwordEncoder.matches(loginUserDto.getPassword(), userEntity.getPasswordHash()))
            {
                return LoginResponseDto.builder()
                        .email(userEntity.getEmail())
                        .userId(userEntity.getId())
                        .isActive(userEntity.getIsActive())
                        .createdAt(userEntity.getCreatedAt())
                        .build();
            }
            else {
                return null;
            }
        }
        return null;

    }
}

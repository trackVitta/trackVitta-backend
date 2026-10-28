package com.trackNest.trackNest_App.auth.repository;

import com.trackNest.trackNest_App.auth.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UsersRepository extends JpaRepository<UserEntity, UUID> {


    UserEntity findUserByEmail(String email);
    boolean existsByEmail(String email);
}

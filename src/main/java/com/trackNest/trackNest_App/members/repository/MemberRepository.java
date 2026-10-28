package com.trackNest.trackNest_App.members.repository;

import com.trackNest.trackNest_App.members.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, UUID> {

    List<MemberEntity> findAllByBusinessEntity_Id(UUID businessEntityId);

    MemberEntity findMemberEntityById(UUID id);

    // The old derived name pointed at BusinessEntity.userEntity, which does not exist (the field is "user"),
    // so Spring Data failed at startup. Same method name, explicit query.
    @Query("select case when count(m) > 0 then true else false end from MemberEntity m "
            + "where m.id = :memberId and m.businessEntity.id = :businessId and m.businessEntity.user.id = :userId")
    boolean existsByIdAndBusinessEntity_IdAndBusinessEntity_UserEntity_Id(
            @Param("memberId") UUID memberId,
            @Param("businessId") UUID businessId,
            @Param("userId") UUID userId
    );
}

package com.trackNest.trackNest_App.business.repository;


import com.trackNest.trackNest_App.business.entity.BusinessEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BusinessRepository extends JpaRepository<BusinessEntity, UUID> {

    List<BusinessEntity> findBusinessByUserId(UUID user_id);
    BusinessEntity findBusinessById(UUID id);

    @Query("select b from BusinessEntity b where b.id = :businessId and b.user.id = :userId")
    BusinessEntity findBusinessByBusinessIdAndUserId(@Param("businessId") UUID businessId,
                                                     @Param("userId") UUID userId);

    @Query("select case when count(b) > 0 then true else false end from BusinessEntity b "
            + "where b.id = :businessId and b.user.id = :ownerId")
    boolean existsByBusinessIdAndUserId(@Param("businessId") UUID businessId,
                                        @Param("ownerId") UUID ownerId);

    @Modifying(clearAutomatically = true)
    @Query("delete from BusinessEntity b where b.id = :businessId and b.user.id = :ownerId")
    void deleteBusinessByBusinessIdAndUserId(@Param("businessId") UUID businessId,
                                             @Param("ownerId") UUID ownerId);

}

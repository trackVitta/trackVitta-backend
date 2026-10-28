package com.trackNest.trackNest_App.business.service;

import com.trackNest.trackNest_App.auth.entity.UserEntity;
import com.trackNest.trackNest_App.business.dto.BusinessCreateRequestDto;
import com.trackNest.trackNest_App.business.dto.BusinessResponseDto;
import com.trackNest.trackNest_App.business.dto.BusinessUpdateDto;
import com.trackNest.trackNest_App.business.entity.BusinessEntity;
import com.trackNest.trackNest_App.business.repository.BusinessRepository;
import com.trackNest.trackNest_App.common.globalExceptionHandler.exceptions.BusinessNotFound;
import com.trackNest.trackNest_App.common.globalExceptionHandler.exceptions.InternalServerException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class BusinessService {


    private final BusinessRepository businessRepository;

    public BusinessService(BusinessRepository businessRepository) {
        this.businessRepository = businessRepository;
    }

    public boolean  existsByBusinessIdAndOwnerId(UUID businessId, UUID ownerId) {
        return businessRepository.existsByBusinessIdAndUserId(businessId, ownerId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public BusinessResponseDto saveBusinessForUser(BusinessCreateRequestDto businessCreateRequestDto) {
        BusinessEntity businessEntity = businessRepository.save(
                BusinessEntity.builder()
                        .businessType(businessCreateRequestDto.getBusinessType())
                        .createdAt(LocalDateTime.now())
                        .name(businessCreateRequestDto.getName())
                        .description(businessCreateRequestDto.getDesc())
                        .user(
                                UserEntity.builder()
                                        .id(businessCreateRequestDto.getLoginResponseDto().getUserId())
                                        .email(businessCreateRequestDto.getLoginResponseDto().getEmail())
                                        .build()
                        )
                        .isActive(true)
                        .updatedAt(LocalDateTime.now())
                        .build()
        );
        if (businessEntity==null) {
            return null;
        }else {
            return BusinessResponseDto.builder()
                    .businessId(businessEntity.getId())
                    .businessDesc(businessEntity.getDescription())
                    .businessName(businessEntity.getName())
                    .businessType(businessEntity.getBusinessType())
                    .createdAt(businessEntity.getCreatedAt())
                    .userId(businessEntity.getUser().getId())
                    .build();
        }
    }

    public List<BusinessResponseDto> findAllBusiness(UUID userId) {
        List<BusinessResponseDto> businessResponseDtos = new ArrayList<>();
        List<BusinessEntity> businessEntities= businessRepository.findBusinessByUserId(userId);
        for (BusinessEntity be: businessEntities) {
            BusinessResponseDto businessResponseDto=BusinessResponseDto.builder()
                    .businessId(be.getId())
                    .businessType(be.getBusinessType())
                    .businessDesc(be.getDescription())
                    .businessName(be.getName())
                    .createdAt(be.getCreatedAt())
                    .userId(be.getUser().getId())
                    .build();
            businessResponseDtos.add(businessResponseDto);

        }

        return businessResponseDtos;

    }

    public BusinessResponseDto findBusinessByBusinessId(UUID businessId) {
       BusinessEntity be= businessRepository.findBusinessById(businessId);
       if (be==null) {
           throw new BusinessNotFound("Business Not Found");
       } return BusinessResponseDto.builder()
                .businessId(be.getId())
                .businessName(be.getName())
                .businessDesc(be.getDescription())
                .businessType(be.getBusinessType())
                .userId(be.getUser().getId())
                .createdAt(be.getCreatedAt())
                .build();
    }
    public BusinessEntity getBusinessByBusinessId(UUID businessId) {
        BusinessEntity be= businessRepository.findBusinessById(businessId);
        if(be==null) {
            throw new BusinessNotFound("Business Not Found");
        }
        return be;
    }

    // on delete cascade in member_entity
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteBusinessByBusinessIdAndOwnerId(UUID businessId, UUID ownerId) {
        businessRepository.deleteBusinessByBusinessIdAndUserId(businessId, ownerId);

    }
    @Transactional(propagation = Propagation.REQUIRED)
    public BusinessResponseDto updateBusinessDetails(BusinessUpdateDto businessUpdateDto) {
        BusinessEntity businessEntity = businessRepository.findBusinessByBusinessIdAndUserId
                (businessUpdateDto.getBusinessId(),businessUpdateDto.getOwnerId());
        if (businessEntity==null) {throw new BusinessNotFound("Business Not Found");}
        businessEntity.setName(businessUpdateDto.getBusinessName());
        businessEntity.setDescription(businessUpdateDto.getBusinessDesc());
        BusinessEntity uBe = businessRepository.save(businessEntity);
        if (uBe==null) {throw new InternalServerException("Server Error");}
        return BusinessResponseDto.builder()
                .businessId(uBe.getId())
                .userId(uBe.getUser().getId())
                .businessName(uBe.getName())
                .businessDesc(uBe.getDescription()).build();
    }
    /**
     * Returns the business only if it belongs to this user. A business owned by someone else looks exactly
     * like a missing one, so ids cannot be probed.
     */
    @Transactional(readOnly = true)
    public BusinessEntity getOwnedBusiness(UUID businessId, UUID ownerId) {
        BusinessEntity be = (businessId == null || ownerId == null)
                ? null
                : businessRepository.findBusinessByBusinessIdAndUserId(businessId, ownerId);
        if (be == null) {
            throw new BusinessNotFound("Business Not Found");
        }
        return be;
    }
}

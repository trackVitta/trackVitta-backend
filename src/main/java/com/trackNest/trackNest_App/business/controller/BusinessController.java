package com.trackNest.trackNest_App.business.controller;

import com.trackNest.trackNest_App.auth.dto.LoginResponseDto;
import com.trackNest.trackNest_App.auth.service.UserService;
import com.trackNest.trackNest_App.business.dto.BusinessCreateRequestDto;
import com.trackNest.trackNest_App.business.dto.BusinessDetailsRequestDto;
import com.trackNest.trackNest_App.business.dto.BusinessResponseDto;
import com.trackNest.trackNest_App.business.dto.BusinessUpdateDto;
import com.trackNest.trackNest_App.business.service.BusinessService;
import com.trackNest.trackNest_App.common.authService.BusinessAuthService;
import com.trackNest.trackNest_App.common.authService.UserAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class BusinessController {


    private final UserService userService;
    private final BusinessService businessService;
    private final UserAuthService userAuthService;
    private final BusinessAuthService businessAuthService;

    public BusinessController(UserService userService,
                              BusinessService businessService,
                              UserAuthService userAuthService, BusinessAuthService businessAuthService) {
        this.userService = userService;
        this.businessService = businessService;
        this.userAuthService = userAuthService;
        this.businessAuthService = businessAuthService;
    }

    @PostMapping("/business/createBusiness")
    public ResponseEntity<BusinessResponseDto> createBusiness(@RequestBody BusinessCreateRequestDto businessCreateRequestDto) {
        if (businessCreateRequestDto == null|| businessCreateRequestDto.getLoginResponseDto().getUserId()==null
        || !userAuthService.verifyCurrentUser(businessCreateRequestDto.getLoginResponseDto().getUserId())) {
            return  (businessCreateRequestDto ==null || businessCreateRequestDto.getLoginResponseDto().getUserId()==null)?
                    ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
                    :ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        LoginResponseDto loginResponseDto = userService.findUserByEmail(
                businessCreateRequestDto.getLoginResponseDto().getEmail());
        if (loginResponseDto == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }else {
            BusinessResponseDto businessResponseDto=businessService.saveBusinessForUser(businessCreateRequestDto);
            if (businessResponseDto==null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }else {
                return ResponseEntity.status(HttpStatus.CREATED).body(businessResponseDto);
            }
        }
    }
    @GetMapping("/business/allBusiness/{userId}")
    public ResponseEntity<List<BusinessResponseDto>> findAllUserBusiness(@PathVariable UUID userId) {
        if (userId==null|| !userAuthService.verifyCurrentUser(userId)) {
            return (userId==null)? ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
                    :ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<BusinessResponseDto> businessResponseDtoList=businessService.findAllBusiness(userId);
        if (businessResponseDtoList==null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }else  {
            return ResponseEntity.status(HttpStatus.OK).body(businessResponseDtoList);
        }
    }

    @GetMapping("/business/getBusinessbyId/{ownerId}/{businessId}")
    public ResponseEntity<BusinessResponseDto> findBusinessByBusinessId
            (@PathVariable UUID ownerId, @PathVariable UUID businessId ) {
        if (businessId==null||
                ownerId==null||
                businessAuthService.doesBusinessExistsAndBelongCurrentUser
                        (businessId,
                                ownerId))
        {
            return (businessId==null ||
                    ownerId==null)?
                    ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
                    :ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
       BusinessResponseDto businessResponseDto= businessService.
               findBusinessByBusinessId(businessId);
        return (businessResponseDto==null)?
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
                :ResponseEntity.status(HttpStatus.OK).body(businessResponseDto);
    }

    @DeleteMapping("/business/deleteBusinessById/{ownerId}/{businessId}")
    public ResponseEntity<Void> deleteBusinessByBusinessId
            (@PathVariable UUID ownerId, @PathVariable UUID businessId ) {
        if (businessId==null
                || ownerId==null
            || businessAuthService.doesBusinessExistsAndBelongCurrentUser
                (businessId, ownerId)) {
            return (businessId == null
                    || ownerId == null) ?
                    ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
                    : ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        businessService.deleteBusinessByBusinessIdAndOwnerId
                (businessId, ownerId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
        }
    @PutMapping("/business/updateBusinessbyId/")
    public ResponseEntity<BusinessResponseDto>  updateBusinessByBusinessId
            (@RequestBody BusinessUpdateDto businessUpdateDto) {
        if (businessUpdateDto.getBusinessId()==null
        || businessUpdateDto.getOwnerId()==null
        || businessUpdateDto.getBusinessName()==null
        || businessAuthService.doesBusinessExistsAndBelongCurrentUser
                (businessUpdateDto.getBusinessId(), businessUpdateDto.getOwnerId())) {
            return (businessUpdateDto.getBusinessId()==null
                    || businessUpdateDto.getOwnerId()==null
                    || businessUpdateDto.getBusinessName()==null)?
                    ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
                    : ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        BusinessResponseDto businessResponseDto=businessService.updateBusinessDetails(businessUpdateDto);
        return (businessResponseDto==null)?
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
                :ResponseEntity.status(HttpStatus.OK).body(businessResponseDto);
    }

}

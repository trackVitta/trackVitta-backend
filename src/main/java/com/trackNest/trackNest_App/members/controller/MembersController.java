package com.trackNest.trackNest_App.members.controller;

import com.trackNest.trackNest_App.common.authService.BusinessAuthService;
import com.trackNest.trackNest_App.common.authService.MemberAuthService;
import com.trackNest.trackNest_App.members.dto.*;
import com.trackNest.trackNest_App.members.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class MembersController {

    private final MemberService memberService;
    private final MemberAuthService memberAuthService;
    private final BusinessAuthService businessAuthService;
    public MembersController(MemberService memberService, MemberAuthService memberAuthService, BusinessAuthService businessAuthService) {
        this.memberService = memberService;
        this.memberAuthService = memberAuthService;
        this.businessAuthService = businessAuthService;
    }


    @PostMapping("/business/member/createMember")
    public ResponseEntity<MemberResponseDto>
    createMember(@RequestBody MemberRequestDto memberRequestDto) {
        if (memberRequestDto.getMembership_fee() == null
                || memberRequestDto.getMembership_fee().signum() < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        if (memberRequestDto.getBusinessId() == null
        || memberRequestDto.getOwnerId() == null
                || memberRequestDto.getFullName() == null
        || businessAuthService.doesBusinessExistsAndBelongCurrentUser
                (memberRequestDto.getBusinessId(), memberRequestDto.getOwnerId())) {
            return (memberRequestDto.getBusinessId() == null
                    || memberRequestDto.getOwnerId() == null
                    || memberRequestDto.getFullName() == null)?
                    ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
                    : ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        MemberResponseDto res= memberService.saveMember(memberRequestDto, memberRequestDto.getBusinessId());
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }
    @PutMapping("/business/member/updateMember/{memberId}")
    public ResponseEntity<MemberResponseDto>
    updateMember(@RequestBody MemberRequestDto memberRequestDto, @PathVariable UUID memberId) {
        //setting member status inactive/active OR changing Membership plan
        if (memberRequestDto.getBusinessId() == null
        || memberRequestDto.getOwnerId() == null
        || memberId==null
        || !memberAuthService.doesMemberExistAndBelongToBusinessOfCurrentUser
                (memberId,memberRequestDto.getBusinessId(),memberRequestDto.getOwnerId())) {
            return (memberRequestDto.getBusinessId() == null
                    || memberRequestDto.getOwnerId() == null
                    || memberId==null)?
                    ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
                    : ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        MemberResponseDto res= memberService.updateMember(memberRequestDto, memberId);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
    @GetMapping("/business/member/getAllmembers/{ownerId}/{businessId}")
    public ResponseEntity<List<MemberResponseDto>> getAllMembers(@PathVariable UUID ownerId, @PathVariable UUID businessId ) {
        if (businessId==null
                || ownerId==null
        || businessAuthService.doesBusinessExistsAndBelongCurrentUser(businessId, ownerId)) {
            return (businessId==null
                    || ownerId==null)?
                    ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
                    : ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        List<MemberResponseDto> res= memberService.findAllMembersByBusinessId(businessId);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
    @GetMapping("/business/member/getMemberById/{ownerId}/{businessId}/{memberId}")
    public ResponseEntity<MemberResponseDto> getMemberById(@PathVariable UUID ownerId, @PathVariable UUID businessId ,
                                                           @PathVariable UUID memberId) {
        if (businessId==null
        || ownerId==null
        || memberId==null
        || !memberAuthService.doesMemberExistAndBelongToBusinessOfCurrentUser
                (memberId,businessId,ownerId)) {
            return (businessId==null
                    || ownerId==null
                    || memberId==null)?
                    ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
                    : ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        MemberResponseDto res=memberService.findMemberById(memberId);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
    @DeleteMapping("/business/member/deleteMemberById/{ownerId}/{businessId}/{memberId}")
    public ResponseEntity<String> deleteMemberById(@PathVariable UUID ownerId, @PathVariable UUID businessId ,
                                                   @PathVariable UUID memberId) {
        if (businessId==null
                || ownerId==null
                || memberId==null
                || !memberAuthService.doesMemberExistAndBelongToBusinessOfCurrentUser
                (memberId,businessId,ownerId)) {
            return (businessId==null
                    || ownerId==null
                    || memberId==null)?
                    ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
                    : ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        memberService.deleteMemberById(memberId);
        return ResponseEntity.status(HttpStatus.OK).body("Member with id: " + memberId + " has been deleted");
    }
    // ---- bulk import: analyze -> preview -> commit (all three take the same multipart "file") ----

    @PostMapping("/business/{businessId}/member/import/analyze")
    public ResponseEntity<ImportAnalyzeResponseDto> analyzeImport(
            @PathVariable UUID businessId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        return ResponseEntity.ok(memberService.analyzeImport(file, businessId, currentUserId(authentication)));
    }

    @PostMapping("/business/{businessId}/member/import/preview")
    public ResponseEntity<ImportPreviewResponseDto> previewImport(
            @PathVariable UUID businessId,
            @RequestParam("file") MultipartFile file,
            @RequestParam Map<String, String> mapping,
            Authentication authentication) {
        return ResponseEntity.ok(memberService.previewImport(file, businessId, mapping, currentUserId(authentication)));
    }

    @PostMapping("/business/{businessId}/member/import/commit")
    public ResponseEntity<BulkImportMemberResponseDto> commitImport(
            @PathVariable UUID businessId,
            @RequestParam("file") MultipartFile file,
            @RequestParam Map<String, String> mapping,
            Authentication authentication) {
        return ResponseEntity.ok(memberService.bulkImportMembers(file, businessId, mapping, currentUserId(authentication)));
    }

    /** The JWT filter stores the user's UUID as the principal. */
    private static UUID currentUserId(Authentication authentication) {
        Object principal = authentication == null ? null : authentication.getPrincipal();
        if (principal instanceof UUID) {
            return (UUID) principal;
        }
        throw new AuthenticationCredentialsNotFoundException("Not logged in");
    }

}

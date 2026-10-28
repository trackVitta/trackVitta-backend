package com.trackNest.trackNest_App.members.imports;

import com.trackNest.trackNest_App.members.MembershipPlan;

import java.math.BigDecimal;
import java.util.Date;

/** A fully validated row, ready to be turned into a MemberEntity. */
public record ImportedMember(
        String fullName,
        String email,
        String memberIdOfBusiness,
        MembershipPlan membershipPlan,
        BigDecimal membershipFee,
        String phone,
        String address,
        Date dateOfBirth,
        String gender,
        Date membershipStart,
        Date membershipEnd,
        String status,
        String emergencyContactName,
        String emergencyContactPhone,
        String notes) {
}

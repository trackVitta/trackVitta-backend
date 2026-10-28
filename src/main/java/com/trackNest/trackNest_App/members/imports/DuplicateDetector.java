package com.trackNest.trackNest_App.members.imports;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * MemberEntity declares member ID, phone and email as unique on their own, so the import treats a row as a
 * duplicate when ANY of those three values is already used by an existing member of this business or by an
 * earlier row of the same file. Catching it here gives a per-row reason instead of one database error that
 * would cancel the whole import.
 */
public final class DuplicateDetector {

    private final Set<String> existingKeys;
    private final Map<String, Integer> fileKeys = new HashMap<>();

    public DuplicateDetector(Set<String> existingKeys) {
        this.existingKeys = existingKeys;
    }

    /** Keys of one member: "id:...", "ph:...", "em:..." (only for values that are present). */
    public static Set<String> keysOf(String memberId, String phone, String email) {
        Set<String> keys = new LinkedHashSet<>();
        String id = clean(memberId);
        String digits = phoneDigits(phone);
        String mail = clean(email);
        if (!id.isEmpty()) {
            keys.add("id:" + id);
        }
        if (!digits.isEmpty()) {
            keys.add("ph:" + digits);
        }
        if (!mail.isEmpty()) {
            keys.add("em:" + mail);
        }
        return keys;
    }

    /** Returns a human reason when the row is a duplicate, otherwise null. */
    public String check(ImportedMember m) {
        for (String key : keysOf(m.memberIdOfBusiness(), m.phone(), m.email())) {
            if (existingKeys.contains(key)) {
                return describe(key) + " is already used by another member of this business";
            }
            Integer firstRow = fileKeys.get(key);
            if (firstRow != null) {
                return describe(key) + " is repeated (first used in row " + firstRow + ")";
            }
        }
        return null;
    }

    public void register(ImportedMember m, int rowNumber) {
        for (String key : keysOf(m.memberIdOfBusiness(), m.phone(), m.email())) {
            fileKeys.putIfAbsent(key, rowNumber);
        }
    }

    private static String describe(String key) {
        if (key.startsWith("id:")) {
            return "Member ID '" + key.substring(3) + "'";
        }
        return key.startsWith("ph:") ? "Phone number" : "Email";
    }

    private static String clean(String s) {
        return s == null ? "" : s.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }

    private static String phoneDigits(String phone) {
        if (phone == null) {
            return "";
        }
        String digits = phone.replaceAll("\\D", "");
        return digits.length() > 10 ? digits.substring(digits.length() - 10) : digits;
    }
}

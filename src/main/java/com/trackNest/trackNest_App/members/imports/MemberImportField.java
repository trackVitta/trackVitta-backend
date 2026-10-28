package com.trackNest.trackNest_App.members.imports;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Every MemberEntity column a user may import into. The key is the exact entity property name,
 * so "map to our member column" is literal. Synonyms drive the automatic column suggestion.
 */
public enum MemberImportField {

    FULL_NAME("fullName", "Full name", FieldType.TEXT, true, false,
            "name", "full name", "fullname", "member name", "customer name", "student name", "client name",
            "member", "customer", "student", "client"),
    MEMBER_ID("memberId_ofBusiness", "Member ID", FieldType.TEXT, false, false,
            "member id", "memberid", "customer id", "student id", "client id", "id", "roll no", "roll number",
            "reg no", "registration number", "membership number", "membership no", "member no", "member number",
            "member code", "customer code"),
    EMAIL("email", "Email", FieldType.TEXT, false, false,
            "email", "e mail", "email id", "email address", "mail", "mail id", "e mail id"),
    PHONE("phone", "Phone", FieldType.TEXT, false, false,
            "phone", "phone number", "phone no", "contact", "contact number", "contact no", "contact details",
            "mobile", "mobile number", "mobile no", "whatsapp", "whatsapp number", "cell", "telephone"),
    ADDRESS("address", "Address", FieldType.TEXT, false, false,
            "address", "residential address", "home address", "location"),
    DATE_OF_BIRTH("date_of_birth", "Date of birth", FieldType.DATE, false, false,
            "dob", "d o b", "date of birth", "birth date", "birthdate", "birthday"),
    GENDER("gender", "Gender", FieldType.TEXT, false, false,
            "gender", "sex"),
    MEMBERSHIP_PLAN("membershipPlan", "Membership plan", FieldType.PLAN, true, true,
            "plan", "membership plan", "membership", "plan type", "membership type", "package", "subscription",
            "fee plan", "billing cycle", "duration"),
    FEE("membership_fee", "Membership fee", FieldType.NUMBER, true, true,
            "fee", "fees", "membership fee", "membership fees", "monthly fee", "plan fee", "fee amount",
            "amount", "price", "charges", "subscription fee"),
    MEMBERSHIP_START("membership_start", "Membership start", FieldType.DATE, false, false,
            "start date", "membership start", "membership start date", "joining date", "join date",
            "date of joining", "doj", "joined on", "start", "admission date"),
    MEMBERSHIP_END("membership_end", "Membership end", FieldType.DATE, false, false,
            "end date", "membership end", "membership end date", "expiry", "expiry date", "expires on",
            "valid till", "valid until", "valid upto", "renewal date"),
    STATUS("status", "Status", FieldType.STATUS, false, true,
            "status", "member status", "active"),
    EMERGENCY_CONTACT_NAME("emergency_contact_name", "Emergency contact name", FieldType.TEXT, false, false,
            "emergency contact", "emergency contact name", "emergency name", "guardian", "guardian name",
            "parent name", "parent", "father name", "mother name"),
    EMERGENCY_CONTACT_PHONE("emergency_contact_phone", "Emergency contact phone", FieldType.TEXT, false, false,
            "emergency phone", "emergency contact phone", "emergency contact number", "emergency number",
            "guardian phone", "guardian contact", "parent phone", "parent contact", "parent mobile",
            "parent number", "alternate number", "alternate phone"),
    NOTES("notes", "Notes", FieldType.TEXT, false, false,
            "notes", "note", "remarks", "remark", "comment", "comments");

    public enum FieldType { TEXT, DATE, PLAN, STATUS, NUMBER }

    private final String key;
    private final String label;
    private final FieldType type;
    private final boolean required;
    private final boolean allowsDefault;
    private final List<String> synonyms;

    MemberImportField(String key, String label, FieldType type, boolean required, boolean allowsDefault,
                      String... synonyms) {
        this.key = key;
        this.label = label;
        this.type = type;
        this.required = required;
        this.allowsDefault = allowsDefault;
        this.synonyms = List.of(synonyms);
    }

    public String key() { return key; }
    public String label() { return label; }
    public FieldType type() { return type; }
    public boolean required() { return required; }
    public boolean allowsDefault() { return allowsDefault; }

    public static MemberImportField byKey(String key) {
        for (MemberImportField f : values()) {
            if (f.key.equals(key)) {
                return f;
            }
        }
        return null;
    }

    public static String normalizeHeader(String s) {
        if (s == null) {
            return "";
        }
        return s.toLowerCase(Locale.ROOT).replaceAll("[^\\p{L}\\p{N}]+", " ").trim();
    }

    private record Candidate(int score, int fieldOrdinal, int column) { }

    /**
     * Suggests which file column feeds which field. Exact header matches win over partial ones,
     * and every column / field is used at most once. Only a suggestion: the user confirms it.
     */
    public static Map<String, Integer> suggest(List<String> headers) {
        List<String> normalized = new ArrayList<>();
        for (String h : headers) {
            normalized.add(normalizeHeader(h));
        }

        List<Candidate> candidates = new ArrayList<>();
        for (MemberImportField field : values()) {
            Set<String> names = new HashSet<>(field.synonyms);
            names.add(normalizeHeader(field.key));
            names.add(normalizeHeader(field.label));
            for (int c = 0; c < normalized.size(); c++) {
                String h = normalized.get(c);
                if (h.isEmpty()) {
                    continue;
                }
                if (names.contains(h)) {
                    candidates.add(new Candidate(100, field.ordinal(), c));
                    continue;
                }
                int best = 0;
                for (String s : names) {
                    if (s.length() >= 4 && (" " + h + " ").contains(" " + s + " ")) {
                        best = Math.max(best, 10 + s.length());
                    }
                }
                if (best > 0) {
                    candidates.add(new Candidate(best, field.ordinal(), c));
                }
            }
        }

        candidates.sort(new Comparator<Candidate>() {
            @Override
            public int compare(Candidate a, Candidate b) {
                if (a.score() != b.score()) {
                    return Integer.compare(b.score(), a.score());
                }
                if (a.fieldOrdinal() != b.fieldOrdinal()) {
                    return Integer.compare(a.fieldOrdinal(), b.fieldOrdinal());
                }
                return Integer.compare(a.column(), b.column());
            }
        });

        Map<String, Integer> result = new LinkedHashMap<>();
        Set<Integer> usedColumns = new HashSet<>();
        Set<MemberImportField> usedFields = new HashSet<>();
        for (Candidate cand : candidates) {
            MemberImportField field = values()[cand.fieldOrdinal()];
            if (usedFields.contains(field) || usedColumns.contains(cand.column())) {
                continue;
            }
            usedFields.add(field);
            usedColumns.add(cand.column());
            result.put(field.key, cand.column());
        }
        return result;
    }
}

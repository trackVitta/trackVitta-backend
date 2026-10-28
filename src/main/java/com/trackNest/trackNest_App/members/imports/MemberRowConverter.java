package com.trackNest.trackNest_App.members.imports;

import com.trackNest.trackNest_App.common.globalExceptionHandler.exceptions.ImportMappingException;
import com.trackNest.trackNest_App.members.MembershipPlan;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import static com.trackNest.trackNest_App.members.imports.MemberImportField.*;

/** Validates and normalizes one file row according to the user's column mapping. Pure logic, no Spring. */
public final class MemberRowConverter {

    /** MemberEntity text columns are plain varchar(255); a longer value would abort the whole insert. */
    private static final int MAX_TEXT = 255;
    private static final int MAX_ID = 100;

    private static final Pattern EMAIL_REGEX = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern PHONE_REGEX = Pattern.compile("^\\+?\\d{7,15}$");
    private static final Pattern SCIENTIFIC = Pattern.compile("^[+-]?\\d(\\.\\d+)?[eE][+-]?\\d+$");
    private static final Pattern ISO_TIMESTAMP = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}[T ].*");
    private static final Pattern EXCEL_SERIAL = Pattern.compile("^\\d{5}(\\.\\d+)?$");

    private static final List<DateTimeFormatter> DATE_FORMATS = List.of(
            fmt("uuuu-M-d"), fmt("d/M/uuuu"), fmt("d-M-uuuu"), fmt("d.M.uuuu"), fmt("uuuu/M/d"),
            fmt("d MMM uuuu"), fmt("d-MMM-uuuu"), fmt("d/MMM/uuuu"), fmt("MMM d, uuuu"),
            fmt("d MMMM uuuu"), fmt("MMMM d, uuuu"));

    private static final Map<String, MembershipPlan> PLAN_WORDS = new HashMap<>();
    private static final Map<String, String> STATUS_WORDS = new HashMap<>();

    static {
        plan(MembershipPlan.MONTHLY, "monthly", "month", "1 month", "one month", "30 days");
        plan(MembershipPlan.DAILY, "daily", "day", "1 day", "one day");
        plan(MembershipPlan.WEEKLY, "weekly", "week", "1 week", "one week", "7 days");
        plan(MembershipPlan.QUARTERLY, "quarterly", "quarter", "3 months", "3 month", "three months", "90 days");
        plan(MembershipPlan.HOURLY, "hourly", "hour", "1 hour", "one hour");
        plan(MembershipPlan.HALF_YEARLY, "half yearly", "halfyearly", "half year", "semi annual", "semiannual",
                "semi annually", "biannual", "6 months", "6 month", "six months", "180 days");
        plan(MembershipPlan.ANNUALLY, "annually", "annual", "yearly", "year", "1 year", "one year",
                "12 months", "12 month", "365 days");
        plan(MembershipPlan.OTHERS, "others", "other", "custom");

        status("ACTIVE", "active");
        status("INACTIVE", "inactive", "in active", "not active");
        status("EXPIRED", "expired");
    }

    private static void plan(MembershipPlan plan, String... words) {
        for (String w : words) {
            PLAN_WORDS.put(w, plan);
        }
    }

    private static void status(String status, String... words) {
        for (String w : words) {
            STATUS_WORDS.put(w, status);
        }
    }

    private static DateTimeFormatter fmt(String pattern) {
        return new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern(pattern)
                .toFormatter(Locale.ENGLISH).withResolverStyle(ResolverStyle.STRICT);
    }

    private final ImportMapping mapping;
    private final MembershipPlan defaultPlan;
    private final String defaultStatus;
    private final BigDecimal defaultFee;

    public MemberRowConverter(ImportMapping mapping) {
        this.mapping = mapping;

        String plan = mapping.defaults().get(MEMBERSHIP_PLAN);
        MembershipPlan parsedPlan = null;
        if (plan != null) {
            parsedPlan = parsePlan(plan);
            if (parsedPlan == null) {
                throw new ImportMappingException("Unknown default membership plan '" + plan + "'");
            }
        }
        this.defaultPlan = parsedPlan;

        String status = mapping.defaults().get(STATUS);
        String parsedStatus = null;
        if (status != null) {
            parsedStatus = parseStatus(status);
            if (parsedStatus == null) {
                throw new ImportMappingException("Unknown default status '" + status + "'");
            }
        }
        this.defaultStatus = parsedStatus;

        String fee = mapping.defaults().get(FEE);
        BigDecimal parsedFee = null;
        if (fee != null) {
            parsedFee = parseFee(fee);
            if (parsedFee == null) {
                throw new ImportMappingException("The fee '" + fee + "' is not a valid amount");
            }
        }
        this.defaultFee = parsedFee;
    }

    public ConvertedRow convert(ParsedRow row) {
        List<String> problems = new ArrayList<>();
        List<String> missing = new ArrayList<>();
        Map<String, String> display = new LinkedHashMap<>();

        String fullName = text(row, FULL_NAME);
        if (fullName.isEmpty()) {
            missing.add(FULL_NAME.key());
            problems.add("Full name is required");
        } else {
            checkLength(FULL_NAME, fullName, MAX_TEXT, problems);
        }

        String memberId = text(row, MEMBER_ID);
        checkLength(MEMBER_ID, memberId, MAX_ID, problems);

        String email = text(row, EMAIL).toLowerCase(Locale.ROOT);
        if (!email.isEmpty() && (email.length() > MAX_TEXT || !EMAIL_REGEX.matcher(email).matches())) {
            problems.add("Invalid email '" + shorten(email) + "'");
        }

        String phone = text(row, PHONE);
        if (!phone.isEmpty()) {
            String normalized = normalizePhone(phone);
            if (normalized == null) {
                problems.add(invalidPhone("Phone", phone));
            } else {
                phone = normalized;
            }
        }

        String address = text(row, ADDRESS);
        checkLength(ADDRESS, address, MAX_TEXT, problems);
        String gender = text(row, GENDER);
        checkLength(GENDER, gender, MAX_TEXT, problems);
        String emergencyName = text(row, EMERGENCY_CONTACT_NAME);
        checkLength(EMERGENCY_CONTACT_NAME, emergencyName, MAX_TEXT, problems);
        String notes = text(row, NOTES);
        checkLength(NOTES, notes, MAX_TEXT, problems);

        String emergencyPhone = text(row, EMERGENCY_CONTACT_PHONE);
        if (!emergencyPhone.isEmpty()) {
            String normalized = normalizePhone(emergencyPhone);
            if (normalized == null) {
                problems.add(invalidPhone("Emergency contact phone", emergencyPhone));
            } else {
                emergencyPhone = normalized;
            }
        }

        String rawPlan = text(row, MEMBERSHIP_PLAN);
        MembershipPlan plan;
        if (rawPlan.isEmpty()) {
            plan = defaultPlan;
            if (plan == null) {
                missing.add(MEMBERSHIP_PLAN.key());
                problems.add("Membership plan is required");
            }
        } else {
            plan = parsePlan(rawPlan);
            if (plan == null) {
                problems.add("Unknown membership plan '" + shorten(rawPlan) + "'");
            }
        }

        String rawFee = text(row, FEE);
        BigDecimal fee;
        if (rawFee.isEmpty()) {
            fee = defaultFee;
            if (fee == null) {
                missing.add(FEE.key());
                problems.add("Membership fee is required");
            }
        } else {
            fee = parseFee(rawFee);
            if (fee == null) {
                problems.add("Invalid membership fee '" + shorten(rawFee) + "' (use a number like 1500 or 1,500.50)");
            }
        }

        String rawStatus = text(row, STATUS);
        String status;
        if (rawStatus.isEmpty()) {
            status = defaultStatus != null ? defaultStatus : "ACTIVE";
        } else {
            status = parseStatus(rawStatus);
            if (status == null) {
                problems.add("Unknown status '" + shorten(rawStatus) + "' (use Active, Inactive or Expired)");
            }
        }

        LocalDate dob = dateField(row, DATE_OF_BIRTH, problems);
        if (dob != null) {
            if (dob.isAfter(LocalDate.now())) {
                problems.add("Date of birth is in the future");
            } else if (dob.getYear() < 1900) {
                problems.add("Date of birth looks wrong: " + dob);
            }
        }
        LocalDate start = dateField(row, MEMBERSHIP_START, problems);
        LocalDate end = dateField(row, MEMBERSHIP_END, problems);
        checkYear(MEMBERSHIP_START, start, problems);
        checkYear(MEMBERSHIP_END, end, problems);
        if (start != null && end != null && end.isBefore(start)) {
            problems.add("Membership end is before membership start");
        }

        show(display, FULL_NAME, fullName);
        show(display, MEMBER_ID, memberId);
        show(display, EMAIL, email);
        show(display, PHONE, phone);
        show(display, ADDRESS, address);
        show(display, DATE_OF_BIRTH, dob == null ? "" : dob.toString());
        show(display, GENDER, gender);
        show(display, MEMBERSHIP_PLAN, plan == null ? rawPlan : plan.name());
        show(display, FEE, fee == null ? rawFee : fee.toPlainString());
        show(display, MEMBERSHIP_START, start == null ? "" : start.toString());
        show(display, MEMBERSHIP_END, end == null ? "" : end.toString());
        show(display, STATUS, status == null ? rawStatus : status);
        show(display, EMERGENCY_CONTACT_NAME, emergencyName);
        show(display, EMERGENCY_CONTACT_PHONE, emergencyPhone);
        show(display, NOTES, notes);

        ImportedMember member = null;
        if (problems.isEmpty()) {
            member = new ImportedMember(fullName, blankToNull(email), blankToNull(memberId), plan, fee,
                    blankToNull(phone), blankToNull(address), toDate(dob), blankToNull(gender),
                    toDate(start), toDate(end), status, blankToNull(emergencyName),
                    blankToNull(emergencyPhone), blankToNull(notes));
        }
        return new ConvertedRow(row.rowNumber(), member, display, List.copyOf(problems), List.copyOf(missing));
    }

    // ---- helpers -----------------------------------------------------------------------------

    private String text(ParsedRow row, MemberImportField field) {
        Integer column = mapping.columns().get(field);
        return column == null ? "" : normalizeSpaces(row.cell(column));
    }

    private void show(Map<String, String> display, MemberImportField field, String value) {
        boolean visible = mapping.columns().containsKey(field)
                || mapping.defaults().containsKey(field)
                || field == STATUS;
        if (visible) {
            display.put(field.key(), value == null ? "" : value);
        }
    }

    private LocalDate dateField(ParsedRow row, MemberImportField field, List<String> problems) {
        String raw = text(row, field);
        if (raw.isEmpty()) {
            return null;
        }
        LocalDate date = parseDate(raw);
        if (date == null) {
            problems.add("Invalid date '" + shorten(raw) + "' in '" + field.label()
                    + "' (use dd/mm/yyyy or yyyy-mm-dd)");
        }
        return date;
    }

    private static void checkYear(MemberImportField field, LocalDate date, List<String> problems) {
        if (date != null && (date.getYear() < 1990 || date.getYear() > 2200)) {
            problems.add(field.label() + " looks wrong: " + date);
        }
    }

    private static void checkLength(MemberImportField field, String value, int max, List<String> problems) {
        if (value.length() > max) {
            problems.add(field.label() + " is longer than " + max + " characters");
        }
    }

    private static String invalidPhone(String label, String raw) {
        if (SCIENTIFIC.matcher(raw).matches()) {
            return label + " '" + shorten(raw) + "' was turned into scientific notation - format that column as Text in Excel";
        }
        return "Invalid " + label.toLowerCase(Locale.ROOT) + " '" + shorten(raw) + "' (expected 7-15 digits)";
    }

    private static String shorten(String s) {
        return s.length() <= 40 ? s : s.substring(0, 37) + "...";
    }

    private static String normalizeSpaces(String s) {
        return s == null ? "" : s.trim().replaceAll("\\s+", " ");
    }

    private static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s;
    }

    private static Date toDate(LocalDate date) {
        return date == null ? null : Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /** Accepts 1500, 1,500.50, Rs. 1500, INR 1500, 1500/-. Returns null when it is not a usable amount. */
    public static BigDecimal parseFee(String raw) {
        String s = raw.trim().toLowerCase(Locale.ROOT)
                .replace("\u20b9", "").replace("inr", "").replace("rs.", "").replace("rs", "")
                .replace("/-", "").replace(",", "").replaceAll("\\s+", "");
        if (!s.matches("^\\d{1,9}(\\.\\d{1,2})?$")) {
            return null;
        }
        return new BigDecimal(s);
    }

    public static String normalizePhone(String raw) {
        String s = raw.replaceAll("[\\s\\-().]", "");
        return PHONE_REGEX.matcher(s).matches() ? s : null;
    }

    public static MembershipPlan parsePlan(String raw) {
        return PLAN_WORDS.get(MemberImportField.normalizeHeader(raw));
    }

    public static String parseStatus(String raw) {
        return STATUS_WORDS.get(MemberImportField.normalizeHeader(raw));
    }

    /** Day-first: 03/04/2024 is 3 April 2024. Returns null when the text is not a recognizable date. */
    public static LocalDate parseDate(String raw) {
        String s = raw.trim();
        if (ISO_TIMESTAMP.matcher(s).matches()) {
            s = s.substring(0, 10);
        }
        if (EXCEL_SERIAL.matcher(s).matches()) {
            double serial = Double.parseDouble(s);
            if (serial >= 20000 && serial <= 80000) {
                return LocalDate.of(1899, 12, 30).plusDays((long) serial);
            }
        }
        for (DateTimeFormatter f : DATE_FORMATS) {
            try {
                return LocalDate.parse(s, f);
            } catch (java.time.DateTimeException ignored) {
                // try next format
            }
        }
        return null;
    }
}

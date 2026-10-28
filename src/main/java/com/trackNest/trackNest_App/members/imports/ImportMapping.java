package com.trackNest.trackNest_App.members.imports;

import com.trackNest.trackNest_App.common.globalExceptionHandler.exceptions.ImportMappingException;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * The user's answer to "which file column feeds which member field".
 * Sent as plain form fields: fullName=0, phone=3, ... and default_membershipPlan=MONTHLY, default_status=ACTIVE.
 */
public record ImportMapping(Map<MemberImportField, Integer> columns, Map<MemberImportField, String> defaults) {

    public static ImportMapping fromParams(Map<String, String> params, List<String> headers) {
        Map<MemberImportField, Integer> columns = new EnumMap<>(MemberImportField.class);
        Map<MemberImportField, String> defaults = new EnumMap<>(MemberImportField.class);
        Map<Integer, MemberImportField> columnOwner = new java.util.HashMap<>();

        for (MemberImportField field : MemberImportField.values()) {
            String raw = params.get(field.key());
            if (raw != null && !raw.isBlank()) {
                int index;
                try {
                    index = Integer.parseInt(raw.trim());
                } catch (NumberFormatException e) {
                    throw new ImportMappingException("Invalid column for '" + field.label() + "'");
                }
                if (index < 0 || index >= headers.size()) {
                    throw new ImportMappingException("Column for '" + field.label() + "' does not exist in the file");
                }
                MemberImportField other = columnOwner.get(index);
                if (other != null) {
                    throw new ImportMappingException("Column '" + headers.get(index) + "' is mapped to both '"
                            + other.label() + "' and '" + field.label() + "'");
                }
                columnOwner.put(index, field);
                columns.put(field, index);
            }

            String def = params.get("default_" + field.key());
            if (def != null && !def.isBlank()) {
                if (!field.allowsDefault()) {
                    throw new ImportMappingException("'" + field.label() + "' cannot have a default value");
                }
                defaults.put(field, def.trim());
            }
        }

        if (!columns.containsKey(MemberImportField.FULL_NAME)) {
            throw new ImportMappingException("Map a column to 'Full name' - it is required");
        }
        if (!columns.containsKey(MemberImportField.MEMBERSHIP_PLAN)
                && !defaults.containsKey(MemberImportField.MEMBERSHIP_PLAN)) {
            throw new ImportMappingException(
                    "Map a column to 'Membership plan' or choose one plan to apply to every member");
        }
        if (!columns.containsKey(MemberImportField.FEE) && !defaults.containsKey(MemberImportField.FEE)) {
            throw new ImportMappingException(
                    "Map a column to 'Membership fee' or enter one fee to apply to every member");
        }
        return new ImportMapping(columns, defaults);
    }
}

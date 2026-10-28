package com.trackNest.trackNest_App.members.imports;

import java.util.List;
import java.util.Map;

/**
 * Result of converting one file row. member is null when the row has problems.
 * display holds the normalized text of every mapped/defaulted field (used for the preview table).
 */
public record ConvertedRow(
        int rowNumber,
        ImportedMember member,
        Map<String, String> display,
        List<String> problems,
        List<String> missingFields) {
}

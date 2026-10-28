package com.trackNest.trackNest_App.members.imports;

import java.util.List;

public record RowIssue(int rowNumber, String fullName, String reason, List<String> missingFields, boolean duplicate) {
}

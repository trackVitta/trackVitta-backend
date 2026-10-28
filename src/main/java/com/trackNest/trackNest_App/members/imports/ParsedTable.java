package com.trackNest.trackNest_App.members.imports;

import java.util.List;

/** An uploaded file reduced to a header row plus data rows (all cells as trimmed text). */
public record ParsedTable(String fileName, String fileType, List<String> headers, List<ParsedRow> rows) {
}

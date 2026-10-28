package com.trackNest.trackNest_App.members.dto;

import java.util.List;
import java.util.Map;

/** Step 1 answer: what the file contains, what we can map to, and our best guess of the mapping. */
public record ImportAnalyzeResponseDto(
        String fileName,
        String fileType,
        int totalRows,
        List<ImportColumnDto> columns,
        List<ImportFieldDto> fields,
        Map<String, Integer> suggestedMapping) {
}

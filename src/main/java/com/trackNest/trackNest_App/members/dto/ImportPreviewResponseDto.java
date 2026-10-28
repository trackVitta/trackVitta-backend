package com.trackNest.trackNest_App.members.dto;

import java.util.List;

/** Step 2 answer: what an import with this mapping would do, plus up to 5 random valid rows as examples. */
public record ImportPreviewResponseDto(
        int totalRows,
        int validRows,
        int invalidRows,
        int duplicateRows,
        List<ImportFieldDto> mappedFields,
        List<ImportPreviewRowDto> sampleRows,
        List<RowErrorDto> errors,
        boolean errorsTruncated,
        boolean canImport) {
}

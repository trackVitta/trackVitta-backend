package com.trackNest.trackNest_App.members.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** Step 3 answer: what was actually imported. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkImportMemberResponseDto {
    private Integer totalRows;
    private Integer successImport;
    private Integer failedImport;
    private Integer duplicateSkipped;
    private List<Integer> failedRowsNumbers;
    private List<RowErrorDto> rowErrors;
    private Boolean errorsTruncated;
}

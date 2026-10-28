package com.trackNest.trackNest_App.members.imports;

import java.util.List;

/** Outcome of running a whole file through a mapping. Used by both preview and commit so they can never disagree. */
public record ImportPlan(int totalRows, List<ConvertedRow> valid, List<RowIssue> issues, int duplicateCount) {

    public int invalidCount() {
        return issues.size() - duplicateCount;
    }
}

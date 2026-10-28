package com.trackNest.trackNest_App.members.imports;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** Runs a whole file through a mapping. Preview and commit both call this, so they cannot disagree. */
public final class ImportProcessor {

    private ImportProcessor() {
    }

    public static ImportPlan process(ParsedTable table, ImportMapping mapping, Set<String> existingKeys) {
        MemberRowConverter converter = new MemberRowConverter(mapping);
        DuplicateDetector duplicates = new DuplicateDetector(existingKeys);

        List<ConvertedRow> valid = new ArrayList<>();
        List<RowIssue> issues = new ArrayList<>();
        int duplicateCount = 0;

        for (ParsedRow row : table.rows()) {
            ConvertedRow converted = converter.convert(row);
            String name = converted.display().getOrDefault(MemberImportField.FULL_NAME.key(), "");

            if (converted.member() == null) {
                issues.add(new RowIssue(row.rowNumber(), name, String.join("; ", converted.problems()),
                        converted.missingFields(), false));
                continue;
            }
            String duplicateReason = duplicates.check(converted.member());
            if (duplicateReason != null) {
                issues.add(new RowIssue(row.rowNumber(), name, duplicateReason, List.of(), true));
                duplicateCount++;
                continue;
            }
            duplicates.register(converted.member(), row.rowNumber());
            valid.add(converted);
        }
        return new ImportPlan(table.rows().size(), valid, issues, duplicateCount);
    }
}

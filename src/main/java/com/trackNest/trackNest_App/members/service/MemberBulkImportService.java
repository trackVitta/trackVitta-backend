package com.trackNest.trackNest_App.members.service;

import com.trackNest.trackNest_App.common.globalExceptionHandler.exceptions.InvalidFileException;
import com.trackNest.trackNest_App.members.MemberStatus;
import com.trackNest.trackNest_App.members.MembershipPlan;
import com.trackNest.trackNest_App.members.dto.ImportAnalyzeResponseDto;
import com.trackNest.trackNest_App.members.dto.ImportColumnDto;
import com.trackNest.trackNest_App.members.dto.ImportFieldDto;
import com.trackNest.trackNest_App.members.dto.ImportPreviewResponseDto;
import com.trackNest.trackNest_App.members.dto.ImportPreviewRowDto;
import com.trackNest.trackNest_App.members.dto.RowErrorDto;
import com.trackNest.trackNest_App.members.imports.ConvertedRow;
import com.trackNest.trackNest_App.members.imports.FileTableReader;
import com.trackNest.trackNest_App.members.imports.ImportMapping;
import com.trackNest.trackNest_App.members.imports.ImportPlan;
import com.trackNest.trackNest_App.members.imports.ImportProcessor;
import com.trackNest.trackNest_App.members.imports.MemberImportField;
import com.trackNest.trackNest_App.members.imports.ParsedRow;
import com.trackNest.trackNest_App.members.imports.ParsedTable;
import com.trackNest.trackNest_App.members.imports.RowIssue;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * File validation, analysis (columns + suggested mapping) and preview. Holds no database access;
 * MemberService adds ownership checks and persistence around it.
 */
@Service
public class MemberBulkImportService {

    private static final long MAX_FILE_SIZE = 5L * 1024 * 1024;
    private static final int PREVIEW_SAMPLE_SIZE = 5;
    private static final int MAX_REPORTED_ERRORS = 200;
    private static final int PREVIEW_REPORTED_ERRORS = 50;

    /** Validates size / type / content and reads the file into a table. */
    public ParsedTable readFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("File cannot be null or empty");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidFileException("File size cannot exceed 5 MB");
        }
        String name = cleanFileName(file.getOriginalFilename());
        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new InvalidFileException("Could not read the uploaded file");
        }
        return FileTableReader.read(name, bytes);
    }

    public ImportAnalyzeResponseDto analyze(ParsedTable table) {
        List<ImportColumnDto> columns = new ArrayList<>();
        for (int i = 0; i < table.headers().size(); i++) {
            columns.add(new ImportColumnDto(i, table.headers().get(i), samples(table, i)));
        }
        List<ImportFieldDto> fields = new ArrayList<>();
        for (MemberImportField field : MemberImportField.values()) {
            fields.add(toFieldDto(field));
        }
        return new ImportAnalyzeResponseDto(table.fileName(), table.fileType(), table.rows().size(), columns, fields,
                MemberImportField.suggest(table.headers()));
    }

    public ImportMapping parseMapping(Map<String, String> params, ParsedTable table) {
        return ImportMapping.fromParams(params, table.headers());
    }

    public ImportPlan plan(ParsedTable table, ImportMapping mapping, Set<String> existingKeys) {
        return ImportProcessor.process(table, mapping, existingKeys);
    }

    public ImportPreviewResponseDto preview(ImportPlan plan, ImportMapping mapping) {
        List<ConvertedRow> pool = new ArrayList<>(plan.valid());
        Collections.shuffle(pool);
        List<ConvertedRow> picked = new ArrayList<>(pool.subList(0, Math.min(PREVIEW_SAMPLE_SIZE, pool.size())));
        picked.sort(Comparator.comparingInt(ConvertedRow::rowNumber));

        List<ImportPreviewRowDto> sampleRows = new ArrayList<>();
        for (ConvertedRow row : picked) {
            sampleRows.add(new ImportPreviewRowDto(row.rowNumber(), row.display()));
        }

        List<ImportFieldDto> mappedFields = new ArrayList<>();
        for (MemberImportField field : MemberImportField.values()) {
            boolean shown = mapping.columns().containsKey(field)
                    || mapping.defaults().containsKey(field)
                    || field == MemberImportField.STATUS;
            if (shown) {
                mappedFields.add(toFieldDto(field));
            }
        }

        List<RowErrorDto> errors = toRowErrors(plan.issues(), PREVIEW_REPORTED_ERRORS);
        return new ImportPreviewResponseDto(plan.totalRows(), plan.valid().size(), plan.invalidCount(),
                plan.duplicateCount(), mappedFields, sampleRows, errors,
                plan.issues().size() > PREVIEW_REPORTED_ERRORS, !plan.valid().isEmpty());
    }

    public List<RowErrorDto> toRowErrors(List<RowIssue> issues) {
        return toRowErrors(issues, MAX_REPORTED_ERRORS);
    }

    private List<RowErrorDto> toRowErrors(List<RowIssue> issues, int limit) {
        List<RowErrorDto> result = new ArrayList<>();
        for (RowIssue issue : issues) {
            if (result.size() >= limit) {
                break;
            }
            result.add(RowErrorDto.builder()
                    .rowNumber(issue.rowNumber())
                    .fullName(issue.fullName())
                    .reason(issue.reason())
                    .missingFields(issue.missingFields())
                    .build());
        }
        return result;
    }

    public boolean errorsTruncated(List<RowIssue> issues) {
        return issues.size() > MAX_REPORTED_ERRORS;
    }

    // ---- helpers -----------------------------------------------------------------------------

    private ImportFieldDto toFieldDto(MemberImportField field) {
        List<String> allowed = List.of();
        if (field.type() == MemberImportField.FieldType.PLAN) {
            allowed = Arrays.stream(MembershipPlan.values()).map(Enum::name).toList();
        } else if (field.type() == MemberImportField.FieldType.STATUS) {
            allowed = Arrays.stream(MemberStatus.values()).map(Enum::name).toList();
        }
        return new ImportFieldDto(field.key(), field.label(), field.required(), field.type().name(),
                field.allowsDefault(), allowed);
    }

    private List<String> samples(ParsedTable table, int column) {
        Set<String> found = new LinkedHashSet<>();
        int scanned = 0;
        for (ParsedRow row : table.rows()) {
            if (scanned++ >= 50 || found.size() >= 3) {
                break;
            }
            String value = row.cell(column);
            if (!value.isBlank()) {
                found.add(value.length() > 60 ? value.substring(0, 57) + "..." : value);
            }
        }
        return new ArrayList<>(found);
    }

    private String cleanFileName(String original) {
        if (original == null || original.isBlank()) {
            throw new InvalidFileException("The uploaded file has no name");
        }
        String name = original.substring(Math.max(original.lastIndexOf('/'), original.lastIndexOf('\\')) + 1);
        return name.length() > 120 ? name.substring(name.length() - 120) : name;
    }
}

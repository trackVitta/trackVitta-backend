package com.trackNest.trackNest_App.members.imports;

import com.trackNest.trackNest_App.common.globalExceptionHandler.exceptions.InvalidFileException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Turns an uploaded .xlsx or .csv into a ParsedTable (header + data rows). */
public final class FileTableReader {

    public static final int MAX_ROWS = 5000;
    public static final int MAX_COLUMNS = 100;

    private FileTableReader() {
    }

    public static ParsedTable read(String fileName, byte[] content) {
        String lower = fileName == null ? "" : fileName.toLowerCase(Locale.ROOT);
        List<ParsedRow> records;
        String type;

        if (lower.endsWith(".xlsx")) {
            // Real validation instead of trusting the browser's content-type: an .xlsx is a ZIP ("PK\3\4")
            if (content.length < 4 || content[0] != 'P' || content[1] != 'K' || content[2] != 3 || content[3] != 4) {
                throw new InvalidFileException("The file is not a valid .xlsx workbook");
            }
            type = "XLSX";
            records = XlsxTableReader.read(content);
        } else if (lower.endsWith(".csv")) {
            type = "CSV";
            records = CsvParser.read(content);
        } else {
            throw new InvalidFileException("Only .xlsx and .csv files are supported");
        }
        return build(fileName, type, records);
    }

    static ParsedTable build(String fileName, String fileType, List<ParsedRow> records) {
        if (records.isEmpty()) {
            throw new InvalidFileException("The file is empty");
        }
        ParsedRow headerRow = records.get(0);
        List<ParsedRow> data = records.subList(1, records.size());
        if (data.isEmpty()) {
            throw new InvalidFileException("The file has a header row but no data rows");
        }
        if (data.size() > MAX_ROWS) {
            throw new InvalidFileException(
                    "The file must contain between 1 and " + MAX_ROWS + " data rows (found " + data.size() + ")");
        }

        int columns = headerRow.cells().size();
        for (ParsedRow row : data) {
            columns = Math.max(columns, row.cells().size());
        }
        if (columns > MAX_COLUMNS) {
            throw new InvalidFileException("The file has too many columns (maximum " + MAX_COLUMNS + ")");
        }

        List<String> headers = new ArrayList<>();
        for (int i = 0; i < columns; i++) {
            String h = i < headerRow.cells().size() ? headerRow.cells().get(i) : "";
            headers.add(h.isBlank() ? "Column " + columnLetters(i) : h);
        }
        return new ParsedTable(fileName, fileType, List.copyOf(headers), List.copyOf(data));
    }

    static void trimTrailingBlanks(List<String> cells) {
        while (!cells.isEmpty() && cells.get(cells.size() - 1).isBlank()) {
            cells.remove(cells.size() - 1);
        }
    }

    static String columnLetters(int index) {
        StringBuilder sb = new StringBuilder();
        int n = index;
        while (n >= 0) {
            sb.insert(0, (char) ('A' + n % 26));
            n = n / 26 - 1;
        }
        return sb.toString();
    }
}

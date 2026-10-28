package com.trackNest.trackNest_App.members.imports;

import com.trackNest.trackNest_App.common.globalExceptionHandler.exceptions.InvalidFileException;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/** Small RFC 4180 CSV reader: quoted fields, escaped quotes, newlines inside quotes, , ; or tab delimiters. */
public final class CsvParser {

    private static final int MAX_RECORDS = 60_000;

    private CsvParser() {
    }

    /** Reads every non-blank record. Row numbers are 1-based record positions (blank lines count). */
    public static List<ParsedRow> read(byte[] content) {
        String text = decode(content);
        char delimiter = detectDelimiter(text);
        List<List<String>> records = parse(text, delimiter, MAX_RECORDS);

        List<ParsedRow> rows = new ArrayList<>();
        for (int i = 0; i < records.size(); i++) {
            List<String> cells = new ArrayList<>();
            for (String raw : records.get(i)) {
                cells.add(clean(raw));
            }
            FileTableReader.trimTrailingBlanks(cells);
            if (cells.isEmpty()) {
                continue;
            }
            rows.add(new ParsedRow(i + 1, cells));
        }
        return rows;
    }

    static String clean(String s) {
        return s.replace('\u00A0', ' ').replace("\uFEFF", "").trim();
    }

    static String decode(byte[] bytes) {
        for (byte b : bytes) {
            if (b == 0) {
                throw new InvalidFileException("This does not look like a plain-text CSV file");
            }
        }
        int offset = (bytes.length >= 3
                && (bytes[0] & 0xFF) == 0xEF && (bytes[1] & 0xFF) == 0xBB && (bytes[2] & 0xFF) == 0xBF) ? 3 : 0;
        CharsetDecoder utf8 = StandardCharsets.UTF_8.newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT);
        try {
            return utf8.decode(ByteBuffer.wrap(bytes, offset, bytes.length - offset)).toString();
        } catch (CharacterCodingException e) {
            // Excel's plain "CSV" save is Windows-1252, not UTF-8
            return new String(bytes, offset, bytes.length - offset, Charset.forName("windows-1252"));
        }
    }

    static char detectDelimiter(String text) {
        int commas = 0;
        int semicolons = 0;
        int tabs = 0;
        boolean inQuotes = false;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '"') {
                inQuotes = !inQuotes;
            } else if (!inQuotes) {
                if (ch == '\n' || ch == '\r') {
                    if (commas + semicolons + tabs > 0) {
                        break;
                    }
                } else if (ch == ',') {
                    commas++;
                } else if (ch == ';') {
                    semicolons++;
                } else if (ch == '\t') {
                    tabs++;
                }
            }
        }
        if (semicolons > commas && semicolons >= tabs) {
            return ';';
        }
        if (tabs > commas && tabs > semicolons) {
            return '\t';
        }
        return ',';
    }

    static List<List<String>> parse(String text, char delimiter, int maxRecords) {
        List<List<String>> records = new ArrayList<>();
        List<String> record = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;
        int n = text.length();

        for (int i = 0; i < n; i++) {
            char ch = text.charAt(i);
            if (inQuotes) {
                if (ch == '"') {
                    if (i + 1 < n && text.charAt(i + 1) == '"') {
                        field.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    field.append(ch);
                }
            } else if (ch == '"' && field.length() == 0) {
                inQuotes = true;
            } else if (ch == delimiter) {
                record.add(field.toString());
                field.setLength(0);
            } else if (ch == '\n' || ch == '\r') {
                if (ch == '\r' && i + 1 < n && text.charAt(i + 1) == '\n') {
                    i++;
                }
                record.add(field.toString());
                field.setLength(0);
                records.add(record);
                record = new ArrayList<>();
                if (records.size() > maxRecords) {
                    throw new InvalidFileException("The file has too many lines");
                }
            } else {
                field.append(ch);
            }
        }

        if (inQuotes) {
            throw new InvalidFileException("Malformed CSV: a quoted value is never closed");
        }
        if (field.length() > 0 || !record.isEmpty()) {
            record.add(field.toString());
            records.add(record);
        }
        return records;
    }
}

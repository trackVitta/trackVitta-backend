package com.trackNest.trackNest_App.members.imports;

import com.trackNest.trackNest_App.common.globalExceptionHandler.exceptions.InvalidFileException;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads the first worksheet of an .xlsx file into text rows.
 * The workbook is fully consumed inside the try block (the old code returned a Sheet from a closed workbook).
 */
final class XlsxTableReader {

    private static final int MAX_SCANNED_ROWS = 50_000;
    private static final int MAX_SCANNED_COLUMNS = 1_000;

    private XlsxTableReader() {
    }

    static List<ParsedRow> read(byte[] content) {
        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(content))) {
            if (workbook.getNumberOfSheets() == 0) {
                throw new InvalidFileException("The uploaded workbook contains no sheets");
            }
            Sheet sheet = workbook.getSheetAt(0);

            List<ParsedRow> records = new ArrayList<>();
            int scanned = 0;
            for (Row row : sheet) {
                if (++scanned > MAX_SCANNED_ROWS) {
                    throw new InvalidFileException("The worksheet has too many rows");
                }
                List<String> cells = readCells(row);
                if (cells.isEmpty()) {
                    continue;
                }
                if (records.size() >= FileTableReader.MAX_ROWS + 1) {
                    throw new InvalidFileException(
                            "The worksheet must contain between 1 and " + FileTableReader.MAX_ROWS + " data rows");
                }
                records.add(new ParsedRow(row.getRowNum() + 1, cells));
            }
            return records;
        } catch (InvalidFileException ex) {
            throw ex;
        } catch (EncryptedDocumentException ex) {
            throw new InvalidFileException("Password-protected workbooks are not supported");
        } catch (IOException | RuntimeException ex) {
            throw new InvalidFileException("Invalid or corrupted XLSX file");
        }
    }

    private static List<String> readCells(Row row) {
        int last = Math.min(Math.max(row.getLastCellNum(), 0), MAX_SCANNED_COLUMNS);
        List<String> cells = new ArrayList<>(last);
        for (int i = 0; i < last; i++) {
            cells.add(cellText(row.getCell(i)));
        }
        FileTableReader.trimTrailingBlanks(cells);
        return cells;
    }

    private static String cellText(Cell cell) {
        if (cell == null) {
            return "";
        }
        CellType type = cell.getCellType();
        if (type == CellType.FORMULA) {
            type = cell.getCachedFormulaResultType();
        }
        switch (type) {
            case STRING:
                return CsvParser.clean(cell.getStringCellValue());
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    LocalDateTime dateTime = cell.getLocalDateTimeCellValue();
                    return dateTime == null ? "" : dateTime.toLocalDate().toString();
                }
                return numberToText(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }

    /** 9876543210 stays 9876543210 (not 9.87654321E9), 12.50 becomes 12.5. */
    static String numberToText(double d) {
        if (Double.isNaN(d) || Double.isInfinite(d)) {
            return "";
        }
        if (d == Math.rint(d) && Math.abs(d) < 1e15) {
            return String.valueOf((long) d);
        }
        return BigDecimal.valueOf(d).stripTrailingZeros().toPlainString();
    }
}

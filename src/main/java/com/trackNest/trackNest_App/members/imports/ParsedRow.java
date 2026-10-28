package com.trackNest.trackNest_App.members.imports;

import java.util.List;

/** One non-blank data row of an uploaded file. rowNumber is the row number the user sees in Excel. */
public record ParsedRow(int rowNumber, List<String> cells) {

    public String cell(int index) {
        return index >= 0 && index < cells.size() ? cells.get(index) : "";
    }
}

package com.trackNest.trackNest_App.members.dto;

import java.util.List;

/** One column of the uploaded file, with a few example values so the user can recognise it. */
public record ImportColumnDto(int index, String name, List<String> samples) {
}

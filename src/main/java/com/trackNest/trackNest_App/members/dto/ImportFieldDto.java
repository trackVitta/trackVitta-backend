package com.trackNest.trackNest_App.members.dto;

import java.util.List;

/** One member field the user can map a column to. type is TEXT, DATE, PLAN or STATUS. */
public record ImportFieldDto(String key, String label, boolean required, String type, boolean allowsDefault,
                             List<String> allowedValues) {
}

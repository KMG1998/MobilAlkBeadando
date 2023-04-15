package com.example.mobilalk_vizora.formatters;

import java.time.format.DateTimeFormatter;

public final class DateFormatters {
    private DateFormatters(){
        throw new IllegalStateException("Cannot be instantiated");
    }

    private static DateTimeFormatter birthDateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    public static DateTimeFormatter getBirthDateFormatter() {
        return birthDateFormatter;
    }
}

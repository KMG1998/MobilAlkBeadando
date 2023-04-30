package com.example.mobilalk_vizora.formatters;

import java.text.SimpleDateFormat;

public final class DateFormatters {
    private DateFormatters(){
        throw new IllegalStateException("Cannot be instantiated");
    }

    private static SimpleDateFormat dateF =new SimpleDateFormat("yyyy.MM.dd");

    public static SimpleDateFormat getDateFormat() {
        return dateF;
    }
}

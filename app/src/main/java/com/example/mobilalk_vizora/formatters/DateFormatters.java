package com.example.mobilalk_vizora.formatters;

import java.text.SimpleDateFormat;

public final class DateFormatters {
    private DateFormatters(){
        throw new IllegalStateException("Cannot be instantiated");
    }

    private static SimpleDateFormat dateF =new SimpleDateFormat("yyyy.MM.dd");

    private static SimpleDateFormat timestampF = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

    public static SimpleDateFormat getDateFormat() {
        return dateF;
    }

    public static SimpleDateFormat getTimestampFormat() {
        return timestampF;
    }
}

package cz.uhk.seenit.utils;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Formatter {

    private static final Format shortDateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public static String formatDateShort(Date date) {
        return shortDateFormat.format(date);
    }
}
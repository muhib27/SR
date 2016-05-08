package com.champs21.sciencerocks.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by BLACK HAT on 08-May-16.
 */
public class AppUtils {

    public static final String DATE_FORMAT_SERVER = "yyyy-MM-dd";
    public static final String DATE_FORMAT_APP = "dd MMM yyyy";
    public static final String DATE_FORMAT_FACEBOOK = "MM/dd/yyyy";

    public static String getDateString(String str, String toFormat,
                                       String fromFormat) {
        Date parsed = null;
        String outputDate = "";

        SimpleDateFormat df_input = new SimpleDateFormat(fromFormat,
                java.util.Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat(toFormat,
                java.util.Locale.getDefault());

        try {
            parsed = df_input.parse(str);
            outputDate = df_output.format(parsed);

        } catch (ParseException e) {
            Log.e("ParseException", "ParseException - dateFormat");
        }
        Log.e("Formated Date", outputDate);
        return outputDate;
    }
}

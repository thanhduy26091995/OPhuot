package com.thanhduy.ophuot.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by buivu on 06/10/2016.
 */
public class EmailValidate {
    public static boolean IsOk(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }
}

package com.example.casttvandroiddemo.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    public static boolean isLetterOrDigit(char c){
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9';
    }

    public static boolean containsChinese(String str) {
        if (str == null) {
            return false;
        }
        Pattern pattern = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }
}

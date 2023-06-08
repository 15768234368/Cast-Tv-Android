package com.example.casttvandroiddemo.utils;

public class StringUtils {
    public static boolean isLetterOrDigit(char c){
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9';
    }
}

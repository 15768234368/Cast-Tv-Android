package com.example.casttvandroiddemo.utils;

import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;

public class ViewUtils {
    public static void setEditViewLimit(EditText et) {
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                // 遍历输入的字符
                StringBuilder filtered = new StringBuilder();
                for (int i = start; i < end; ++i) {
                    char c = source.charAt(i);
                    // 过滤非字母和数字字符
                    if (StringUtils.isLetterOrDigit(c)) {
                        filtered.append(c);
                    }
                }
                return filtered.toString();
            }
        };
        et.setFilters(new InputFilter[]{filter});
    }
}

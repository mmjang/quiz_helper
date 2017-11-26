package com.mmjang.quizhelper.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chenxiangjie on 2017/7/26.
 */

public class StringUtil {
    /**
     * 判断字符串是否为null或全为空白字符
     *
     * @param s 待校验字符串
     * @return {@code true}: null或全空白字符<br> {@code false}: 不为null且不全空白字符
     */
    public static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isSpace(char c) {
        return Character.isSpaceChar(c) || Character.isWhitespace(c);
    }

    public static String boldToBlank(String str){
            final String regex = "<b>.+?</b>";
            final Matcher m = Pattern.compile(regex).matcher(str);
            String copy = str;
            while (m.find()) {
                String bold = m.group(0);
                copy = copy.replace(bold, bold.substring(3, bold.length() - 3).replaceAll(".", "_"));
            }
            return copy;
        }
}

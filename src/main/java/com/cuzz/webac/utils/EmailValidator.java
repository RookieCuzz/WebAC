package com.cuzz.webac.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator {

    // 定义邮箱的正则表达式
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$";

    public static boolean isValidEmail(String email) {
        // 编译正则表达式
        Pattern pattern = Pattern.compile(EMAIL_REGEX);

        // 匹配输入的邮箱字符串
        Matcher matcher = pattern.matcher(email);

        // 返回是否匹配
        return matcher.matches();
    }

}

package com.xwq.spider.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class NumberUtil {
    private static final Set<String> numSet = new HashSet<>();

    static {
        String[] strs = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "."};
        numSet.addAll(Arrays.asList(strs));
    }

    public static Double getDoubleNumber(String input) {
        StringBuffer sb = new StringBuffer();

        for(char c : input.toCharArray()) {
            if(numSet.contains(c + "")) {
                sb.append(c);
            }
        }
        return Double.valueOf(sb.toString());
    }
}

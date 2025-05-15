package com.scenic.util;

public class MemberLevelUtil {
    public static int calculateLevel(int points) {
        if (points >= 10000) {
            return 3; // 钻石会员
        } else if (points >= 5000) {
            return 2; // 金卡会员
        } else if (points >= 1000) {
            return 1; // 银卡会员
        } else {
            return 0; // 普通会员
        }
    }
} 
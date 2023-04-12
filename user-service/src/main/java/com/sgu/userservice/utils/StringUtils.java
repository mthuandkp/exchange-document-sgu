package com.sgu.userservice.utils;

import java.util.concurrent.ThreadLocalRandom;

public class StringUtils {

    public static String createOTP() {
        Long min = 100000L,max = 999999L;
        Long otpCode = ThreadLocalRandom.current().nextLong(min, max + 1);

        return String.valueOf(otpCode);
    }
}

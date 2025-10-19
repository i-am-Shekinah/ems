package com.encentral.scaffold.commons.utils;

import org.mindrot.jbcrypt.BCrypt;

public class SecurityUtil {
    public static String hashPassword(String plain) {
        return BCrypt.hashpw(plain, BCrypt.gensalt());
    }

    public static boolean verifyPassword(String plain, String hash) {
        return BCrypt.checkpw(plain, hash);
    }

    public static String generate4DigitPin() {
        int pin = (int)(Math.random() * 9000) + 1000;
        return String.valueOf(pin);
    }
}

package net.mmorgenstern.gh;

import java.security.MessageDigest;

public class StringUtils {
    public static boolean constantTimeCompare(String a, String b) {
        return MessageDigest.isEqual(a.getBytes(), b.getBytes());
    }
}

package com.stframework.core;

import java.util.Base64;

public class Util {

    public static String encode(String str) {
        return Base64.getEncoder().encodeToString(str.getBytes());
    }

    public static String decode(String b64) {
        return new String(Base64.getDecoder().decode(b64.getBytes()));
    }

    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static Object assertClass(Object obj, Class<?> clazz) {
        if (obj.getClass() != clazz) {
            return null;
        }
        return obj;
    }
}

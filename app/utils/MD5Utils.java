package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import play.Logger;

public class MD5Utils {

    /**
     * MD5加密
     * 
     * @param str
     * @return
     */
    public static String md5(String str) {
        String s = str;
        if (s == null) {
            return "";
        } else {
            String value = null;
            MessageDigest md5 = null;
            try {
                md5 = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException ex) {
                Logger.info(ex.getMessage());
            }
            sun.misc.BASE64Encoder baseEncoder = new sun.misc.BASE64Encoder();
            try {
                value = baseEncoder.encode(md5.digest(s.getBytes("utf-8")));
            } catch (Exception ex) {

            }
            return value;
        }
    }
}

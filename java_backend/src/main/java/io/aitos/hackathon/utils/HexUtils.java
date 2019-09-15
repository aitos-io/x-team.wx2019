package io.aitos.hackathon.utils;

import static org.web3j.utils.Numeric.cleanHexPrefix;

public class HexUtils {

    private static final String HEX_PREFIX = "0x";

    public static String bytesToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0, len = bytes.length; i < len; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public static String byteToHexString(byte b) {
        StringBuffer sb = new StringBuffer();
        String hex = Integer.toHexString(b & 0xFF);
        if (hex.length() < 2) {
            sb.append(0);
        }
        sb.append(hex);
        return sb.toString();
    }

    public static byte[] hexStringToByteArray(String input) {
        String cleanInput = cleanHexPrefix(input);

        int len = cleanInput.length();

        if (len == 0) {
            return new byte[] {};
        }

        byte[] data;
        int startIdx;
        if (len % 2 != 0) {
            data = new byte[(len / 2) + 1];
            data[0] = (byte) Character.digit(cleanInput.charAt(0), 16);
            startIdx = 1;
        } else {
            data = new byte[len / 2];
            startIdx = 0;
        }

        for (int i = startIdx; i < len; i += 2) {
            data[(i + 1) / 2] = (byte) ((Character.digit(cleanInput.charAt(i), 16) << 4)
                    + Character.digit(cleanInput.charAt(i + 1), 16));
        }
        return data;
    }

    private static String prependHexPrefix(String input) {
        if (!containsHexPrefix(input)) {
            return HEX_PREFIX + input;
        } else {
            return input;
        }
    }

    private static boolean containsHexPrefix(String input) {
        return !isEmpty(input) && input.length() > 1
                && input.charAt(0) == '0' && input.charAt(1) == 'x';
    }

    private static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

}

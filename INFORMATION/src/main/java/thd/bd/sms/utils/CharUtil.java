package thd.bd.sms.utils;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

public class CharUtil {
    private static String hexString = "0123456789ABCDEF";

    public CharUtil() {
    }

    public static void main(String[] args) {
        String[] strArr = new String[]{"www.micmiu.com", "!@#$%^&*()_+{}[]|\"'?/:;<>,.", "！￥……（）——：；“”‘’《》，。？、", "不要啊", "やめて", "韩佳人", "???"};
        String[] var2 = strArr;
        int var3 = strArr.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            String str = var2[var4];
            System.out.println("===========> 测试字符串：" + str);
            System.out.println("正则判断结果：" + isChineseByREG(str) + " -- " + isChineseByName(str));
            System.out.println("Unicode判断结果 ：" + isChinese(str));
            System.out.println("详细判断列表：");
            char[] ch = str.toCharArray();

            for(int i = 0; i < ch.length; ++i) {
                char c = ch[i];
                System.out.println(c + " --> " + (isChinese(c) ? "是" : "否"));
            }
        }

    }

    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION;
    }

    public static boolean isChinese(String strName) {
        char[] ch = strName.toCharArray();

        for(int i = 0; i < ch.length; ++i) {
            char c = ch[i];
            if (!isChinese(c)) {
                return false;
            }
        }

        return true;
    }

    public static boolean isChineseByREG(String str) {
        if (str == null) {
            return false;
        } else {
            Pattern pattern = Pattern.compile("[\\u4E00-\\u9FBF]+");
            return pattern.matcher(str.trim()).find();
        }
    }

    public static boolean isChineseByName(String str) {
        if (str == null) {
            return false;
        } else {
            String reg = "\\p{InCJK Unified Ideographs}&&\\P{Cn}";
            Pattern pattern = Pattern.compile(reg);
            return pattern.matcher(str.trim()).find();
        }
    }

    public static boolean isNumeric1(String str) {
        int i = str.length();

        do {
            --i;
            if (i < 0) {
                return true;
            }
        } while(Character.isDigit(str.charAt(i)));

        return false;
    }

    public static boolean isNumeric2(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    public static boolean isNumeric3(String str) {
        int i = str.length();

        char chr;
        do {
            --i;
            if (i < 0) {
                return true;
            }

            chr = str.charAt(i);
        } while(chr >= '0' && chr <= '9');

        return false;
    }

    private static byte toByte(char c) {
        byte b = (byte)hexString.indexOf(c);
        return b;
    }

    public static byte[] hexStringToByte(String hex) {
        int len = hex.length() / 2;
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();

        for(int i = 0; i < len; ++i) {
            int pos = i * 2;
            result[i] = (byte)(toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }

        return result;
    }

    public static final String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);

        for(int i = 0; i < bArray.length; ++i) {
            String sTemp = Integer.toHexString(255 & bArray[i]);
            if (sTemp.length() < 2) {
                sb.append(0);
            }

            sb.append(sTemp.toUpperCase());
        }

        return sb.toString();
    }

    public static String encode(String str) {
        byte[] bytes = null;

        try {
            bytes = str.getBytes("GBK");
        } catch (UnsupportedEncodingException var4) {
            var4.printStackTrace();
        }

        StringBuilder sb = new StringBuilder(bytes.length * 2);

        for(int i = 0; i < bytes.length; ++i) {
            sb.append(hexString.charAt((bytes[i] & 240) >> 4));
            sb.append(hexString.charAt((bytes[i] & 15) >> 0));
        }

        return sb.toString();
    }

    public static String decode(String bytes) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length() / 2);

        for(int i = 0; i < bytes.length(); i += 2) {
            baos.write(hexString.indexOf(bytes.charAt(i)) << 4 | hexString.indexOf(bytes.charAt(i + 1)));
        }

        String str = "";

        try {
            str = new String(baos.toByteArray(), "GBK");
        } catch (UnsupportedEncodingException var4) {
            var4.printStackTrace();
        }

        return str;
    }
}

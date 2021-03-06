package xyzs.hy.com.xyzs.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用于验证手机号是否合法
 * 开通13全号段，14去除5和7,15去除4,17全号段，18全号段
 */
public class IsPhone {

    public static boolean isPhone(String phone) {
        Pattern p = Pattern.compile("^((13[0-9])|(14[5,7])|(15[^4\\D])|(17[0-9])|(18[0-9]))\\d{8}$");
        Matcher m = p.matcher(phone);
        return m.matches();
    }

}

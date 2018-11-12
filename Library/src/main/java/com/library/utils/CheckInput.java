package com.library.utils;

import android.support.annotation.Nullable;

/**
 * Created by Administrator on 2016/6/3.
 */
public class CheckInput {
    /**
     * 手机号码
     *
     * @param phoneNum 手机号码
     * @return 信息
     */
    @Nullable
    public static String checkPhoneNum(String phoneNum) {
        String compare = "1[3|4|5|7|8][0-9]{9}";
        if (phoneNum.equals("")) {
            return "请输入正确的手机号";
        } else if (phoneNum.length() != 11) {
            return "请输入正确的手机号";
        } else if (!phoneNum.matches(compare)) {
            return "请输入正确的手机号";
        }
        return null;
    }

    public static String checkPassNum(String phoneNum) {
        if (phoneNum == null || phoneNum.isEmpty() || phoneNum.length() < 6 || phoneNum.length() > 18) {
            return "请填写6-18位密码";
        }
        return null;
    }

    public static String checkIdeCode(String ideCode) {
        if (ideCode == null || ideCode.isEmpty() || ideCode.length() != 6) {
            return "请填写6位验证码";
        }
        return null;
    }

    public static String checkComName(String comName) {
        if (comName == null || comName.isEmpty()) {
            return "请填写组织名称";
        }
        return null;
    }

    public static String checkRealName(String comName) {
        if (comName == null || comName.isEmpty()) {
            return "请填写昵称";
        }
        return null;
    }
}

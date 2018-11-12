package com.library.utils;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Yangzb on 2017/7/7 11:02
 * E-mail：yangzongbin@si-top.com
 * 检测url是否有效
 */
public class UrlUtil {
    /**
     * 检查url是否有效
     *
     * @param murl
     * @return
     */
    public static boolean checkURL(String murl) {
        boolean value = false;
        URL url = null;
        HttpURLConnection httpURLConnection = null;
        OutputStream outStream = null;
        try {
            url = new URL(murl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            outStream = httpURLConnection.getOutputStream();
            value = true;
        } catch (Exception e) {
            url = null;
            value = false;
        }
        return value;
    }
}

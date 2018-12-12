package com.example.xty.ndkdemo;

/**
 * Created by xty on 18-11-22.
 */

public class java2c {
    static {
        System.loadLibrary("switch");
    }

    public native String getResult(String image_path);
}

package com.arr.simple.nauta.utils;

import android.graphics.Bitmap;

public interface CaptchaCallback {

    void loadCaptcha(Bitmap bitmap);

    void handlerException(Exception e);
}

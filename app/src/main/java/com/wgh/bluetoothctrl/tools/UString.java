package com.wgh.bluetoothctrl.tools;

import android.content.Intent;

/**
 * Created by WGH on 2017/4/15.
 */

public class UString {

    public static String trimCarriageReturn(String string) {
        return string.replaceAll("\r|\n", "").trim();
    }

    public static String getStringFromByteArray(byte[] bytes) {
        return new String(bytes);
    }
}

package com.wgh.bluetoothctrl.tools;

import android.util.Log;

/**
 * Created by WGH on 2017/4/10.
 */

public class ULog {

    private static final int LOG_LEVEL = 6;// can be set to 0 if you want to block all the log.
    private static final int VERBOSE = 5;
    private static final int DEBUG = 4;
    private static final int INFO = 3;
    private static final int WARN = 2;
    private static final int ERROR = 1;
    private static final String TAGWARN = "BLUETOOTHCTRL_WARN";
    private static final String TAGERROR = "BLUETOOTHCTRL_ERROR";

    public static void v(String tag, String msg) {
        if (LOG_LEVEL > VERBOSE) {
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (LOG_LEVEL > DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (LOG_LEVEL > INFO) {
            Log.i(tag, msg);
        }

    }

    public static void w(String tag, String msg) {
        if (LOG_LEVEL > WARN) {
            Log.i(TAGWARN + tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (LOG_LEVEL > ERROR) {
            Log.e(TAGERROR + tag, msg);
        }
    }

    public static void v(String msg) {
        if (LOG_LEVEL > VERBOSE) {
            Log.v(getCallerName(), msg);
        }
    }

    public static void d(String msg) {
        if (LOG_LEVEL > DEBUG) {
            Log.d(getCallerName(), msg);
        }
    }

    public static void i(String msg) {
        if (LOG_LEVEL > INFO) {
            Log.i(getCallerName(), msg);
        }
    }

    public static void w(String msg) {
        if (LOG_LEVEL > WARN) {
            Log.w(TAGWARN + getCallerName(), msg);
        }
    }

    public static void e(String msg) {
        if (LOG_LEVEL > ERROR) {
            Log.e(TAGERROR + getCallerName(), msg);
        }
    }

    /**
     * Get the caller's class name.
     *
     * @return
     */
    private static String getCallerName() {
        StackTraceElement caller = Thread.currentThread().getStackTrace()[4];
        String className = caller.getClassName();
        className = className.substring(className.lastIndexOf(".") + 1);

        return className;
    }
}

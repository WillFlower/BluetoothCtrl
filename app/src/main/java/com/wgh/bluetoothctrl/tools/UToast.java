package com.wgh.bluetoothctrl.tools;

import android.widget.Toast;

import com.wgh.bluetoothctrl.global.MyApplication;

/**
 * Created by WGH on 2017/4/10.
 */

public class UToast {
    private static Toast toast = null;

    public static void showMsg(String msg) {
        try {
            if (toast == null) {
                toast = Toast.makeText(MyApplication.context(), msg, Toast.LENGTH_SHORT);
            } else {
                toast.setText(msg);
            }
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showMsg(int id) {
        try {
            if (toast == null) {
                toast = Toast.makeText(MyApplication.context(), id, Toast.LENGTH_SHORT);
            } else {
                toast.setText(id);
            }
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showMsgLong(String msg) {
        try {
            if (toast == null) {
                toast = Toast.makeText(MyApplication.context(), msg, Toast.LENGTH_LONG);
            } else {
                toast.setText(msg);
            }
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showMsgLong(int id) {
        try {
            if (toast == null) {
                toast = Toast.makeText(MyApplication.context(), id, Toast.LENGTH_LONG);
            } else {
                toast.setText(id);
            }
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

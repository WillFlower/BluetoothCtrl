package com.wgh.bluetoothctrl.tools;

import android.content.SharedPreferences;
import com.wgh.bluetoothctrl.global.MyApplication;

/**
 * Created by WGH on 2017/4/10.
 */

public class USP {
    private static final String NAME = "UtilSharedPreference";
    private static final String ENABLEBLUETOOTHAUTOFLAG = "enablebluetoothautoflag";
    private static final String CONNECTBLUETOOTHAUTOFLAG = "connectbluetoothautoflag";
    private static final String BLUETOOTH_ADDRESS = "bluetooth_address";
    private static final String BLUETOOTH_NAME = "bluetooth_name";
    private static final String LOGFILEINDEX = "logfileindex";
    private static SharedPreferences.Editor editor = null;
    private static SharedPreferences preferences = null;
    private static USP usp = null;

    public static USP getInstance() {
        if (usp == null) {
            usp = new USP();
            preferences = MyApplication.context().getSharedPreferences(NAME, MyApplication.context().MODE_PRIVATE);
            editor = preferences.edit();
        }
        return usp;
    }

    public void setEnableBluetoothAutoFlag(boolean flag) {
        editor.putBoolean(ENABLEBLUETOOTHAUTOFLAG, flag).commit();
    }

    public boolean getEnableBluetoothAutoFlag() {
        return preferences.getBoolean(ENABLEBLUETOOTHAUTOFLAG, false);
    }

    public void setConnectBluetoothAutoFlag(boolean flag) {
        editor.putBoolean(CONNECTBLUETOOTHAUTOFLAG, flag).commit();
    }

    public boolean getConnectBluetoothAutoFlag() {
        return preferences.getBoolean(CONNECTBLUETOOTHAUTOFLAG, false);
    }

    public void setBluetoothAddress(String address) {
        editor.putString(BLUETOOTH_ADDRESS, address).commit();
    }

    public String getBluetoothAddress() {
        return preferences.getString(BLUETOOTH_ADDRESS, "");
    }

    public void setBluetoothName(String name) {
        editor.putString(BLUETOOTH_NAME, name).commit();
    }

    public String getBluetoothName() {
        return preferences.getString(BLUETOOTH_NAME, "");
    }

    public void setLogFileIndex(int index) {
        editor.putInt(LOGFILEINDEX, index).commit();
    }

    public int getLogFileIndex() {
        return preferences.getInt(LOGFILEINDEX, 1);
    }
}

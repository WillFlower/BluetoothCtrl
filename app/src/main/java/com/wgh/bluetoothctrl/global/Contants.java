package com.wgh.bluetoothctrl.global;

import com.wgh.bluetoothctrl.tools.USP;

/**
 * Created by WGH on 2017/4/10.
 */

public class Contants {

    public static final double scalingValueScanActivity = 0.96;

    public static boolean isBluetoothAutoEnable() {
        return USP.getInstance().getEnableBluetoothAutoFlag();
    }

    public static void isBluetoothAutoEnable(boolean flag) {
        USP.getInstance().setEnableBluetoothAutoFlag(flag);
    }

    public static boolean isBluetoothAutoConnect() {
        return USP.getInstance().getConnectBluetoothAutoFlag();
    }

    public static void isBluetoothAutoConnect(boolean flag) {
        USP.getInstance().setConnectBluetoothAutoFlag(flag);
    }

    public static String bluetoothAddress() {
        return USP.getInstance().getBluetoothAddress();
    }

    public static void bluetoothAddress(String address) {
        USP.getInstance().setBluetoothAddress(address);
    }

    public static String bluetoothName() {
        return USP.getInstance().getBluetoothName();
    }

    public static void bluetoothName(String address) {
        USP.getInstance().setBluetoothName(address);
    }

    public static int logFileIndex() {
        return USP.getInstance().getLogFileIndex();
    }

    public static void logFileIndex(int index) {
        USP.getInstance().setLogFileIndex(index);
    }
}

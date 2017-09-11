package com.wgh.bluetoothctrl.bean;

/**
 * Created by WGH on 2017/4/10.
 */

public class BluetoothDevice {
    private String deviceName;
    private String deviceAddress;
    private String BlueToothClass;
    private String BondState;
    private String deviceType;
    private String deviceUuid;
    private String deviceClass;

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public void setBlueToothClass(String blueToothClass) {
        BlueToothClass = blueToothClass;
    }

    public void setBondState(String bondState) {
        BondState = bondState;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public void setDeviceUuid(String deviceUuid) {
        this.deviceUuid = deviceUuid;
    }

    public void setDeviceClass(String deviceClass) {
        this.deviceClass = deviceClass;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public String getBlueToothClass() {
        return BlueToothClass;
    }

    public String getBondState() {
        return BondState;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public String getDeviceUuid() {
        return deviceUuid;
    }

    public String getDeviceClass() {
        return deviceClass;
    }
}

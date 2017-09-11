package com.wgh.bluetoothctrl.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.wgh.bluetoothctrl.tools.ULog;

import java.util.List;
import java.util.UUID;

/**
 * Created by WGH on 2017/4/10.
 */

public class BluetoothLeService extends Service {
    public final static String ACTION_GATT_CONNECTING = "action_gatt_connecting";
    public final static String ACTION_GATT_CONNECTED = "action_gatt_connected";
    public final static String ACTION_GATT_DISCONNECTED = "action_gatt_disconnected";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "action_gatt_services_discovered";
    public final static String ACTION_DATA_AVAILABLE = "action_data_available";
    public final static String EXTRA_DATA = "extra_data";

    // 用于数据接收、发送的service和character对应的UUID,由该ble透传模块决定
    public final static UUID UUID_NOTIFY =
            UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
    private static final UUID CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID =
            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    // 用于数据接收、发送的character,由该ble透传模块决定
    public BluetoothGattCharacteristic mNotifyCharacteristic;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;

    private final IBinder mBinder = new LocalBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return  mBinder;
    }

    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public boolean initialize() {
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                ULog.e("Unable to initialize BluetoothManager.");
                return false;
            }
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            ULog.e("Unable to obtain a BluetoothAdapter.");
            return false;
        }
        ULog.i("Initialize BluetoothLeService success!");
        return true;
    }

    public boolean connect(String address) {
        if (mBluetoothAdapter == null || address == null) {
            ULog.e("BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            ULog.i("Trying to use an existing mBluetoothGatt for connection.");
            return mBluetoothGatt.connect();
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            ULog.e("Device not found,Unable to connect.");
            return false;
        }
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        ULog.i("Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        return true;
    }

    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            ULog.e("BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    public void close() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
            mBluetoothGatt = null;;
        }
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                broadcastUpdate(ACTION_GATT_CONNECTED);
                ULog.i("Connected to GATT server.");
                mBluetoothGatt.discoverServices();
                ULog.i("Attempting to start service discovery:");
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                broadcastUpdate(ACTION_GATT_DISCONNECTED);
                ULog.w("Disconnected from GATT server.");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                ULog.e("onServicesDiscovered received : " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                ULog.i("onCharacteristicRead()");
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            ULog.i("onCharacteristicChanged()");
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            ULog.i("onCharacteristicWrite()");
            super.onCharacteristicWrite(gatt, characteristic, status);
        }
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        final byte[] data = characteristic.getValue();
        intent.putExtra(EXTRA_DATA, data);
        sendBroadcast(intent);
    }

    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) {
            return null;
        }

        List<BluetoothGattService> gattServices = mBluetoothGatt.getServices();

        for (BluetoothGattService gattService : gattServices) {
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                String uuid = gattCharacteristic.getUuid().toString();
                if(uuid.equalsIgnoreCase(UUID_NOTIFY.toString())){
                    mNotifyCharacteristic = gattCharacteristic;
                    mBluetoothGatt.setCharacteristicNotification(gattCharacteristic, true);
                    ULog.i("setCharacteristicNotification : " + uuid);
                    BluetoothGattDescriptor descriptor = gattCharacteristic
                            .getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    mBluetoothGatt.writeDescriptor(descriptor);
                }
            }
        }
        return gattServices;
    }

    public void writeCharacteristic(byte[] data) {
        mNotifyCharacteristic.setValue(data);
        mBluetoothGatt.writeCharacteristic(mNotifyCharacteristic);
    }
}

package com.wgh.bluetoothctrl.view;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.wgh.bluetoothctrl.R;
import com.wgh.bluetoothctrl.adapter.MyBluetoothDeviceAdapter;
import com.wgh.bluetoothctrl.engine.BluetoothScan;
import com.wgh.bluetoothctrl.global.Contants;
import com.wgh.bluetoothctrl.tools.ULog;
import com.wgh.bluetoothctrl.tools.UToast;
import com.wgh.bluetoothctrl.tools.UUi;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WGH on 2017/4/10.
 */

public class BluetoothScanActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 1000 * 10;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private MyBluetoothDeviceAdapter mBluetoothDeviceAdapter;
    private List<BluetoothDevice> mBluetoothDeviceList = new ArrayList<>();
    private MyBluetoothScanCallBack mBluetoothScanCallBack = new MyBluetoothScanCallBack();
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_scan);
        initView();
        initData();
        scanLeDevice(true);
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        UUi.getInstance(this).setWindowHeight((int) (UUi.getInstance(this).getWindowHeight()
                * Contants.scalingValueScanActivity));
        UUi.getInstance(this).setWindowWidth((int) (UUi.getInstance(this).getWindowWidth()
                * Contants.scalingValueScanActivity));
    }

    private void initData() {
        mHandler = new Handler();
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        mBluetoothDeviceAdapter = new MyBluetoothDeviceAdapter(mBluetoothDeviceList);
        recyclerView.setAdapter(mBluetoothDeviceAdapter);

        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mBluetoothDeviceList != null) {
                    mBluetoothDeviceList.clear();
                }
                scanLeDevice(true);
            }
        });


    }

    private void scanLeDevice(boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    swipeRefresh.setRefreshing(false);
                    BluetoothScan.stopScan();
                }
            }, SCAN_PERIOD);
            swipeRefresh.setRefreshing(true);
            BluetoothScan.startScan(Contants.isBluetoothAutoEnable(), mBluetoothScanCallBack);
        } else {
            swipeRefresh.setRefreshing(false);
            BluetoothScan.stopScan();
        }
    }

    private class MyBluetoothScanCallBack implements BluetoothScan.BluetoothScanCallBack {
        @Override
        public void onLeScanInitFailure(int failureCode) {
            ULog.i("onLeScanInitFailure()");
            switch (failureCode) {
                case BluetoothScan.SCAN_FEATURE_ERROR :
                    UToast.showMsg(R.string.scan_feature_error);
                    break;
                case BluetoothScan.SCAN_ADAPTER_ERROR :
                    UToast.showMsg(R.string.scan_adapter_error);
                    break;
                default:
                    UToast.showMsg(R.string.unKnow_error);
            }
        }

        @Override
        public void onLeScanInitSuccess(int successCode) {
            ULog.i("onLeScanInitSuccess()");
            switch (successCode) {
                case BluetoothScan.SCAN_BEGIN_SCAN :
                    ULog.i("successCode : " + successCode);
                    break;
                case BluetoothScan.SCAN_NEED_ENADLE :
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    break;
                case BluetoothScan.AUTO_ENABLE_FAILURE :
                    UToast.showMsg(R.string.auto_enable_bluetooth_error);
                    break;
                default:
                    UToast.showMsg(R.string.unKnow_error);
            }
        }

        @Override
        public void onLeScanResult(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if(!mBluetoothDeviceList.contains(device) && device != null) {
                mBluetoothDeviceList.add(device);
                mBluetoothDeviceAdapter.notifyDataSetChanged();
                ULog.i("notifyDataSetChanged() " + "BluetoothName :　" + device.getName() +
                        "  BluetoothAddress :　" + device.getAddress());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_CANCELED) {
                UToast.showMsg(R.string.enable_bluetooth_error);
                return;
            } else if (resultCode == Activity.RESULT_OK) {
                if (mBluetoothDeviceList != null) {
                    mBluetoothDeviceList.clear();
                }
                scanLeDevice(true);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

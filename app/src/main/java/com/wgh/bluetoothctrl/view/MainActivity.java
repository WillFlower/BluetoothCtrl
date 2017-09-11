package com.wgh.bluetoothctrl.view;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wgh.bluetoothctrl.R;
import com.wgh.bluetoothctrl.global.Contants;
import com.wgh.bluetoothctrl.service.BluetoothLeService;
import com.wgh.bluetoothctrl.tools.LogFileHelper;
import com.wgh.bluetoothctrl.tools.ULog;
import com.wgh.bluetoothctrl.tools.UString;
import com.wgh.bluetoothctrl.tools.UToast;

/**
 * Created by WGH on 2017/4/10.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CONNECT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 2;
    public static final String EXTRAS_DEVICE_NAME = "extras_device_name";
    public static final String EXTRAS_DEVICE_ADDRESS = "extras_device_address";
    private String mConnectionState = BluetoothLeService.ACTION_GATT_DISCONNECTED;

    private DrawerLayout mDrawerLayout;
    private BluetoothLeService mBluetoothLeService;
    private EditText mEditTextSend;
    private TextView mTextViewReceive;
    private Button mButtonSend;
    private String mDeviceName;
    private String mDeviceAddress;
    private View mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LogFileHelper.getInstance().startCat();

        Contants.isBluetoothAutoEnable(false);// Bluetooth Auto Enable.
        Contants.isBluetoothAutoConnect(true);// Bluetooth Auto Connect.

        initView();
        requestPermission();
        initService();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initReceiver();
    }

    private void initView() {
        mProgressBar = findViewById(R.id.progressBar);
        mTextViewReceive = (TextView) findViewById(R.id.textViewReceive);
        mEditTextSend = (EditText) findViewById(R.id.etSend);
        mButtonSend = (Button) findViewById(R.id.buttonSend);
        mButtonSend.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        navView.setCheckedItem(R.id.nav_call);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBluetoothLeService != null &&
                        mConnectionState.equals(BluetoothLeService.ACTION_GATT_CONNECTED)) {
                    mBluetoothLeService.disconnect();
                }
                Intent intent = new Intent(MainActivity.this, BluetoothScanActivity.class);
                startActivityForResult(intent, REQUEST_CONNECT);
            }
        });
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check.
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_REQUEST_COARSE_LOCATION);
            }
        }
    }

    private void initService() {
        ULog.i("initService()");
        if (mBluetoothLeService == null) {
            Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
            bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        } else {
            autoConnect();
        }
    }

    private void initReceiver() {
        ULog.i("initReceiver()");
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ULog.i("onRequestPermissionsResult() Success!");
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CONNECT) {
            if (resultCode == Activity.RESULT_OK) {
                mDeviceName = UString.trimCarriageReturn(data.getExtras().
                        getString(EXTRAS_DEVICE_NAME));
                mDeviceAddress = UString.trimCarriageReturn(data.getExtras().
                        getString(EXTRAS_DEVICE_ADDRESS));
                ULog.i("Attempt to connect device : " + mDeviceName + "(" + mDeviceAddress + ")");
                if (mBluetoothLeService != null) {
                    if (mBluetoothLeService.connect(mDeviceAddress)) {
                        mConnectionState = BluetoothLeService.ACTION_GATT_CONNECTING;
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                ULog.e("Unable to initialize Bluetooth");
            } else {
                autoConnect();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };


    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                ULog.i("ACTION_GATT_CONNECTED!!!");
                mConnectionState = BluetoothLeService.ACTION_GATT_CONNECTED;
                mProgressBar.setVisibility(View.INVISIBLE);
                if (!TextUtils.isEmpty(mDeviceAddress)) {
                    Contants.bluetoothAddress(mDeviceAddress);
                    if (!TextUtils.isEmpty(mDeviceName)) {
                        Contants.bluetoothName(mDeviceName);
                    } else {
                        Contants.bluetoothName(getString(R.string.device_unknown));
                    }
                }
                UToast.showMsg(getString(R.string.connected_success) + "\n" + Contants.bluetoothName());
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                ULog.i("ACTION_GATT_DISCONNECTED!!!");
                UToast.showMsg(R.string.disconnected_error);
                mConnectionState = BluetoothLeService.ACTION_GATT_DISCONNECTED;
                mProgressBar.setVisibility(View.INVISIBLE);
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                mBluetoothLeService.getSupportedGattServices();
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                final byte[] data = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                mTextViewReceive.append(UString.getStringFromByteArray(data));
                ULog.i("Get string : " + new String(data));
                if (data != null && data.length > 0) {
                    final StringBuilder stringBuilder = new StringBuilder(data.length);
                    for (byte byteChar : data) {
                        stringBuilder.append(String.format("%02X ", byteChar));
                    }
                    ULog.i("Get string(ASCII) : " + stringBuilder.toString());
                }
            }
        }
    };

    private void autoConnect() {
        if (Contants.isBluetoothAutoConnect() &&
                !TextUtils.isEmpty(Contants.bluetoothAddress())) {
            if (mBluetoothLeService.connect(Contants.bluetoothAddress())) {
                mProgressBar.setVisibility(View.VISIBLE);
                ULog.i("Bluetooth autoConnecting!");
            } else {
                ULog.i("Bluetooth autoConnect error!");
                UToast.showMsg(R.string.auto_connected_error);
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.backup:
                Toast.makeText(this, "You clicked menu1", Toast.LENGTH_SHORT).show();
                break;
            case R.id.delete:
                Toast.makeText(this, "You clicked menu2", Toast.LENGTH_SHORT).show();
                break;
            case R.id.settings:
                Toast.makeText(this, "You clicked menu3", Toast.LENGTH_SHORT).show();
                break;
            default:
        }
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        ULog.i("unregisterReceiver()");
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothLeService != null) {
            mBluetoothLeService.disconnect();
            mBluetoothLeService.close();
            unbindService(mServiceConnection);
            mBluetoothLeService = null;
            ULog.i("unbindService()");
        }
        LogFileHelper.getInstance().stopCat();
    }

    public void btSendBytes(byte[] data) {
        if (mBluetoothLeService != null &&
                mConnectionState.equals(BluetoothLeService.ACTION_GATT_CONNECTED)) {
            mBluetoothLeService.writeCharacteristic(data);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonSend:
                String sendText = mEditTextSend.getText().toString();
                if (!TextUtils.isEmpty(sendText)) {
                    btSendBytes(sendText.getBytes());
                } else {
                    UToast.showMsg(R.string.send_empty_error);
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ULog.i("KEYCODE_BACK");
        }
        return super.onKeyDown(keyCode, event);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}

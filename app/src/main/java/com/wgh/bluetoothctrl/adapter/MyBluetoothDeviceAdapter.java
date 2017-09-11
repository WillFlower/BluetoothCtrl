package com.wgh.bluetoothctrl.adapter;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wgh.bluetoothctrl.R;
import com.wgh.bluetoothctrl.engine.BluetoothScan;
import com.wgh.bluetoothctrl.global.Contants;
import com.wgh.bluetoothctrl.tools.ULog;
import com.wgh.bluetoothctrl.tools.UUi;
import com.wgh.bluetoothctrl.view.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by WGH on 2017/4/10.
 */

public class MyBluetoothDeviceAdapter extends RecyclerView.Adapter<MyBluetoothDeviceAdapter.ViewHolder> {

    private Context mContext;
    private List<BluetoothDevice> mBluetoothDeviceList;
    private ArrayList<Integer> mDrawableList = new ArrayList<>();

    private int mRandomInt;

    public MyBluetoothDeviceAdapter(List<BluetoothDevice> bluetoothDeviceList) {
        mBluetoothDeviceList = bluetoothDeviceList;
        initDrawableList();
    }

    private void initDrawableList() {
        if (mDrawableList != null && mDrawableList.size() != 0) {
            mDrawableList.clear();
        }
        mDrawableList.add(R.drawable.bluetootha);
        mDrawableList.add(R.drawable.bluetoothb);
        mDrawableList.add(R.drawable.bluetoothc);
        mDrawableList.add(R.drawable.bluetoothd);
        mDrawableList.add(R.drawable.bluetoothe);
        mDrawableList.add(R.drawable.bluetoothf);
        mDrawableList.add(R.drawable.bluetoothg);
        mDrawableList.add(R.drawable.bluetoothh);
        mDrawableList.add(R.drawable.bluetoothi);
        mDrawableList.add(R.drawable.bluetoothj);
        mDrawableList.add(R.drawable.bluetoothk);
        mDrawableList.add(R.drawable.bluetoothl);
        Random mRandom = new Random();
        mRandomInt = mRandom.nextInt(mDrawableList.size());
    }

    @Override
    public MyBluetoothDeviceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.bluetoothdevice_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getAdapterPosition() < 0) {
                    ULog.e("holder.getAdapterPosition() : " + holder.getAdapterPosition());
                    return;
                }
                final BluetoothDevice device = mBluetoothDeviceList.get(holder.getAdapterPosition());
                if (device == null) {
                    return;
                }
                BluetoothScan.stopScan();
                final Intent intent = new Intent();
                intent.putExtra(MainActivity.EXTRAS_DEVICE_NAME, device.getName());
                intent.putExtra(MainActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
                ((Activity) mContext).setResult(Activity.RESULT_OK, intent);
                ((Activity) mContext).finish();
            }
        });

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ULog.w("LongClick :　" + holder.getAdapterPosition());
                return true;
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(MyBluetoothDeviceAdapter.ViewHolder holder, int position) {
        BluetoothDevice device = mBluetoothDeviceList.get(position);
        if (TextUtils.isEmpty(device.getName())) {
            holder.deviceName.setText(R.string.device_unknown);
        } else {
            holder.deviceName.setText(device.getName());
        }

        holder.deviceImage.setImageResource(mDrawableList.get(((position + mRandomInt) %
                mDrawableList.size() + mDrawableList.size()) % mDrawableList.size()));

        ViewGroup.LayoutParams layoutParams = holder.deviceImage.getLayoutParams();
        layoutParams.height = (int) (UUi.getInstance(mContext).getWindowWidth()
                * Contants.scalingValueScanActivity / 2) - 166;
        holder.deviceImage.setLayoutParams(layoutParams);
    }

    @Override
    public int getItemCount() {
        return mBluetoothDeviceList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView deviceImage;
        TextView deviceName;

        ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            deviceImage = (ImageView) view.findViewById(R.id.device_image);
            deviceName = (TextView) view.findViewById(R.id.device_name);
        }
    }
}

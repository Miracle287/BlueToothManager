package com.lzy.bluetoothmanager;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 *
 * @Author lzy
 */
public class RmBluetoothDeviceManager {

    private final List<RmBluetoothDevice> mDevices = new ArrayList<>();

    private Context mContext;

    private static RmBluetoothDeviceManager instance;

    public static RmBluetoothDeviceManager getInstance(Context context){
        if(instance == null){
            synchronized (RmBluetoothDeviceManager.class){
                if(instance == null){
                    instance = new RmBluetoothDeviceManager(context);
                }
            }
        }
        return instance;
    }

    RmBluetoothDeviceManager(Context context) {
        mContext = context;
    }

    public RmBluetoothDevice findDevice(String address){
        for(RmBluetoothDevice device : mDevices) {
            if(device.getDeviceAddress().equals(address)){
                return device;
            }
        }
        return null;
    }

    public RmBluetoothDevice addDevice(BluetoothDevice device) {
        RmBluetoothDevice rmDevice = new RmBluetoothDevice(mContext, device);
        mDevices.add(rmDevice);
        return rmDevice;
    }

    public void onDeviceNameUpdated(BluetoothDevice device){
        RmBluetoothDevice rmDevice = findDevice(device.getAddress());
        rmDevice.setDeviceName(device.getName());
    }

    public List<RmBluetoothDevice> getAvailableDevices(){
        return mDevices;
    }

    public void clearAll(){
        if(mDevices.size() > 0){
            mDevices.clear();
        }
    }
}








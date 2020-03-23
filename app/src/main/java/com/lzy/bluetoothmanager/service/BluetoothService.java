package com.lzy.bluetoothmanager.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.lzy.bluetoothmanager.BluetoothEventManager;
import com.lzy.bluetoothmanager.IBlueToothService;
import com.lzy.bluetoothmanager.RmBluetoothDevice;
import com.lzy.bluetoothmanager.RmBluetoothDeviceManager;
import com.lzy.bluetoothmanager.task.BluetoothDiscoveryTask;

import java.util.List;

/**
 * Description: 蓝牙服务，提供RPC接口
 *
 * @Author lzy
 */
public class BluetoothService extends Service {
    public final static String TAG = BluetoothService.class.getSimpleName();

    private final Context mContext = BluetoothService.this;

    private BluetoothEventManager mEventManager;
    private RmBluetoothDeviceManager mDeviceManager;
    private BluetoothDiscoveryTask mDiscoveryTask;

    private BluetoothReceiver receiver = new BluetoothReceiver();

    @Override
    public void onCreate() {
        super.onCreate();
        mDeviceManager = RmBluetoothDeviceManager.getInstance(mContext);
        mEventManager = new BluetoothEventManager(mContext);
        mEventManager.registerAdapterIntentReceiver();
        registerReceiver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mEventManager.unRegisterReceiver();
        unRegisterReceiver();
    }

    @Override
    public IBinder onBind(Intent intent) {
        BlueToothAIDLBinder binder = new BlueToothAIDLBinder();
        return binder;
    }

    /**
     * 蓝牙远程AIDL调用接口
     */
    private class BlueToothAIDLBinder extends IBlueToothService.Stub{
        @Override
        public List<RmBluetoothDevice> getDevices() {
            if(mDiscoveryTask == null){
                mDeviceManager.clearAll();
                mDiscoveryTask = new BluetoothDiscoveryTask();
                mDiscoveryTask.execute();
            }
            return mDeviceManager.getAvailableDevices();
        }
    }

    private class BluetoothReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            synchronized (BluetoothReceiver.class) {
                String action = intent.getAction();
                if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    handleActionDiscoveryFinished(intent);
                }
            }
        }
    }

    public void handleActionDiscoveryFinished(Intent intent){
        mDiscoveryTask = null;
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mContext.registerReceiver(receiver, filter);
    }

    private void unRegisterReceiver(){
        mContext.unregisterReceiver(receiver);
    }
}

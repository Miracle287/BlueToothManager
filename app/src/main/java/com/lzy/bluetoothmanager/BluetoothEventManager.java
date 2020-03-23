package com.lzy.bluetoothmanager;

/**
 * Description:
 *  BluetoothEventManager receives broadcasts and callbacks from the Bluetooth
 *  API and dispatches the event on the UI thread to the right class in the
 *  Settings.
 *
 * @Author lzy
 */

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class BluetoothEventManager {
    private static final String TAG = "BluetoothEventManager";

    private final IntentFilter mAdapterIntentFilter, mProfileIntentFilter;
    private final Map<String, Handler> mHandlerMap;
    private Context mContext;

    private android.os.Handler mReceiverHandler;

    private RmBluetoothDeviceManager mDeviceManager;

    interface Handler {
        void onReceive(Context context, Intent intent, BluetoothDevice device);
    }

    private void addHandler(String action, Handler handler) {
        mHandlerMap.put(action, handler);
        mAdapterIntentFilter.addAction(action);
    }

    public BluetoothEventManager(Context context) {
        mAdapterIntentFilter = new IntentFilter();
        mProfileIntentFilter = new IntentFilter();
        mHandlerMap = new HashMap<String, Handler>();
        mContext = context;
        mDeviceManager = RmBluetoothDeviceManager.getInstance(mContext);

        // Bluetooth on/off broadcasts
        addHandler(BluetoothAdapter.ACTION_STATE_CHANGED, new AdapterStateChangedHandler());
        // Generic connected/not broadcast
        addHandler(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED,
                new ConnectionStateChangedHandler());

        // Discovery broadcasts
        addHandler(BluetoothAdapter.ACTION_DISCOVERY_STARTED, new ScanningStateChangedHandler(true));
        addHandler(BluetoothAdapter.ACTION_DISCOVERY_FINISHED, new ScanningStateChangedHandler(false));

        addHandler(BluetoothDevice.ACTION_FOUND, new DeviceFoundHandler());
        //addHandler(BluetoothDevice.ACTION_DISAPPEARED, new DeviceDisappearedHandler());
        addHandler(BluetoothDevice.ACTION_NAME_CHANGED, new NameChangedHandler());
        //addHandler(BluetoothDevice.ACTION_ALIAS_CHANGED, new NameChangedHandler());

        // Pairing broadcasts
        addHandler(BluetoothDevice.ACTION_BOND_STATE_CHANGED, new BondStateChangedHandler());
        //addHandler(BluetoothDevice.ACTION_PAIRING_CANCEL, new PairingCancelHandler());

        // Fine-grained state broadcasts
        addHandler(BluetoothDevice.ACTION_CLASS_CHANGED, new ClassChangedHandler());
        addHandler(BluetoothDevice.ACTION_UUID, new UuidChangedHandler());
    }

    public void registerProfileIntentReceiver() {
        mContext.registerReceiver(mBroadcastReceiver, mProfileIntentFilter, null, mReceiverHandler);
    }

    public void registerAdapterIntentReceiver() {
        mContext.registerReceiver(mBroadcastReceiver, mAdapterIntentFilter, null, mReceiverHandler);
    }

    public void unRegisterReceiver(){
        mContext.unregisterReceiver(mBroadcastReceiver);
    }

    public void setReceiverHandler(android.os.Handler handler) {
        unRegisterReceiver();
        mReceiverHandler = handler;
        registerAdapterIntentReceiver();
        registerProfileIntentReceiver();
    }

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent
                    .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            Handler handler = mHandlerMap.get(action);
            if (handler != null) {
                handler.onReceive(context, intent, device);
            }
        }
    };

    private class AdapterStateChangedHandler implements Handler {
        public void onReceive(Context context, Intent intent, BluetoothDevice device) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR);
            
        }
    }

    private class ScanningStateChangedHandler implements Handler {
        private final boolean mStarted;

        ScanningStateChangedHandler(boolean started) {
            mStarted = started;
        }
        public void onReceive(Context context, Intent intent,
                              BluetoothDevice device) {
            
        }
    }

    private class DeviceFoundHandler implements Handler {
        public void onReceive(Context context, Intent intent, BluetoothDevice device) {
            short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
            BluetoothClass btClass = intent.getParcelableExtra(BluetoothDevice.EXTRA_CLASS);
            String name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);

            RmBluetoothDevice rmDevice = mDeviceManager.findDevice(device.getAddress());
            if (rmDevice == null) {
                rmDevice = mDeviceManager.addDevice(device);
                Log.d(TAG, "DeviceFoundHandler created new RmBluetoothDevice: " + rmDevice);
            }
            rmDevice.setRSSI(rssi);
            rmDevice.setBtClass(btClass.getMajorDeviceClass());
            rmDevice.setDeviceName(name);
            rmDevice.setDeviceAddress(device.getAddress());
            rmDevice.setVisible(true);
        }
    }

    private class ConnectionStateChangedHandler implements Handler {
        @Override
        public void onReceive(Context context, Intent intent, BluetoothDevice device) {
            RmBluetoothDevice RmDevice = mDeviceManager.findDevice(device.getAddress());
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE,
                    BluetoothAdapter.ERROR);
            dispatchConnectionStateChanged(RmDevice, state);
        }
    }

    private void dispatchConnectionStateChanged(RmBluetoothDevice RmDevice, int state) {

    }

    private class DeviceDisappearedHandler implements Handler {
        public void onReceive(Context context, Intent intent, BluetoothDevice device) {
            RmBluetoothDevice RmDevice = mDeviceManager.findDevice(device.getAddress());
            if (RmDevice == null) {
                Log.w(TAG, "received ACTION_DISAPPEARED for an unknown device: " + device);
                return;
            }

        }
    }

    private class NameChangedHandler implements Handler {
        public void onReceive(Context context, Intent intent, BluetoothDevice device) {
            mDeviceManager.onDeviceNameUpdated(device);
        }
    }

    private class BondStateChangedHandler implements Handler {
        public void onReceive(Context context, Intent intent, BluetoothDevice device) {
            if (device == null) {
                Log.e(TAG, "ACTION_BOND_STATE_CHANGED with no EXTRA_DEVICE");
                return;
            }
            int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE,
                    BluetoothDevice.ERROR);

            //Todo
            RmBluetoothDevice RmDevice = mDeviceManager.findDevice(device.getAddress());
            if (RmDevice == null) {

            }
        }
    }

    private class ClassChangedHandler implements Handler {
        public void onReceive(Context context, Intent intent, BluetoothDevice device) {
        }
    }

    private class UuidChangedHandler implements Handler {
        public void onReceive(Context context, Intent intent, BluetoothDevice device) {
        }
    }

    private class PairingCancelHandler implements Handler {
        public void onReceive(Context context, Intent intent, BluetoothDevice device) {
            if (device == null) {
                Log.e(TAG, "ACTION_PAIRING_CANCEL with no EXTRA_DEVICE");
                return;
            }
        }
    }
    
}


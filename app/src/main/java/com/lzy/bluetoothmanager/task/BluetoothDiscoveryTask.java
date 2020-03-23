package com.lzy.bluetoothmanager.task;

import android.bluetooth.BluetoothAdapter;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Description: 蓝牙扫描任务
 *
 * @Author lzy
 */
public class BluetoothDiscoveryTask extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "BluetoothDiscoveryTask";

    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            doDiscovery();
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        cancelDiscovery();
    }

    private void doDiscovery() {
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter.startDiscovery();
    }

    private void cancelDiscovery(){
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
    }
}

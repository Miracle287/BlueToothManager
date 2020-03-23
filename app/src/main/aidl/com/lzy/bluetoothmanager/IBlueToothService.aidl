// IBlueToothService.aidl
package com.lzy.bluetoothmanager;

// Declare any non-default types here with import statements
import com.lzy.bluetoothmanager.RmBluetoothDevice;

interface IBlueToothService {

    List<RmBluetoothDevice> getDevices();

}
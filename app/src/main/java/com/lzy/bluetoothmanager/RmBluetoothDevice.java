package com.lzy.bluetoothmanager;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Description: model
 *
 * @Author lzy
 */
public class RmBluetoothDevice implements Parcelable {

    private Context mContext;

    private final BluetoothDevice mDevice;

    private int btClass;
    private int RSSI;
    private String deviceName;
    private String deviceAddress;
    private boolean visible;

    private int state;

    public RmBluetoothDevice(Context context, BluetoothDevice device){
        mContext = context;
        mDevice = device;
    }

    protected RmBluetoothDevice(Parcel in) {
        mDevice = in.readParcelable(BluetoothDevice.class.getClassLoader());
        btClass = in.readInt();
        RSSI = in.readInt();
        deviceName = in.readString();
        deviceAddress = in.readString();
        visible = in.readByte() != 0;
        state = in.readInt();
    }

    public static final Creator<RmBluetoothDevice> CREATOR = new Creator<RmBluetoothDevice>() {
        @Override
        public RmBluetoothDevice createFromParcel(Parcel in) {
            return new RmBluetoothDevice(in);
        }

        @Override
        public RmBluetoothDevice[] newArray(int size) {
            return new RmBluetoothDevice[size];
        }
    };

    public int getBtClass() {
        return btClass;
    }

    public void setBtClass(int btClass) {
        this.btClass = btClass;
    }

    public int getRSSI() {
        return RSSI;
    }

    public void setRSSI(int RSSI) {
        this.RSSI = RSSI;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public BluetoothDevice getDevice() {
        return mDevice;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RmBluetoothDevice) {
            return mDevice.equals(((RmBluetoothDevice) o).getDevice());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return mDevice.hashCode();
    }

    @Override
    public String toString() {
        return "RmBluetoothDevice{" +
                "btClass=" + btClass +
                ", RSSI=" + RSSI +
                ", deviceName='" + deviceName + '\'' +
                ", deviceAddress='" + deviceAddress + '\'' +
                ", visible=" + visible +
                ", state=" + state +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mDevice, flags);
        dest.writeInt(btClass);
        dest.writeInt(RSSI);
        dest.writeString(deviceName);
        dest.writeString(deviceAddress);
        dest.writeByte((byte) (visible ? 1 : 0));
        dest.writeInt(state);
    }
}

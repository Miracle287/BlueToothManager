package com.lzy.bluetoothtest;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;

import com.lzy.bluetoothmanager.IBlueToothService;
import com.lzy.bluetoothmanager.RmBluetoothDevice;
import com.lzy.bluetoothtest.Services.BlueToothConstant;
import com.lzy.bluetoothtest.model.TsEvent;
import com.lzy.bluetoothtest.utils.EventBusUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class MainActivity extends Activity {
    public final static String TAG = MainActivity.class.getSimpleName();

    private TextView mainTestTv;

    private IBlueToothService blueToothAidlBinder;

    private ServiceConnection btConnection;
    private boolean isBtConnectionAlive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        initView();
        btConnection = new BlueToothRemoteConnection();
        isBtConnectionAlive = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        isBtConnectionAlive = false;
    }

    /**
     * 初始化视图控件
     */
    public void initView(){
        mainTestTv = findViewById(R.id.main_test_tv);
        mainTestTv.setText(getString(R.string.app_content));
    }

    /**
     * 远程连接蓝牙服务器
     */
    public void toConnectServer(){
        Intent intent = new Intent(BlueToothConstant.BLUETOOTH_SERVICE_ACTION);
        bindService(intent, btConnection, BIND_AUTO_CREATE);
    }

    /**
     * 连接远程蓝牙扫描服务
     */
    private class BlueToothRemoteConnection implements ServiceConnection{

        List<RmBluetoothDevice> devices;

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isBtConnectionAlive = true;

            //获取远程传回的服务接口
            blueToothAidlBinder = IBlueToothService.Stub.asInterface(service);

            Thread btTask = new Thread(){
                @Override
                public void run() {
                    //每隔1秒获取蓝牙设备信息
                    while(isBtConnectionAlive){
                        try {
                            Thread.sleep(1000);
                            blueToothAidlBinder.fetchDeviceInfo(devices);
                            EventBusUtil.post(BlueToothConstant.EVENT_DEVICES_DESCOVERYING, devices);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            btTask.start();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            unbindService(btConnection);
            isBtConnectionAlive = false;
        }

    }

    /**
     * EventBus订阅事件
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void EventSubscribe(TsEvent event) {
        switch (event.getType()){
            case BlueToothConstant.EVENT_START_DISCOVERY:
                handleStartDiscovery();
                break;
            case BlueToothConstant.EVENT_DEVICES_DESCOVERYING:
                List<RmBluetoothDevice> devices = (List<RmBluetoothDevice>)event.getValue();
                handleFoundDevices(devices);
                break;
            default:
                break;
        }
    }

    private void handleStartDiscovery(){
        if(!isBtConnectionAlive){
            toConnectServer();
        }
    }

    private void handleFoundDevices(List<RmBluetoothDevice> devices){
        if(devices != null && devices.size() > 0){
            for(RmBluetoothDevice device : devices) {
                Log.e(TAG, "发现设备: " + device.getDeviceName() + " -- " + device.getDeviceAddress());
            }
        }
    }

}

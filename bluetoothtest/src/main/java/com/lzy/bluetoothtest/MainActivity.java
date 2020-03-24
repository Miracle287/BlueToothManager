package com.lzy.bluetoothtest;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.IBinder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.lzy.bluetoothmanager.IBlueToothService;
import com.lzy.bluetoothmanager.RmBluetoothDevice;
import com.lzy.bluetoothtest.services.BlueToothConstant;
import com.lzy.bluetoothtest.adapter.BluetoothDevicesListAdapter;
import com.lzy.bluetoothtest.model.TsEvent;
import com.lzy.bluetoothtest.utils.EventBusUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    public final static String TAG = MainActivity.class.getSimpleName();

    private TextView startBtn;
    private ListView deviceLv;

    private BluetoothDevicesListAdapter mDeviceAdapter;

    private IBlueToothService blueToothAidlBinder;

    private ServiceConnection btConnection;
    private boolean isBtConnectionAlive;

    private List<RmBluetoothDevice> devices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        btConnection = new BlueToothRemoteConnection();
        isBtConnectionAlive = false;
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isBtConnectionAlive = false;
        EventBus.getDefault().unregister(this);
    }

    /**
     * 初始化视图控件
     */
    public void initView(){
        startBtn = findViewById(R.id.main_start_btn);
        deviceLv = findViewById(R.id.main_devices_lv);

        startBtn.setText(getString(R.string.start_discovery));
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isBtConnectionAlive) {
                    EventBusUtil.post(BlueToothConstant.EVENT_START_DISCOVERY, null);
                }
            }
        });
    }

    /**
     * 远程连接蓝牙服务器
     */
    public void toConnectServer(){
        Intent intent = new Intent();
        intent.setAction(BlueToothConstant.BLUETOOTH_SERVICE_ACTION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Android 5.0 之后必须设置组件
            PackageManager pm = getApplication().getPackageManager();
            List<ResolveInfo> resolveInfo = pm.queryIntentServices(intent, 0);
            if (resolveInfo != null && resolveInfo.size() == 1) {
                ResolveInfo serviceInfo = resolveInfo.get(0);
                String packageName = serviceInfo.serviceInfo.packageName;
                String className = serviceInfo.serviceInfo.name;
                Intent componentIntent = new Intent(intent);
                componentIntent.setComponent(new ComponentName(packageName, className));
                bindService(componentIntent, btConnection, BIND_AUTO_CREATE);
            } else {
                Log.e(TAG, "BluetoothService is not installed.");
            }
        } else {
            bindService(intent, btConnection, BIND_AUTO_CREATE);
        }
    }

    /**
     * 连接远程蓝牙扫描服务
     */
    private class BlueToothRemoteConnection implements ServiceConnection{
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isBtConnectionAlive = true;

            //获取远程传回的服务接口
            blueToothAidlBinder = IBlueToothService.Stub.asInterface(service);

            Thread btTask = new Thread(){
                @Override
                public void run() {
                    // 定时获取蓝牙设备信息
                    while(isBtConnectionAlive){
                        try {
                            Thread.sleep(500);
                            devices = blueToothAidlBinder.getDevices();
                            EventBusUtil.post(BlueToothConstant.EVENT_DEVICES_DESCOVERYING, null);
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
                handleFoundDevices();
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

    private void handleFoundDevices(){
        if(devices != null && devices.size() > 0){
            for(RmBluetoothDevice device : devices) {
                Log.e(TAG, "Found device: " + device.getDeviceName() + " -- " + device.getDeviceAddress());
            }
        }

        if (mDeviceAdapter == null) {
            mDeviceAdapter = new BluetoothDevicesListAdapter(MainActivity.this, devices);
            deviceLv.setAdapter(mDeviceAdapter);
        } else {
            mDeviceAdapter.setList(devices);
            mDeviceAdapter.notifyDataSetChanged();
        }
    }
}

# BlueToothManager
基于AIDL实现的简易蓝牙扫描程序



## 更新

[2020.3.24] 修复Android 5.0及以上平台Intent隐式调用Service的问题 



## 使用

开发环境使用 Android Studio，平台API在Android 4.4以上均可

项目有具有两个Module：

* app: 作为AIDL服务端，用于蓝牙定时扫描后台服务，并传回数据给客户端Module
* bluetoothtest: 作为AIDL客户端，用于绑定服务端，定时获取蓝牙设备，并实时显示在界面上

> NOTE: 将两个Module分别编译apk文件，先安装app，再安装bluetoothtest



## AIDL文件

服务端和客户端组件都定义如下的AIDL文件：

```java
// IBlueToothService.aidl
package com.lzy.bluetoothmanager;

// Declare any non-default types here with import statements
import com.lzy.bluetoothmanager.RmBluetoothDevice;

interface IBlueToothService {

    List<RmBluetoothDevice> getDevices();

}
```

```java
// RmBluetoothDevice.aidl
package com.lzy.bluetoothmanager;

parcelable RmBluetoothDevice;

```



## 关于

本程序可供 Android 初学者学习之用，作为AIDL技术的一个简单示例应用






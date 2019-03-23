package com.lzy.bluetoothtest.utils;

import com.lzy.bluetoothtest.model.TsEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Description: EventBus操作工具类
 *
 * @Author lzy
 */
public class EventBusUtil {

    /**
     * EventBus事件广播
     * @param type
     * @param value
     */
    public static void post(int type, Object value) {
        TsEvent event = new TsEvent();
        event.setType(type);
        event.setValue(value);
        EventBus.getDefault().post(event);
    }

}

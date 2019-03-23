package com.lzy.bluetoothtest.model;

/**
 * Description: EventBus消息事件类
 *
 * @Author lzy
 */
public class TsEvent {
    private int type;
    private Object value;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}

package com.lzy.bluetoothtest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzy.bluetoothmanager.RmBluetoothDevice;
import com.lzy.bluetoothtest.R;

import java.util.List;

/**
 * Description: 蓝牙列表适配器
 *
 * @Author lzy
 */
public class BluetoothDevicesListAdapter extends BaseAdapter {

    private Context mContext;
    private List<RmBluetoothDevice> list;
    private LayoutInflater mInflater;

    public BluetoothDevicesListAdapter(Context context, List<RmBluetoothDevice> list) {
        this.mContext = context;
        this.list = list;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setList(List<RmBluetoothDevice> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.listview_item_bt_devices, parent, false);
            holder = new ViewHolder();
            holder.deviceIcon = convertView.findViewById(R.id.device_item_type_icon);
            holder.deviceName = convertView.findViewById(R.id.device_item_name_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.deviceIcon.setImageResource(R.mipmap.bluetooth);
        holder.deviceName.setText(list.get(position).getDeviceName());
        return convertView;
    }

    /**
     * 蓝牙视图持有者
     */
    private class ViewHolder{
        private ImageView deviceIcon;
        private TextView deviceName;
    }
}

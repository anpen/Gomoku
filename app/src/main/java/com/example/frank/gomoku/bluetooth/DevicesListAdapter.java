package com.example.frank.gomoku.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.frank.gomoku.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Frank on 2016/1/26.
 */
public class DevicesListAdapter extends BaseAdapter {
    private static final int TYPE_TITLE = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_EMPTY = 2;

    private final ArrayList<BluetoothDevice> mListBondedValues = new ArrayList<BluetoothDevice>();
    private final ArrayList<BluetoothDevice> mListValues = new ArrayList<BluetoothDevice>();
    private final Context mContext;

    public DevicesListAdapter(Context context) {
        mContext = context;
    }

    /**
     * Sets a list of bonded devices.
     * @param devices list of bonded devices.
     */
    public void addBondedDevices(final Set<BluetoothDevice> devices) {
        final List<BluetoothDevice> bondedDevices = mListBondedValues;
        for (BluetoothDevice device : devices) {
            bondedDevices.add(device);
        }
        notifyDataSetChanged();
    }

    public void addDevice(BluetoothDevice result) {
        mListValues.add(result);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        final int bondedCount = mListBondedValues.size() + 1; // 1 for the title
        final int availableCount = mListValues.isEmpty() ? 2 : mListValues.size() + 1; // 1 for title, 1 for empty text
        if (bondedCount == 1)
            return availableCount;
        return bondedCount + availableCount;
    }

    @Override
    public Object getItem(int position) {

        final int bondedCount = mListBondedValues.size() + 1; // 1 for the title
        if (mListBondedValues.isEmpty()) {
            if (position == 0)
                return R.string.scanner_subtitle_not_bonded;
            else
                return mListValues.get(position - 1);
        } else {
            if (position == 0)
                return R.string.scanner_subtitle_bonded;
            if (position < bondedCount)
                return mListBondedValues.get(position - 1);
            if (position == bondedCount)
                return R.string.scanner_subtitle_not_bonded;
            return mListValues.get(position - bondedCount - 1);
        }
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return getItemViewType(position) == TYPE_ITEM;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_TITLE;

        if (!mListBondedValues.isEmpty() && position == mListBondedValues.size() + 1)
            return TYPE_TITLE;

        if (position == getCount() - 1 && mListValues.isEmpty())
            return TYPE_EMPTY;

        return TYPE_ITEM;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View oldView, ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        final int type = getItemViewType(position);

        View view = oldView;
        switch (type) {
            case TYPE_EMPTY:
                if (view == null) {
                    view = inflater.inflate(R.layout.device_list_empty, parent, false);
                }
                break;
            case TYPE_TITLE:
                if (view == null) {
                    view = inflater.inflate(R.layout.device_list_title, parent, false);
                }
                final TextView title = (TextView) view;
                title.setText((Integer) getItem(position));
                break;
            default:
                if (view == null) {
                    view = inflater.inflate(R.layout.device_row, parent, false);
                    final ViewHolder holder = new ViewHolder();
                    holder.name = (TextView) view.findViewById(R.id.bt_name);
                    holder.address = (TextView) view.findViewById(R.id.bt_addr);
                    view.setTag(holder);
                }

                final BluetoothDevice device = (BluetoothDevice) getItem(position);
                final ViewHolder holder = (ViewHolder) view.getTag();
                final String name = device.getName();
                holder.name.setText(name != null ? name : mContext.getString(R.string.not_available));
                holder.address.setText(device.getAddress());
                break;
        }
        return view;
    }


    private class ViewHolder {
        private TextView name;
        private TextView address;
    }
}

package com.example.frank.gomoku.bluetooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.frank.gomoku.R;

import java.util.logging.LogRecord;

/**
 * Created by Frank on 2016/1/26.
 */
public class ScannerFragment extends DialogFragment {
    private static ScannerFragment scannerFragment;

    private BluetoothAdapter mBluetoothAdapter;
    private DevicesListAdapter mDeviceListAdapter;
    private OnDeviceSelectedListener mListener;

    protected final Handler mHandler = new Handler();


    public interface OnDeviceSelectedListener {
        void onDeviceSeleted(BluetoothDevice device);
        void onDialogCancel();
    }

    public static ScannerFragment getInstance() {
        if (scannerFragment == null) {
            scannerFragment = new ScannerFragment();
        }
        return scannerFragment;
    }

    public void startScan() {
        // If we're already discovering, stop it
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        // Request discover from BluetoothAdapter
        mBluetoothAdapter.startDiscovery();
    }

    public void stopScan() {
//        if (mBluetoothAdapter != null)
          mBluetoothAdapter.cancelDiscovery();
    }

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context,final Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        mDeviceListAdapter.addDevice(device);
                    }
                });
            }
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnDeviceSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnDeviceSelectedListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mDeviceListAdapter = new DevicesListAdapter(getActivity());

        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopScan();
        getActivity().unregisterReceiver(mReceiver);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.scanner_fragment,null);
        final ListView list = (ListView) dialogView.findViewById(R.id.devices);
        list.setAdapter(mDeviceListAdapter);
        list.setEmptyView(dialogView.findViewById(android.R.id.empty));
        builder.setTitle(R.string.scanner_title);
        final  AlertDialog dialog = builder.setView(dialogView).create();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                stopScan();
                dialog.dismiss();
                BluetoothDevice bt = (BluetoothDevice) mDeviceListAdapter.getItem(position);
                mListener.onDeviceSeleted(bt);
            }
        });
        mDeviceListAdapter.addBondedDevices(mBluetoothAdapter.getBondedDevices());
        startScan();
        return dialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        mListener.onDialogCancel();
    }
}

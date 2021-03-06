package com.example.frank.gomoku.bluetooth;

/**
 * Created by Frank on 2016/1/27.
 */

/**
 * Defines several constants used between {@link BluetoothChatService} and the UI
 */
public interface Constants {

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    public static final int COMMAND_BACK = 6;
    public static final int COMMAND_CLEAR = 7;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "devcie_name";
    public static final String TOAST = "toast";

}

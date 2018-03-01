package com.example.frank.gomoku.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.frank.gomoku.R;
import com.example.frank.gomoku.model.Chessman;
import com.example.frank.gomoku.view.ChessBoard;

import java.io.IOException;


public class BluetoothGame extends AppCompatActivity implements ScannerFragment.OnDeviceSelectedListener {

    private ChessBoard chessBoard;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothChatService mChatService;
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;

    private boolean secure = true;
    private boolean isBlack = false;
    private boolean isYourTurn = false;
    private boolean isSound = false;
    private int backCount = 0;

    private static final String TAG = "BluetoothGame";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_game);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mChatService == null) {
            setupChat();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mChatService != null) {
            mChatService.stop();
        }
    }

    private void setupChat() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mChatService = new BluetoothChatService(this,mHandler);
    }

    private void init() {
        chessBoard = (ChessBoard)findViewById(R.id.board);
        chessBoard.setStart(false);
        chessBoard.setOnChessmanDroppedListener(new ChessBoard.OnChessmanDroppedListener() {
            @Override
            public void onChessmanDropped(Chessman cm) {
                Log.i(TAG,String.format("send :(%d,%d)",cm.getX(), cm.getY()));
                if (isBlack) {
                    cm.setColor(ChessBoard.BLACK);
                } else {
                    cm.setColor(ChessBoard.WHITE);
                }
                if (isYourTurn) {
                    sendMessage(cm);
                    chessBoard.drop(cm);
                    isYourTurn = false;
                }
            }
        });
    }

    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            startActivity(discoverableIntent);
        }
    }

    private void sendMessage(Chessman cm) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            return;
        }
        // Check that there's actuallly something to send
        if (cm != null) {
            mChatService.write(cm);
        }
    }

    private void sendCommand(byte[] bytes) {
        mChatService.write(bytes);
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_NONE:
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_CONNECTED:
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            isBlack = true;
                            isYourTurn = true;
                            Log.i(TAG, "" + isBlack);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    break;
                case Constants.MESSAGE_READ:

                    byte[] readBuf = (byte[]) msg.obj;
                    if (msg.arg1 == 1) {
                        Log.e(TAG,readBuf.length + " len");
                        if (readBuf[0] == Constants.COMMAND_BACK) {
                            chessBoard.back();
                        } else {
                            chessBoard.clear();
                        }
                    } else {
                        Chessman cm = null;
                        try {
                            cm = Chessman.deserialize(readBuf);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        if (!isYourTurn) {
                            chessBoard.drop(cm);
                            isYourTurn = true;
                        }
                    }
                    break;
                case  Constants.MESSAGE_DEVICE_NAME:
                    String  mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    Toast.makeText(BluetoothGame.this, "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    chessBoard.setStart(true);
                    break;
                case Constants.MESSAGE_TOAST:
                    Toast.makeText(BluetoothGame.this, msg.getData().getString(Constants.TOAST),
                            Toast.LENGTH_SHORT).show();
                    sendCommand(new byte[]{Constants.COMMAND_CLEAR});
                    chessBoard.clear();
                    chessBoard.setStart(false);
                    break;
            }
        }
    };

    @Override
    public void onDeviceSeleted(final BluetoothDevice device) {
        mChatService.connect(device,true);
    }

    @Override
    public void onDialogCancel() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bluetooth_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.secure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                secure = true;
                ScannerFragment.getInstance().show(getFragmentManager(), "ScannerFragment");
                return true;
            }
            case R.id.back : {
                if (!isYourTurn && chessBoard.getCount() > 0) {
                    sendCommand(new byte[] {Constants.COMMAND_BACK});
                    isYourTurn = true;
                    chessBoard.back();
                }
                return true;
            }
            case R.id.clear: {
                if (!isYourTurn && chessBoard.getCount() > 0) {
                    sendCommand(new byte[]{Constants.COMMAND_CLEAR});
                    chessBoard.clear();
                }
                return true;
            }
            case R.id.sound: {
                isSound = !isSound;
                chessBoard.setSound(isSound);
                return true;
            }
            case R.id.discoverable: {
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isYourTurn && chessBoard.getCount() > 0) {
                sendCommand(new byte[]{Constants.COMMAND_BACK});
                chessBoard.back();
                isYourTurn = true;
                return true;
            } else {
                if (++backCount == 1) {
                    Toast.makeText(this,"exit one more click", Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}

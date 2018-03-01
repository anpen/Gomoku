package com.example.frank.gomoku;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.frank.gomoku.bluetooth.BluetoothGame;
import com.example.frank.gomoku.bluetooth.ScannerFragment;
import com.example.frank.gomoku.view.ChessBoard;

public class MainActivity extends AppCompatActivity {
    private final static int REQUEST_ENABLE_BT = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        init();
    }

    private void init() {
        findViewById(R.id.alone).setOnClickListener(clickListener);
        findViewById(R.id.bluetooth).setOnClickListener(clickListener);
        findViewById(R.id.network).setOnClickListener(clickListener);
    }
    
    private View.OnClickListener clickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.alone:
                    enterAloneMode();
                    break;
                case R.id.bluetooth:
                    setupBt();
                    break;
                case R.id.network:
                    enterNetworkMode();
                    break;
            }
        }
    };

    private void enterNetworkMode() {
    }

    private void enterBluetoothMode() {
        Intent i = new Intent(this, BluetoothGame.class);
        startActivity(i);
    }

    private void enterAloneMode() {
        
    }


    private void setupBt() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (null == mBluetoothAdapter) {
            Toast.makeText(this, "Bluetooth is not supported on your device!", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (!mBluetoothAdapter.isEnabled()) {
            enableBt();
        } else {
            enterBluetoothMode();
        }
    }

    private void enableBt() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_ENABLE_BT) {
            enterBluetoothMode();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

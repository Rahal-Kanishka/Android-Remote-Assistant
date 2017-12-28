package com.testing.rahal.firstapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;

public class FirstTester extends AppCompatActivity {

    Button b_on, b_off, b_disc, b_list;
    ListView listView;
    BluetoothAdapter bluetoothAdapter;
    private static final int REQUEST_ENABLED = 0;
    private static final int REQUEST_DISCOVERABLE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_tester);


        b_on = (Button) findViewById(R.id.b_on);
        b_off = (Button) findViewById(R.id.b_off);
        b_disc = (Button) findViewById(R.id.b_disc);
        b_list = (Button) findViewById(R.id.b_list);
        listView = (ListView) findViewById(R.id.list);


//        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        final BluetoothManager bluetoothManager = (BluetoothManager) this.getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {

            Toast.makeText(this, "Bluetooth not supported!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Bluetooth supported!", Toast.LENGTH_SHORT).show();
        }

        b_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, REQUEST_ENABLED);
                if (bluetoothAdapter.isEnabled()) {
                    Toast.makeText(FirstTester.this, "Bluetooth enabled!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        b_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothAdapter.disable();
            }
        });
        b_disc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!bluetoothAdapter.isDiscovering()) {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    startActivityForResult(intent, REQUEST_DISCOVERABLE);
                }
            }
        });
        b_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                OutputStream outputStream = null;
                String message = "Initial message through bluetooth";
                ArrayList<String> deviceNamesList = new ArrayList<>();

                for (BluetoothDevice device : pairedDevices) {
                    deviceNamesList.add(device.getName());
                }

                ArrayAdapter arrayAdapter = new ArrayAdapter(FirstTester.this, android.R.layout.simple_list_item_1, deviceNamesList);
                listView.setAdapter(arrayAdapter);

                BluetoothDevice selectedDevice = null;
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //get the device
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().equalsIgnoreCase("rahal-EliteBook")) {
                        Toast.makeText(FirstTester.this, "Bluetooth device rahal-EliteBook found!", Toast.LENGTH_SHORT).show();
                        selectedDevice = device;
                        break;
                    }
                }

                if (selectedDevice == null) {
                    Toast.makeText(FirstTester.this, "Bluetooth device rahal-EliteBook not found!", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        ParcelUuid[] uuids = selectedDevice.getUuids();
                        BluetoothSocket socket = selectedDevice.createRfcommSocketToServiceRecord(uuids[0].getUuid());

                        socket.connect();
                        outputStream = socket.getOutputStream();
                        outputStream.write(message.getBytes());
                        Toast.makeText(FirstTester.this, "Bluetooth message send!", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(FirstTester.this, "Bluetooth message failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


}

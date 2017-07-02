/**
 * Wolves Vs Goats by Andr� Rosa and Fernando Alves is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 * Based on a work at http://wvg.i3portal.net.
 * <p>
 * Learn how to share your work with existing communities that have enabled Creative Commons licensing.
 * <p>
 * Creative Commons is a non-profit organization.
 *
 * @author Andr� Rosa
 * @author Fernando Alves
 * @version 0.1
 */
package com.wolvesvsgoats.objectives;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.wolvesvsgoats.MainActivity;
import com.wolvesvsgoats.R;
import com.wolvesvsgoats.utils.Audio;
import com.wolvesvsgoats.utils.Dispensa;
import com.wolvesvsgoats.utils.Message;
import com.wolvesvsgoats.utils.Utilities;

import java.util.ArrayList;
import java.util.Hashtable;

public class Chinada extends Activity implements SensorEventListener {
    private final static String noDevices = "no devices";
    private static final int THRESHOLD = 500;
    private static final int MINIMUN_DISTANCE = -70;
    private static final long TIME_THRESHOLD = 6000;
    // for bluetooth
    private BluetoothAdapter mBtAdapter;
    private Button chinada;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;
    private ArrayList<String> searchRes;
    private Hashtable<String, Info> rssis;
    // for sounds
    private Audio audio;
    // for gyroscope
    private SensorManager manager;
    private Sensor acc;
    private long lastUpdate = -1;
    private float x, y, z, lastX, lastY, lastZ;
    private boolean chinating;
    private String chinadaTarget;
    private int itemPosition;
    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    private String macToDiscover;
    private int rssi;
    Runnable getNameFromBMac = new Runnable() {

        @Override
        public void run() {
            System.out.println("vou perguntar cenas: " + macToDiscover);
            Message send = new Message(Message.GET_NAME_BY_BMAC), receive = null;
            send.addElement(macToDiscover);

            Utilities.send(send);
            do {
                receive = Utilities.receive();
            } while (receive.getType() != Message.GET_NAME_BY_BMAC);

            String name = (String) receive.getElement();
            System.out.println("received name: " + name);

            if (!name.equals("")) {

                if (!searchRes.contains(name)) {
                    searchRes.add(name);
                }
                // keep track of time to keep chinadas close to imediate
                rssis.put(name, new Info(rssi, System.currentTimeMillis()));
            }
        }
    };
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("recebi");
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                System.out.println("leitura");
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);

                macToDiscover = device.getAddress();

                new Thread(getNameFromBMac).start();
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                System.out.println("finish");

                mNewDevicesArrayAdapter.clear();
                System.out.println("limpei");
                if (searchRes.size() == 0) {
                    mNewDevicesArrayAdapter.add(noDevices);
                    System.out.println("nao ha nada a listar");
                } else {
                    mNewDevicesArrayAdapter.addAll(searchRes);
                    System.out.println("listei cenas");
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        audio = new Audio(this);

        setContentView(R.layout.chinada_screen);

        chinada = (Button) findViewById(R.id.button_chinada);
        chinada.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                Toast.makeText(getContext(), "Choose your target quickly!", Toast.LENGTH_SHORT).show();
                doDiscovery();
            }
        });

        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.devices_list);
        rssis = new Hashtable<String, Info>();

        initGyroscope();
        initBluetooth();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        setResult(-1, i);
        finish();
    }

    public Context getContext() {
        return this;
    }

    @Override
    public void onConfigurationChanged(Configuration c) {
        super.onConfigurationChanged(c);
    }

    private void initGyroscope() {
        chinating = false;
        chinadaTarget = null;
        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        acc = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    }

    private void initBluetooth() {
        // Find and set up the ListView for newly discovered devices
        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        // new_devices
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                setChinada(position);
            }
        });

        searchRes = new ArrayList<String>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // bluetooth registers
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // gyroscope registers
        manager.registerListener(this, acc, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }
        this.unregisterReceiver(mReceiver);
        manager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }
    }

    private void doClear() {
        mNewDevicesArrayAdapter.clear();
        searchRes.clear();
    }

    private void doDiscovery() {
        doClear();

        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        mBtAdapter.startDiscovery();
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (chinadaTarget != null && event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                x = event.values[0];
                y = event.values[1];
                z = event.values[2];

                float speed = Math.abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000;
                if (!chinating && speed > THRESHOLD) {
                    chinating = true;
                    chinada();
                } else {
                    chinating = false;
                }
                lastX = x;
                lastY = y;
                lastZ = z;
            }
        }
    }

    private void setChinada(int position) {
        chinadaTarget = mNewDevicesArrayAdapter.getItem(position);
        if (chinadaTarget.equals(noDevices)) {
            chinadaTarget = null;
            return;
        }
        String device = mNewDevicesArrayAdapter.getItem(itemPosition);
        Info info = rssis.get(device);
        int rssi = info.rssi;
        if (rssi > MINIMUN_DISTANCE) {
            itemPosition = position;
            Toast.makeText(this, "Get your target quickly!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Target is too far away!", Toast.LENGTH_SHORT).show();
            chinadaTarget = null;
            doDiscovery();
        }
    }

    private void chinada() {
        String device = mNewDevicesArrayAdapter.getItem(itemPosition);
        Info info = rssis.get(device);

        if (System.currentTimeMillis() - info.time > TIME_THRESHOLD) {
            Toast.makeText(this, "You took too long!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "CHINADAAAAAAAA!!!", Toast.LENGTH_LONG).show();
            audio.playSound(Dispensa.player.getWeapon());

            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.putExtra("chinada_target", chinadaTarget);
            setResult(2, i);

            finish();
        }
        chinadaTarget = null;
        doDiscovery();
    }

    private class Info {
        int rssi;
        long time;

        Info(int rssi, long time) {
            this.rssi = rssi;
            this.time = time;
        }
    }
}
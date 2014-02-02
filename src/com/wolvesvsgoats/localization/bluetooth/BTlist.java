/**
 * Wolves Vs Goats by Andr� Rosa and Fernando Alves is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 * Based on a work at http://wvg.i3portal.net.
 * 
 * Learn how to share your work with existing communities that have enabled Creative Commons licensing.
 * 
 * Creative Commons is a non-profit organization.
 * 
 * @author Andr� Rosa
 * @author Fernando Alves
 * @version 0.1
 */
package com.wolvesvsgoats.localization.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.widget.TextView;

public class BTlist {

	// Return Intent extra
	public static String EXTRA_DEVICE_ADDRESS = "device_address";
	public static String EXTA_DEVICE_RSSI = "device_rssi";
	private BluetoothAdapter mBtAdapter;
	private int SamplingIntval = 2000;
	private Thread thread;

	/** Called when the activity is first created. */
	public BTlist(BluetoothAdapter ba) {
		mBtAdapter = ba;
	}

	protected void onDestroy() {

		if (mBtAdapter != null) {
			mBtAdapter.cancelDiscovery();
		}
	}

	/**
	 * Start device discover with the BluetoothAdapter
	 */
	public void start() {
		thread = new Thread(Scanner);
		thread.start();

	}

	private Runnable Scanner = new Runnable() {
		public void run() {
			while (true) {
				try {
					Thread.sleep(SamplingIntval);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				if (mBtAdapter.isDiscovering()) {
					mBtAdapter.cancelDiscovery();
				}
				mBtAdapter.startDiscovery();
			}
		}
	};

	/*public class BtReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("recebeu bt");
			StringBuilder sb = new StringBuilder();
			Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
			for(BluetoothDevice device : pairedDevices) {
				short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
				sb.append(device.getName()).append("\t").append(device.getAddress())
				.append("\t").append(rssi).append("\n");
			}
			/*String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
				mNewDevicesArrayAdapter.add(device.getName() + " " + device.getAddress() + " " + "RSSI = " + rssi);
			}
			btScn.setText(sb);
		}
	};*/
}
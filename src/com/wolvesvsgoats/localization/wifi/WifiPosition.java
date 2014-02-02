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
package com.wolvesvsgoats.localization.wifi;

import android.content.Context;
import android.net.wifi.WifiManager;

public class WifiPosition {

	/** Called when the activity is first created. */
	private WifiManager mainWifi;
	private double[] myLocation = new double[2];
	boolean toScan = false;
	private int SamplingIntval = 2000;
	StringBuilder sb = new StringBuilder();
	Thread thread;

	public WifiPosition(Context context, WifiManager wm) {
		mainWifi = wm;
	}

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

				mainWifi.startScan();
			}
		}
	};

	public double[] getMyLocation() {
		return myLocation;
	}

	/*public class WifiReceiver extends BroadcastReceiver {

		public void onReceive(Context c, Intent intent) {
			System.out.println("recebeu wifi");
			sb = new StringBuilder();
			List<ScanResult> wifiList = mainWifi.getScanResults();
			Collections.sort(wifiList, new Comparator<ScanResult>() {
				@Override
				// reverse order
				public int compare(ScanResult one, ScanResult two) {
					if (one.level > two.level)
						return 1;
					if (one.level == two.level)
						return 0;
					return -1;
				}
			});
			String loc;
			String[] lat_lng;
			int a = 0;
			int min = wifiList.size() > 3 ? 3 : wifiList.size();
			for (int i = 0; i < min; i++) {
				ScanResult scan = wifiList.get(i);

				// POI Hash filtering
				// showing the three registered beacons with higher strength
				if ((loc = positionsTable.get(scan.BSSID)) != null) {
					int dist = (int) Utilities.calcDistance(scan.level);
					Log.d("Distance", "" + dist);
					lat_lng = loc.split(",");
					wifiLocation[a][0] = Double.parseDouble(lat_lng[0]);
					wifiLocation[a][1] = Double.parseDouble(lat_lng[1]);
					wifiLocation[a][2] = scan.level;
					wifiLocation[a][3] = dist;
					sb.append(scan.SSID).append("\t").append(scan.BSSID)
							.append("\t").append(scan.level).append("\t")
							.append(dist).append("\n");
					a++;
				}
			}

			myLocation = Utilities.MyTrilateration(wifiLocation[0],
					wifiLocation[1], wifiLocation[2]);

			wifiScn.setText(sb);
		}
	}*/
}

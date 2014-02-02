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
package com.wolvesvsgoats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.api.IMapView;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;

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
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wolvesvsgoats.data.Player;
import com.wolvesvsgoats.localization.bluetooth.BTlist;
import com.wolvesvsgoats.localization.wifi.WifiPosition;
import com.wolvesvsgoats.objectives.CameraActivity;
import com.wolvesvsgoats.objectives.Chinada;
import com.wolvesvsgoats.objectives.Objective;
import com.wolvesvsgoats.utils.Dispensa;
import com.wolvesvsgoats.utils.Message;
import com.wolvesvsgoats.utils.Position;
import com.wolvesvsgoats.utils.Utilities;

public class MainActivity extends Activity {
	private static final int ALWAYS_DISCOVERABLE = 9459328;
	private WifiReceiver receiverWifi;
	public BtReceiver receiverBt;
	private WifiManager mainWifi;
	public static BluetoothAdapter mBtAdapter;
	private MapView mapView;
	private Map<String, String> positionsTable;
	private Button chinada;
	private Button camera;
	private Button objectives;
	private TextView faccao;
	private CustomOverLay oldPos;

	private String ipAddress;
	private String macB;

	private String playerName;
	private Player player;

	private Handler handler;
	private Timer timer;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent data = getIntent();
		Bundle bundle = data.getExtras();
		playerName = bundle.getString("name");

		setContentView(R.layout.activity_main);
		chinada = (Button) findViewById(R.id.toChinada);
		chinada.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent chinar = new Intent(getApplicationContext(), Chinada.class);
				startActivityForResult(chinar, 2);
			}
		});

		camera = (Button) findViewById(R.id.toCamera);
		camera.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent camera = new Intent(getApplicationContext(), CameraActivity.class);
				startActivityForResult(camera, 1);
			}
		});

		objectives = (Button) findViewById(R.id.toObjectives);
		objectives.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showObjectiveDialog();
			}
		});
		
		faccao = (TextView) findViewById(R.id.faccao);
		
		mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiPosition wp = new WifiPosition(getApplicationContext(), mainWifi);
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		BTlist btl = new BTlist(mBtAdapter);

		positionsTable = new HashMap<String, String>();
		// eduroam
		positionsTable.put("00:12:80:44:d0:a0", 38.755533 + "," + (-9.157493));
		positionsTable.put("00:14:6a:c5:d2:30", 38.755357 + "," + (-9.157459));
		positionsTable.put("00:1e:4a:55:e3:b0", 38.755407 + "," + (-9.158033));
		positionsTable.put("00:14:6a:d8:c5:20", 38.755725 + "," + (-9.15818));
		positionsTable.put("00:12:80:44:d8:90", 38.756033 + "," + (-9.157681));
		positionsTable.put("00:14:6a:d8:ca:a0", 38.756238 + "," + (-9.158094));
		positionsTable.put("00:14:6a:d8:c0:f0", 38.756192 + "," + (-9.158207));
		positionsTable.put("00:14:6a:d8:cd:40", 38.755407 + "," + (-9.157472));
		positionsTable.put("00:14:6a:d8:cd:80", 38.755767 + "," + (-9.158202));
		positionsTable.put("00:21:d8:44:8a:40", 38.755568 + "," + (-9.158016));
		positionsTable.put("00:14:6a:d8:be:90", 38.755759 + "," + (-9.158196));
		positionsTable.put("00:1e:7a:28:61:30", 38.756156 + "," + (-9.157754));
		positionsTable.put("00:14:6a:c5:d0:f0", 38.756254 + "," + (-9.158016));
		positionsTable.put("00:1e:7a:28:5e:60", 38.756141 + "," + (-9.158395));
		positionsTable.put("00:14:6a:d8:c1:00", 38.755265 + "," + (-9.157729));
		positionsTable.put("00:1a:e3:d3:fc:b0", 38.755391 + "," + (-9.158008));
		positionsTable.put("00:1e:7a:28:5c:90", 38.755637 + "," + (-9.158121));
		positionsTable.put("00:1e:7a:28:58:70", 38.756121 + "," + (-9.157737));
		positionsTable.put("00:14:6a:c5:d0:90", 38.756202 + "," + (-9.158183));
		positionsTable.put("c4:0a:cb:25:2a:b0", 38.755273 + "," + (-9.157957));
		positionsTable.put("c4:0a:cb:25:34:20", 38.755817 + "," + (-9.158212));
		positionsTable.put("00:14:6a:c5:d3:e0", 38.755246 + "," + (-9.157818));
		positionsTable.put("00:14:6a:d8:be:80", 38.755342 + "," + (-9.15799));
		positionsTable.put("00:14:6a:c5:cf:b0", 38.755732 + "," + (-9.158185));
		positionsTable.put("00:14:6a:d8:0d:60", 38.755987 + "," + (-9.157898));
		positionsTable.put("00:1e:7a:28:46:e0", 38.75616 + "," + (-9.157746));
		positionsTable.put("00:14:6a:c5:d5:b0", 38.756257 + "," + (-9.158011));

		receiverWifi = new WifiReceiver();
		IntentFilter filter1 = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		registerReceiver(receiverWifi, filter1);

		// bluetooth Listener
		receiverBt = new BtReceiver();
		IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(receiverBt, filter2);
		filter2 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(receiverBt, filter2);

		wp.start();
		btl.start();

		mapView = (MapView) findViewById(R.id.mapView);// new MapView(this,
														// 256);
		mapView.setClickable(true);
		mapView.setBuiltInZoomControls(true);
		// mapView.setTileSource(TileSourceFactory.);
		mapView.getController().setZoom(18);
		mapView.getController().setCenter(new GeoPoint(38.75579, -9.15789));
		mapView.setUseDataConnection(false);

		WifiInfo wifiInfo = mainWifi.getConnectionInfo();
		int ip = wifiInfo.getIpAddress();
		ipAddress = Formatter.formatIpAddress(ip);
		macB = mBtAdapter.getAddress();

		player = new Player(playerName, ipAddress, macB);
		Dispensa.player = player;

		final Message joinGame = new Message(Message.JOIN_GAME);
		joinGame.addElement(player);
		Utilities.send(joinGame);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// switch? not very stronk
		if (requestCode == 1) {
			Bundle bundle = data.getExtras();
			if (bundle != null) {
				String qrCode = bundle.getString("qrcode");
				processObjective(qrCode);
			}
		} else if (requestCode == 2) {
			Bundle bundle = data.getExtras();
			if (bundle != null) {
				String chinado = bundle.getString("chinada_target");
				mataOGajo(chinado);
			}
		}
	}

	private void mataOGajo(String chinado) {
		System.out.println("chinei");
		Message m = new Message(Message.CHINAR);
		m.addElement(chinado);
		m.addElement(player);
		Utilities.send(m);
		System.out.println("mandei -> espero");
	}

	private void processObjective(String qrCode) {
		Message m = new Message(Message.PROCESS_QRCODE);
		m.addElement(player);
		m.addElement(qrCode);
		Utilities.send(m);
	}

	@Override
	public void onBackPressed() {
	}

	void showObjectiveDialog() {
		DialogFragment newFragment = MyAlertDialogFragment.newInstance(1);
		newFragment.setCancelable(false);
		newFragment.show(getFragmentManager(), "firstcontractdialog");
	}

	public static class MyAlertDialogFragment extends DialogFragment {
		private static AlertDialog aux;

		public static MyAlertDialogFragment newInstance(int title) {
			MyAlertDialogFragment frag = new MyAlertDialogFragment();
			Bundle args = new Bundle();
			args.putInt("title", title);
			frag.setArguments(args);
			return frag;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			LayoutInflater inflater = getActivity().getLayoutInflater();

			View view = inflater.inflate(R.layout.first_contract_dialog, null, false);

			ListView auxView = (ListView) view.findViewById(R.id.globais);
			ListView auxView2 = (ListView) view.findViewById(R.id.faccao);
			
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(Dispensa.ctx, android.R.layout.simple_list_item_1) {
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					View view = super.getView(position, convertView, parent);
					TextView text = (TextView) view.findViewById(android.R.id.text1);
					text.setTextColor(Color.BLACK);
					return view;
				}
			};
			ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(Dispensa.ctx, android.R.layout.simple_list_item_1) {
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					View view = super.getView(position, convertView, parent);
					TextView text = (TextView) view.findViewById(android.R.id.text1);
					text.setTextColor(Color.BLACK);
					return view;
				}
			};
			
			for (Objective o : Dispensa.objectives) {
				if(o.isFaction()){
					adapter2.add(o.toString());
				}else{
					adapter.add(o.toString());
				}
			}
			auxView.setAdapter(adapter);
			auxView2.setAdapter(adapter2);

			aux = new AlertDialog.Builder(getActivity()).setTitle("Objectivos").setView(view).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					aux.dismiss();
				}
			}).create();

			aux.show();

			return aux;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		finishActivity(ALWAYS_DISCOVERABLE);
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiverWifi);
		unregisterReceiver(receiverBt);

		timer.cancel();
		timer.purge();
	}

	@Override
	protected void onResume() {
		Dispensa.ctx = getApplicationContext();
		handler = new Handler();
		timer = new Timer();
		TimerTask doAsynchronousTask = new TimerTask() {
			@Override
			public void run() {
				handler.post(new Runnable() {
					public void run() {
						try {
							new Communication().execute();
							System.out.println("pronto a receber");
						} catch (Exception e) {
						}
					}
				});
			}
		};
		timer.schedule(doAsynchronousTask, 0, 50);

		registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(receiverBt, filter2);
		filter2 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(receiverBt, filter2);

		super.onResume();
	}

	public class WifiReceiver extends BroadcastReceiver {

		public void onReceive(Context c, Intent intent) {
			System.out.println("recebeu wifi");
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
			double[][] wifiLocation = new double[3][4];
			double[] myLocation;
			double lat, lng;

			for (int i = 0; i < wifiList.size() && a < 3; i++) {
				ScanResult scan = wifiList.get(i);

				// POI Hash filtering
				// showing the three registered beacons with higher strength
				loc = positionsTable.get(scan.BSSID);
				if (a < 3 && loc != null) {
					lat_lng = loc.split(",");
					lat = Double.parseDouble(lat_lng[0]);
					lng = Double.parseDouble(lat_lng[1]);
					// eduroam && guest UL
					if (notRepeated(lat, lng, wifiLocation)) {
						wifiLocation[a][0] = lat;
						wifiLocation[a][1] = lng;
						wifiLocation[a][2] = scan.level;
						wifiLocation[a][3] = scan.frequency;
						a++;
					}
				}
			}

			if (a == 3) {
				myLocation = Utilities.padeiroTrilateration(wifiLocation[0], wifiLocation[1], wifiLocation[2]);
				GeoPoint geo = new GeoPoint(myLocation[0], myLocation[1]);
				mapView.getController().setCenter(geo);
				setLocation(geo);
			} else if (a == 2) {
				myLocation = Utilities.padeiroBilateration(wifiLocation[0], wifiLocation[1]);
				GeoPoint geo = new GeoPoint(myLocation[0], myLocation[1]);
				mapView.getController().setCenter(new GeoPoint(myLocation[0], myLocation[1]));
				setLocation(geo);
			}
		}
	}

	private void setLocation(GeoPoint geo) {
		drawPoint(geo, getResources().getDrawable(R.drawable.ic_maps_indicator_current_position), "place");
	}

	private void addMapObjective(Position pos) {
		drawPoint(new GeoPoint(pos.lat, pos.lng), getResources().getDrawable(R.drawable.objective_position), "objective");
		//TODO meter pontos com cores diferentes
	}

	private void drawPoint(GeoPoint geo, Drawable marker, String name) {
		OverlayItem overlayItem = new OverlayItem(name, "", geo);
		List<Overlay> mapOverlays = mapView.getOverlays();
		CustomOverLay overlays = new CustomOverLay(marker);

		if (name.equals("place")) {
			mapOverlays.remove(oldPos);
			oldPos = overlays;
		}

		overlays.addOverlayItem(overlayItem);

		mapOverlays.add(overlays);
	}
	
	private void cleanMap(){
		mapView.getOverlays().clear();
		if(oldPos != null)
			mapView.getOverlays().add(oldPos);
	}

	private boolean notRepeated(double lat, double lng, double[][] wifiLocation) {

		for (double[] loc : wifiLocation) {
			if (loc[0] == lat && loc[1] == lng)
				return false;
		}

		return true;
	}

	private class CustomOverLay extends ItemizedOverlay<OverlayItem> {

		private ArrayList<OverlayItem> overlayItems = new ArrayList<OverlayItem>();

		public CustomOverLay(Drawable drawable) {
			super(drawable, new DefaultResourceProxyImpl(getApplicationContext()));
		}

		public void addOverlayItem(OverlayItem item) {
			overlayItems.add(item);
			populate();
		}

		@Override
		protected OverlayItem createItem(int index) {
			return overlayItems.get(index);
		}

		@Override
		public int size() {
			return overlayItems.size();
		}

		@Override
		protected boolean onTap(int index) {
			return true;
		}

		@Override
		public boolean onSnapToItem(int arg0, int arg1, Point arg2, IMapView arg3) {
			return false;
		}
	}

	public class BtReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("recebeu bt");

			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
			}
		}
	}

	public class Communication extends AsyncTask<Void, Void, Message> {

		@Override
		protected Message doInBackground(Void... params) {
			return Utilities.receive();
		}

		@Override
		protected void onPostExecute(Message message) {
			if (message == null)
				return;

			System.out.println("recebi msg: " + message.getType());
			switch (message.getType()) {
				case Message.NEW_GAME:
					break;

				case Message.START_GAME:
					break;

				case Message.END_GAME:
					break;

				case Message.ADD_PLAYER:
					break;
				case Message.ELIMINATE_PLAYER:
					Toast.makeText(getApplicationContext(), "FOSTE ELIMINADO", Toast.LENGTH_LONG).show();
					try {
						Thread.sleep(Toast.LENGTH_LONG);
					} catch (InterruptedException e) {
					}
					System.exit(0);
					break;
				case Message.SET_OBJECTIVE:

					break;
				case Message.COMPLETE_GOAT_OBJECTIVE:
					completeObjective(message);
					break;
				case Message.COMPLETE_WOLF_OBJECTIVE:
					completeObjective(message);
					break;
				case Message.COMPLETE_GLOBAL_OBJECTIVE:
					completeObjective(message);
					break;
				case Message.SET_OBJECTIVE_PLACE:

					break;
				case Message.LIST_GAMES:

					break;
				case Message.JOIN_GAME:
					break;

				case Message.OBJECTIVE_PLACES:
					break;

				case Message.GET_NAME_BY_BMAC:
					break;

				case Message.LIST_OBJECTIVE:
					System.out.println("vou meter pontos");

					for (int i = 0; i < message.getElements().size(); i++) {
						Objective o = (Objective) message.getElement(i);
						Dispensa.objectives.add(o);
					}
					displayObjectives();

					break;

				case Message.PROCESS_QRCODE:
					Toast.makeText(getApplicationContext(), (String) message.getElement(), Toast.LENGTH_LONG).show();
					break;
					
				case Message.UPGRADE_WEAPON:
					player.upgradeWeapon();
					updateStatus();
					break;
					
				case Message.UPGRADE_STATUS:
					upgradePlayer(message);
					break;
			}
		}

		private void upgradePlayer(Message message) {
			Player player = (Player) message.getElement();
			Dispensa.player = player;
			if(player.getFaction() == Player.GOAT)
				faccao.setText("GOAT");
			else
				faccao.setText("WOLF");
		}

		private void updateStatus() {
			Message message = new Message(Message.UPGRADE_STATUS);
			message.addElement(player);
			Utilities.send(message);
		}

		private void completeObjective(Message message) {
			Objective obj = (Objective) message.getElement();
			Dispensa.objectives.remove(obj);
			if(obj.isFaction())
				Toast.makeText(getApplicationContext(), "Faction objective complete", Toast.LENGTH_LONG).show();
			else
				Toast.makeText(getApplicationContext(), "Global objective complete", Toast.LENGTH_LONG).show();
			displayObjectives();
		}

		private void displayObjectives() {
			cleanMap();
			for (Objective o : Dispensa.objectives) {
				if (o.getType() != Objective.CHINAR) {
					addMapObjective(o.getPosition());
				}
			}
		}
	}
}

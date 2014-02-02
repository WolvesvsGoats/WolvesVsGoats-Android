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

import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wolvesvsgoats.data.Player;
import com.wolvesvsgoats.utils.Dispensa;
import com.wolvesvsgoats.utils.Message;
import com.wolvesvsgoats.utils.Utilities;

public class Teste extends Activity {

	private static final int ALWAYS_DISCOVERABLE = 9459328;
	private Button next;
	private EditText player;
	private ListView list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		int ip = ((WifiManager) getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getIpAddress();
		((WifiManager) getSystemService(Context.WIFI_SERVICE)).setWifiEnabled(true);
		String ipAddress = Formatter.formatIpAddress(ip);
		String macB = BluetoothAdapter.getDefaultAdapter().getAddress();
		Dispensa.player = new Player("", ipAddress, macB);

		System.out.println("vou abrir cenas");
		
		Utilities.openConnections(Dispensa.player);
		
		System.out.println("abri cenas");

		setContentView(R.layout.start);

		Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
		startActivityForResult(discoverableIntent, ALWAYS_DISCOVERABLE);

		player = (EditText) findViewById(R.id.playerName);
		list = (ListView) findViewById(R.id.lista);

		Message m = new Message(Message.LIST_GAMES);
		System.out.println("vou pedir lista");
		new Communication().execute(m);
		System.out.println("agora tem que estar tudo bem");

		next = (Button) findViewById(R.id.buttonNext);
		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String name = player.getText().toString();
				if (list.getCheckedItemCount() != 1) {
					AudioManager am1 = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
					Log.e("am1.isWiredHeadsetOn()", am1.isWiredHeadsetOn() + "");
					Log.e("am1.isMusicActive()", am1.isMusicActive() + "");
					Log.e("am1.isSpeakerphoneOn()", am1.isSpeakerphoneOn() + "");
					Toast.makeText(getApplicationContext(), "Tem que seleccionar um jogo", Toast.LENGTH_LONG).show();
				} else {
					if (name.equals("")) {
						Toast.makeText(getApplicationContext(), "Tem que introduzir um nome", Toast.LENGTH_LONG).show();
					} else {
						Dispensa.player.setName(name);
						Intent i = new Intent(getApplicationContext(), MainActivity.class);
						i.putExtra("name", name);
						startActivityForResult(i, 1);
						finish();
					}
				}
			}
		});
	}

	private class Communication extends AsyncTask<Object, Void, Message> {

		@Override
		protected Message doInBackground(Object... params) {
			try {
				System.out.println("[THREAD] enviar");
				Utilities.send((Message) params[0]);
				System.out.println("[THREAD] receber");
				return Utilities.receive();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(Message message) {
			if (message == null)
				return;
			switch (message.getType()) {
				case Message.LIST_GAMES:
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_single_choice) {
						@Override
						public View getView(int position, View convertView, ViewGroup parent) {
							View view = super.getView(position, convertView, parent);
							TextView text = (TextView) view.findViewById(android.R.id.text1);
							text.setTextColor(Color.BLACK);
							return view;
						}
					};
					@SuppressWarnings("unchecked")
					List<String> cenas = (List<String>) message.getElement();
					for (String s : cenas) {
						adapter.add(s);
					}
					list.setAdapter(adapter);
					break;
			}
		}
	}
}

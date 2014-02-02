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
package com.wolvesvsgoats.utils;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.net.SocketFactory;

import com.wolvesvsgoats.data.Player;

public class Utilities {

	public static double calcDistance(double rssi, double freq) {
		double exp = (27.55 - (20 * Math.log10(freq)) - rssi) / 20.0;
		return Math.pow(10.0, exp);
	}

	// Description of calDistToDeg function
	//
	// To get the myLocation, rssi distance should be converted into
	// latitude and longitude unit.
	// This function convert rssi distance into lat long decimal unit.
	//
	public static double calDistToDeg(double dist) {
		double result;

		final int lat = 42;
		final double a = 6378137;
		final double b = 6356752.3;
		final double ang = lat * (Math.PI / 180);

		// This function will calculate the longitude distance based on the
		// latitude
		// More information is
		// http://en.wikipedia.org/wiki/Geographic_coordinate_system#Expressing_latitude_and_longitude_as_linear_units

		result = Math.cos(ang) * Math.sqrt((Math.pow(a, 4) * (Math.pow(Math.cos(ang), 2)) + (Math.pow(b, 4) * (Math.pow(Math.sin(ang), 2)))) / (Math.pow((a * Math.cos(ang)), 2) + Math.pow((b * Math.sin(ang)), 2))) * Math.PI / 180;

		return result;

	}

	public static double[] myRotation(double x, double y, double dist, double deg) {

		double[] myLocation = new double[3];

		myLocation[0] = x * Math.cos((Math.PI / 180) * deg) - y * Math.sin((Math.PI / 180) * deg);
		myLocation[1] = x * Math.sin((Math.PI / 180) * deg) + y * Math.cos((Math.PI / 180) * deg);
		myLocation[2] = dist;

		return myLocation;
	}

	public static double[] MyTrilateration(double[] first, double[] sec, double[] third) {

		double[] tmpWAP1 = new double[3];
		double[] tmpWAP2 = new double[3];
		double[] tmpWAP3 = new double[3];

		double dist1, dist2, dist3;
		double tmpLat2, tmpLong2, tmpLat3, tmpLong3;
		double tmpSlide, deg;
		double MyLat, MyLong;

		double[] MyLocation = new double[2];

		dist1 = calDistToDeg(calcDistance(first[2], first[3]));
		dist2 = calDistToDeg(calcDistance(sec[2], sec[3]));
		dist3 = calDistToDeg(calcDistance(third[2], third[3]));

		tmpLat2 = sec[0] - first[0];
		tmpLong2 = sec[1] - first[1];
		tmpLat3 = third[0] - first[0];
		tmpLong3 = third[1] - first[1];

		tmpSlide = Math.sqrt(Math.pow(tmpLat2, 2) + Math.pow(tmpLong2, 2));

		// deg = (180 / Math.PI) * Math.acos(((Math.pow(tmpLat2,2) +
		// Math.pow(tmpSlide,2) - Math.pow(tmpLong2, 2)) /
		// (2*tmpLat2*tmpSlide)));
		deg = (180 / Math.PI) * Math.acos(Math.abs(tmpLat2) / Math.abs(tmpSlide));

		// 1 quadrant
		if ((tmpLat2 > 0 && tmpLong2 > 0)) {
			deg = 360 - deg;
		}
		// 2 quadrant
		else {
			if ((tmpLat2 < 0 && tmpLong2 > 0)) {
				deg = 180 + deg;
			}
			// 3 quadrant
			else {
				if ((tmpLat2 < 0 && tmpLong2 < 0)) {
					deg = 180 - deg;
				}
				// 4 quadrant
				else {
					if ((tmpLat2 > 0 && tmpLong2 < 0)) {
						;
					}
				}
			}
		}
		tmpWAP1[0] = 0.0;
		tmpWAP1[1] = 0.0;
		tmpWAP1[2] = dist1;
		tmpWAP2 = myRotation(tmpLat2, tmpLong2, dist2, deg);
		tmpWAP3 = myRotation(tmpLat3, tmpLong3, dist3, deg);

		MyLat = (Math.pow(tmpWAP1[2], 2) - Math.pow(tmpWAP2[2], 2) + Math.pow(tmpWAP2[0], 2)) / (2 * tmpWAP2[0]);

		MyLong = (Math.pow(tmpWAP1[2], 2) - Math.pow(tmpWAP3[2], 2) - Math.pow(MyLat, 2) + Math.pow(MyLat - tmpWAP3[0], 2) + Math.pow(tmpWAP3[1], 2)) / (2 * tmpWAP3[1]);

		MyLocation = myRotation(MyLat, MyLong, 0, -deg);

		MyLocation[0] = MyLocation[0] + first[0];
		MyLocation[1] = MyLocation[1] + first[1];

		return MyLocation;
	}

	public static double[] padeiroBilateration(double[] first, double[] sec) {
		double dist1 = calDistToDeg(calcDistance(first[2], first[3]));
		double dist2 = calDistToDeg(calcDistance(sec[2], sec[3]));

		double difLat = (first[0] - sec[0]) / 2 + dist1 - dist2;
		double difLong = (first[1] - sec[1]) / 2 + dist1 - dist2;
		double[] res = new double[2];

		res[0] = first[0] - difLat;
		res[1] = first[1] - difLong;
		return res;
	}

	public static double[] padeiroTrilateration(double[] first, double[] sec, double[] third) {

		double[] um = padeiroBilateration(first, sec);
		double[] dois = padeiroBilateration(first, third);
		double[] tres = padeiroBilateration(sec, third);

		double[] res = new double[2];

		res[0] = (um[0] + dois[0] + tres[0]) / 3;
		res[1] = (um[1] + dois[1] + tres[1]) / 3;

		return res;
	}

	// ///////////////////////NETWORKING

	public static final int BUFFER_SIZE = 65000;
	public static final String host = "194.117.20.243";
	public static final int hostPort = 50000;
	private static boolean socketOk = false;
//	private static ReentrantLock commLock = new ReentrantLock();

	public synchronized static void openConnections(final Player player) {
//		synchronized (Dispensa.commLock) {

			new Thread(new Runnable() {
				@Override
				public void run() {
					while (!socketOk) {
						try {
							if(Dispensa.socket != null){
								Dispensa.socket.close();
							}
							if(Dispensa.outStream != null){
								Dispensa.outStream.close();
							}
							if(Dispensa.inStream != null){
								Dispensa.inStream.close();
							}
							
							SocketFactory socks = SocketFactory.getDefault();
							Dispensa.socket = socks.createSocket(host, hostPort);
							Dispensa.socket.setKeepAlive(true);
							Dispensa.socket.setReuseAddress(true);
							Dispensa.outStream = new ObjectOutputStream(Dispensa.socket.getOutputStream());
							Dispensa.inStream = new ObjectInputStream(Dispensa.socket.getInputStream());
							
							System.out.println("socket: "+Dispensa.socket);
							System.out.println("out: "+Dispensa.outStream);
							System.out.println("in: "+Dispensa.inStream);
							
							Message message = new Message(Message.REGISTER_PLAYER);
							message.addElement(player);
							Dispensa.outStream.writeObject(message);
							socketOk = true;
							System.out.println("abri tudo");
						} catch (Exception e) {
							socketOk = false;
							System.err.println("conneccao falhou");
							try {
								Thread.sleep(1000);
							} catch (Exception e1) {
								System.err.println("PORQUE CARALHO NAO ME DEIXAM DORMIR????");
								e1.printStackTrace();
							}
						}
					}
				}
			}).start();
//		}
	}

	public static void send(final Message message) {
//		synchronized (Dispensa.commLock) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					boolean done = false;
					while (!done) {
						try {
							System.out.println("[SENDING THREAD] vou enviar msg");
							Dispensa.outStream.writeObject(message);
							System.out.println("[SENDING THREAD] enviada");
							done = true;
						} catch (Exception e) {
							System.out.println("envio falhou");
							try {
								Thread.sleep(1000);
							} catch (Exception e1) {
								System.err.println("PORQUE CARALHO NAO ME DEIXAM DORMIR????");
								e1.printStackTrace();
							}
							socketOk = false;
							openConnections(Dispensa.player);
						}
					}
				}
			}).start();
//		}
	}

	public synchronized static Message receive() {
//		synchronized (Dispensa.commLock) {
			while (true) {
				try {
					Message res = (Message) Dispensa.inStream.readObject();
					return res;
				} catch (Exception e) {
					System.out.println("recepcao falhou");
					try {
						Thread.sleep(1000);
					} catch (Exception e1) {
						System.err.println("PORQUE CARALHO NAO ME DEIXAM DORMIR????");
						e1.printStackTrace();
					}
					socketOk = false;
					openConnections(Dispensa.player);
				}
			}
//		}
	}
}
/*
 * public static void send(Message message, SocketChannel socket) {
 * System.out.println("[Utilities] vou enviar cenas"); ByteArrayOutputStream out
 * = new ByteArrayOutputStream(BUFFER_SIZE); ObjectOutputStream oos;
 * 
 * try { oos = new ObjectOutputStream(out); oos.writeObject(message); byte[]
 * buffer = out.toByteArray();
 * 
 * socket.write(ByteBuffer.wrap(buffer));
 * 
 * } catch (IOException e) { e.printStackTrace(); }
 * System.out.println("[Utilities] enviei"); } /* public static Message
 * receive(SocketChannel socket) {
 * System.out.println("[Utilities] vou receber cenas"); ByteBuffer buffer =
 * ByteBuffer.allocate(Utilities.BUFFER_SIZE); ByteArrayInputStream in = new
 * ByteArrayInputStream(buffer.array()); Message m = null; try {
 * socket.read(buffer); ObjectInputStream ios = new ObjectInputStream(in); m =
 * (Message) ios.readObject();
 * 
 * } catch (Exception e) { e.printStackTrace(); }
 * 
 * return m; }
 */

/*
 * public static ConnectionStruct sendAndWait(ConnectionStruct c, DatagramSocket
 * socket) { ByteArrayOutputStream out = new ByteArrayOutputStream(BUFFER_SIZE);
 * ObjectOutputStream oos;
 * 
 * Message m = c.message; DatagramPacket sendPacket = null; ConnectionStruct cs
 * = null; boolean done = false;
 * 
 * try { oos = new ObjectOutputStream(out); oos.writeObject(m); byte[] buffer =
 * out.toByteArray(); sendPacket = new DatagramPacket(buffer, buffer.length,
 * c.address, c.port);
 * 
 * } catch (IOException e) { e.printStackTrace(); }
 * 
 * byte[] buffer = new byte[BUFFER_SIZE]; DatagramPacket receivePacket = new
 * DatagramPacket(buffer, buffer.length);
 * 
 * while (!done) { try { socket.send(sendPacket); socket.receive(receivePacket);
 * done = true; } catch (SocketTimeoutException e) {
 * System.out.println("resend"); } catch (IOException e) { e.printStackTrace();
 * } }
 * 
 * ByteArrayInputStream in = new ByteArrayInputStream(buffer); m = null; try {
 * ObjectInputStream ios = new ObjectInputStream(in); m = (Message)
 * ios.readObject();
 * 
 * } catch (Exception e) { e.printStackTrace(); } cs = new
 * ConnectionStruct(receivePacket.getAddress(), receivePacket.getPort(), m);
 * 
 * return cs; }
 * 
 * final Message message = new Message(Message.UPDATE);
 * message.addElement(player);
 * 
 * final Handler handler = new Handler(); Timer timer = new Timer(); TimerTask
 * ttask = new TimerTask() {
 * 
 * @Override public void run() { handler.post(new Runnable() {
 * 
 * @Override public void run() { try { new Communication().execute(message,
 * true); } catch (Exception e) { e.printStackTrace(); } } }); } }; // adiciona
 * a hashtable de timers timer.schedule(ttask, 0, TIME_OUT);
 * 
 * 
 * private void send(Message message) { ByteArrayOutputStream out = new
 * ByteArrayOutputStream(Utilities.BUFFER_SIZE); ObjectOutputStream oos;
 * 
 * try { oos = new ObjectOutputStream(out); oos.writeObject(message); byte[]
 * buffer = out.toByteArray();
 * 
 * } catch (IOException e) { e.printStackTrace(); } }
 * 
 * private Message receive() {
 * 
 * byte[] buffer = new byte[Utilities.BUFFER_SIZE]; ByteArrayInputStream in;
 * Message m = null; try { in = new ByteArrayInputStream(buffer);
 * ObjectInputStream ios = new ObjectInputStream(in); m = (Message)
 * ios.readObject();
 * 
 * } catch (IOException e) { e.printStackTrace(); } catch
 * (ClassNotFoundException e) { e.printStackTrace(); }
 * 
 * return m; }
 */
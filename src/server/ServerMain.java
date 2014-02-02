package server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

import com.wolvesvsgoats.data.Game;
import com.wolvesvsgoats.data.Player;
import com.wolvesvsgoats.objectives.Objective;
import com.wolvesvsgoats.utils.Content;
import com.wolvesvsgoats.utils.Message;
import com.wolvesvsgoats.utils.Position;

public class ServerMain {
	private static final int SERVER_PORT = 50000;

	public static void main(String[] args) {

		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(SERVER_PORT);
		} catch (IOException e2) {
			e2.printStackTrace();
			System.err.println("Não foi possível iniciar servidor");
			System.exit(-1);
		}

		GameController controller = new GameController();

		while (true) {
			Socket socket = null;
			Thread last = null;
			try {
				socket = serverSocket.accept();

				System.out.println("[receiver thread] aceitei socket");
				last = new Thread(new ServerController(socket, controller));
				last.start();

				socket = null;
				last = null;
			} catch (IOException e) {
				e.printStackTrace();
				/*
				 * try { last.join(); socket.close(); } catch (IOException e1) {
				 * e1.printStackTrace(); } catch (InterruptedException e1) {
				 * e1.printStackTrace(); } finally { socket = null; }
				 */
			}
		}
	}

	private static class ServerController implements Runnable {
		private ObjectInputStream inStream;
		private ObjectOutputStream outStream;
		private GameController gameController;
		private boolean alive;

		ServerController(Socket socket, GameController controller) {
			gameController = controller;
			try {
				inStream = new ObjectInputStream(socket.getInputStream());
				outStream = new ObjectOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			String line;
			Message message, answer = null;
			alive = true;

			while (alive) {
				try {
					message = receive();
					answer = null;
					if (message != null) {

						System.out.println("recebi msg: " + message.getType());

						switch (message.getType()) {
							case Message.NEW_GAME:
								line = (String) message.getElement(0);
								gameController.newGame(line);
								break;

							case Message.START_GAME:
								gameController.startGame();
								break;

							case Message.END_GAME:
								gameController.endGame();
								break;

							case Message.ADD_PLAYER:
								gameController.addPlayer(message);

								break;
							case Message.ELIMINATE_PLAYER:

								break;
							case Message.SET_OBJECTIVE:

								break;
							case Message.COMPLETE_GOAT_OBJECTIVE:

								break;
							case Message.COMPLETE_WOLF_OBJECTIVE:

								break;
							case Message.COMPLETE_GLOBAL_OBJECTIVE:

								break;
							case Message.SET_OBJECTIVE_PLACE:

								break;
							case Message.LIST_GAMES:
								answer = gameController.listGames();
								break;
							case Message.JOIN_GAME:
								gameController.joinGame(message);
								break;

							case Message.OBJECTIVE_PLACES:
								gameController.setObjectivePlaces(message);
								break;

							case Message.GET_NAME_BY_BMAC:
								answer = gameController.getNameByMac(message);
								break;

							case Message.LIST_OBJECTIVE:

								gameController.listObjectives(message);

								break;

							case Message.PROCESS_QRCODE:

								gameController.processQRCode(message);
								break;

							case Message.UPDATE:

								break;

							case Message.CHINAR:
								gameController.processChinada(message);
								break;

							case Message.REGISTER_PLAYER:
								gameController.registerPlayer(message, outStream);
								break;

							case Message.UPGRADE_STATUS:
								gameController.upgradeStatus(message);
								break;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (answer != null) {
					send(answer);
				}
			}
			return;
		}

		private void send(Message message) {
			try {
				outStream.writeObject(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private Message receive() {
			try {
				return (Message) inStream.readObject();
			} catch (EOFException e) {
				alive = false;
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				alive = false;
				return null;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return null;
			} catch (NullPointerException e) {
				System.err.println("é desta...?");
				try {
					inStream.close();
					outStream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				alive = false;
				Thread.currentThread().interrupt();

				return null;
			} catch (Exception e) {
				System.err.println("Problemas de ligação; ligação vai terminar");
				try {
					inStream.close();
					outStream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				alive = false;
				Thread.currentThread().interrupt();

				return null;
			}
		}

	}

	private static class GameController {
		private int playerCounter = 0;
		private static Game game;
		private final int NUMBER_GLOBAL_OBJECTIVES = 5;
		private final int NUMBER_FACTION_OBJECTIVES = 3;
		private LinkedList<Position> DEFAULT_OBJECTIVE_POSIITONS;
		private Hashtable<Position, Content> qrCodes;
		private ReentrantLock lock = new ReentrantLock();
		private Hashtable<String, ObjectOutputStream> connections;

		public GameController() {
			connections = new Hashtable<String, ObjectOutputStream>();
			qrCodes = new Hashtable<Position, Content>();
			DEFAULT_OBJECTIVE_POSIITONS = new LinkedList<Position>();
			DEFAULT_OBJECTIVE_POSIITONS.add(new Position(38.755265, -9.157729, "ponto um"));
			DEFAULT_OBJECTIVE_POSIITONS.add(new Position(38.755391, -9.158008, "ponto dois"));
			DEFAULT_OBJECTIVE_POSIITONS.add(new Position(38.755637, -9.158121, "ponto tres"));
			DEFAULT_OBJECTIVE_POSIITONS.add(new Position(38.756121, -9.157737, "ponto quatro"));
			DEFAULT_OBJECTIVE_POSIITONS.add(new Position(38.756202, -9.158183, "ponto cinco"));
			DEFAULT_OBJECTIVE_POSIITONS.add(new Position(38.755273, -9.157957, "ponto seis"));
			DEFAULT_OBJECTIVE_POSIITONS.add(new Position(38.755817, -9.158212, "ponto sete"));
			DEFAULT_OBJECTIVE_POSIITONS.add(new Position(38.755355, -9.157515, "ponto oito"));
			DEFAULT_OBJECTIVE_POSIITONS.add(new Position(38.755895, -9.158315, "ponto nove"));
			DEFAULT_OBJECTIVE_POSIITONS.add(new Position(38.756054, -9.157671, "ponto dez"));
			DEFAULT_OBJECTIVE_POSIITONS.add(new Position(38.756131, -9.158476, "ponto onze"));

			newGame("Teste");
			startGame();
		}

		private void upgradeStatus(Message message) {
			Player newPlayer = (Player) message.getElement();
			for (Player p : game.getPlayers()) {
				if (p.getName().equals(newPlayer.getName())) {
					game.getPlayers().remove(p);
					game.getPlayers().add(newPlayer);
					return;
				}
			}
		}

		void registerPlayer(Message message, ObjectOutputStream outStream) {
			synchronized (lock) {
				Player p = (Player) message.getElement();
				if (!p.getIp().equals(""))
					connections.put(p.getIp(), outStream);
			}
		}

		void sendToPlayer(Player player, Message message) {
			synchronized (lock) {
				ObjectOutputStream out = connections.get(player.getIp());
				try {
					out.writeObject(message);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		void processChinada(Message message) {
			synchronized (lock) {
				String chinado = (String) message.getElement();
				Player player = (Player) message.getElement(1);
				for (Player p : game.getPlayers()) {
					if (p.getName().equals(chinado)) {
						checkChinadaObjective(player, p);
						eliminatePlayer(p);
						return;
					}
				}
			}
		}

		private void checkChinadaObjective(Player chinador, Player chinado) {
			if (chinador.getFaction() == chinado.getFaction())
				return;

			if (chinador.getFaction() == Player.GOAT) {
				for (Objective o : game.getGoatObjectives()) {
					if (o.getType() == Objective.CHINAR) {
						completeFactionObjective(o, true);
						return;
					}
				}
			} else {
				for (Objective o : game.getWolfObjectives()) {
					if (o.getType() == Objective.CHINAR) {
						completeFactionObjective(o, false);
						return;
					}
				}
			}
		}

		void eliminatePlayer(Player p) {
			Message message = new Message(Message.ELIMINATE_PLAYER);
			sendToPlayer(p, message);
		}

		void processQRCode(Message message) {
			synchronized (lock) {
				Player player = (Player) message.getElement();
				String qrCode = (String) message.getElement(1);

				if (player.getFaction() == Player.GOAT) {
					for (Objective o : game.getGoatObjectives()) {
						if ((o.getType() == Objective.GET_WEAPON || o.getType() == Objective.SIMPLE) && o.getPosition().qrString.equals(qrCode)) {
							completeFactionObjective(o, true);
							break;
						}
					}
				} else if (player.getFaction() == Player.WOLF) {
					for (Objective o : game.getWolfObjectives()) {
						if ((o.getType() == Objective.GET_WEAPON || o.getType() == Objective.SIMPLE) && o.getPosition().qrString.equals(qrCode)) {
							completeFactionObjective(o, false);
							break;
						}
					}
				}
				for (Objective o : game.getGlobalObjectives()) {
					if (o.getPosition().qrString.equals(qrCode)) {
						completeGLobalObjective(o);
						break;
					}
				}
				Position pos = null;
				for (Position p : DEFAULT_OBJECTIVE_POSIITONS) {
					if (p.qrString.equals(qrCode)) {
						pos = p;
						break;
					}
				}
				if (pos == null)
					return;
				Content cnt = qrCodes.get(pos);
				if (cnt != null)
					switch (cnt.type) {
						case Content.EMPTY:
							// TODO set trap
							break;
						case Content.TRAP:
							eliminatePlayer(player);
							break;
						case Content.WEAPON:
							upgradeWeapon(player);
							break;
					}
			}
		}

		private void upgradeWeapon(Player player) {
			Message m = new Message(Message.UPGRADE_WEAPON);
			sendToPlayer(player, m);
		}

		void setObjectivePlaces(Message message) {
			synchronized (lock) {
				ArrayList<Position> list = (ArrayList<Position>) message.getElement(0);
				game.setObjectivePositions(list);
			}
		}

		void completeGLobalObjective(Objective o) {
			synchronized (lock) {
				game.getGlobalObjectives().remove(o);
				Message message = new Message(Message.COMPLETE_GLOBAL_OBJECTIVE);
				message.addElement(o);
				for (Player p : game.getPlayers()) {
					sendToPlayer(p, message);
				}

				// TODO add new objective
			}
		}

		void completeFactionObjective(Objective o, boolean goat) {
			synchronized (lock) {
				if (goat) {
					game.getGoatObjectives().remove(o);
					Message message = new Message(Message.COMPLETE_GOAT_OBJECTIVE);
					message.addElement(o);
					for (Player p : game.getPlayers()) {
						if (p.getFaction() == Player.GOAT)
							sendToPlayer(p, message);
					}
				} else {
					game.getWolfObjectives().remove(o);
					Message message = new Message(Message.COMPLETE_WOLF_OBJECTIVE);
					message.addElement(o);
					for (Player p : game.getPlayers()) {
						if (p.getFaction() == Player.WOLF)
							sendToPlayer(p, message);
					}
				}
			}
		}

		Message listObjectives(Message message) {
			// zombie camandros = very stronk
			synchronized (lock) {
				int faction = ((Player) message.getElement()).getFaction();

				Message m = new Message(Message.LIST_OBJECTIVE);
				System.out.println("listar objectivos");
				for (Objective o : game.getGlobalObjectives()) {
					m.addElement(o);
				}
				if (faction == Player.GOAT) {
					for (Objective o : game.getGoatObjectives()) {
						m.addElement(o);
					}
				} else {
					for (Objective o : game.getWolfObjectives()) {
						m.addElement(o);
					}
				}

				return m;
			}
		}

		void newGame(String name) {
			synchronized (lock) {
				game = new Game(name);
				for (Position p : DEFAULT_OBJECTIVE_POSIITONS) {
					game.addObjectivePosition(p);
				}
				System.out.println("New game created: " + name);
			}
		}

		void startGame() {
			synchronized (lock) {
				Random r = new Random();
				Objective obj;
				Position pos;
				int type, place;
				List<Position> positions = (List<Position>) game.getObjectivePositions().clone();

				for (int i = 0; i < NUMBER_FACTION_OBJECTIVES; i++) {
					// add goat
					place = r.nextInt(positions.size());
					pos = positions.get(place);

					type = r.nextInt(4);
					obj = new Objective(pos, type, true);

					game.addGoatObjective(obj);
					positions.remove(place);

					// add wolf
					place = r.nextInt(positions.size());
					pos = positions.get(place);

					type = r.nextInt(4);
					obj = new Objective(pos, type, true);

					game.addWolfObjective(obj);
					positions.remove(place);
				}

				for (int i = 0; i < NUMBER_GLOBAL_OBJECTIVES; i++) {

					place = r.nextInt(positions.size());
					pos = positions.remove(place);
					// only get there or get weapon
					type = r.nextInt(2);
					obj = new Objective(pos, type, false);

					game.addGlobalObjective(obj);
				}

				positions = game.getObjectivePositions();

				int aux;
				Content c;
				for (Position p : positions) {
					aux = r.nextInt(2);
					c = new Content();
					c.type = aux;
					qrCodes.put(p, c);
				}
			}
		}

		void endGame() {
			synchronized (lock) {
				game = null;
			}
		}

		void joinGame(Message message) {
			synchronized (lock) {
				Player player = (Player) message.getElement(0);
				if (playerCounter % 2 == 0) {
					player.setFaction(Player.GOAT);
					if (playerCounter == 0) {
						player.setDetector(true);
					}
				} else {
					player.setFaction(Player.WOLF);
					if (playerCounter == 1) {
						player.setDetector(true);
					}
				}

				game.addPlayer(player);
				playerCounter++;

				System.out.println("Novo jogador: " + player.getName());

				sendToPlayer(player, listObjectives(message));
				Message pl = new Message(Message.UPGRADE_STATUS);
				pl.addElement(player);
				sendToPlayer(player, pl);
			}
		}

		Message listGames() {
			synchronized (lock) {
				List<String> games = new LinkedList<String>();
				// only one game at a time for now
				games.add(game.getGameName());

				Message message = new Message(Message.LIST_GAMES);
				message.addElement(games);

				return message;
			}
		}

		void addPlayer(Message message) {
			synchronized (lock) {
				Player player = (Player) message.getElement();
				game.addPlayer(player);
				ArrayList<Player> players = game.getPlayers();
				System.out.println("Current Players: " + players);
			}
		}

		Message getNameByMac(Message message) {
			synchronized (lock) {
				String name = "";
				String mac = (String) message.getElement();
				for (Player p : game.getPlayers()) {
					System.out.println(p.getBluetoothMac());
					if (p.getBluetoothMac().equals(mac)) {
						name = p.getName();
						break;
					}
				}

				Message m = new Message(Message.GET_NAME_BY_BMAC);
				m.addElement(name);
				return m;
			}
		}
	}
}

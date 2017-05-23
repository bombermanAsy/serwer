package main;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Serwer {

	private final int ilosc_graczy = 3;
	private static final int PORT = 1306;
	private final ExecutorService executor = Executors.newFixedThreadPool(8);
	private ArrayList<Players> players;
	private ArrayList<ObjectOutputStream> outs;
	private ArrayList<ObjectInputStream> ins;
	private static int zalogowani = 0;
	private static int pl_num = 0;
	private static boolean[] myNumber;

	private final int[] posX = { 50, 650, 50, 650 };
	private final int[] posY = { 50, 50, 550, 550 };

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Serwer() {
		System.out.println("Serwer jedzie");
		players = new ArrayList();
		outs = new ArrayList();
		ins = new ArrayList();
		
		myNumber = new boolean[ilosc_graczy];
		for (int i = 0; i < myNumber.length; i++) {
			myNumber[i] = true;
		}

		try (ServerSocket serverSocket = new ServerSocket(PORT)) {
			System.out.println("TCP started");
			while (true) {
				final Socket socket = serverSocket.accept();
				executor.submit(() -> makeConnection(socket, zalogowani++));
			}
		} catch (IOException x) {
			System.out.println("Wyj¹tek I/O");
		} catch (Exception e) {
			System.out.println("Nie uda³o siê po³¹czyæ");
		}
	}

	private void makeConnection(Socket socket, int logged) {
		try {
			synchronized (executor) {
				outs.add(new ObjectOutputStream(socket.getOutputStream()));
				ins.add(new ObjectInputStream(socket.getInputStream()));
			}
			ObjectOutputStream out = outs.get(logged);

			// zalogowal sie
			if (players.size() < ilosc_graczy) {
				players.add(new Players(posX[players.size()], posY[players.size()]));
				out.writeObject(100);
				out.writeObject(pl_num);
				out.writeObject(posX[players.size() - 1]);
				out.writeObject(posY[players.size() - 1]);

				/*int pl_num = 0;
				for (pl_num = 0; pl_num < myNumber.length; pl_num++) {
					if (myNumber[pl_num] == true) {
						break;
					}
				}*/
				
				
				System.out.println("Player: " + pl_num + " logged");
				pl_num++;
				
				synchronized (executor) {
					try {
						while (zalogowani < ilosc_graczy) {
							executor.wait();
						}
						if (logged == ilosc_graczy - 1)
							executor.notifyAll();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				out.writeObject(players.size() - 1);
				for (int i = 0; i < players.size(); i++) {
					if (i != logged) {
						out.writeObject(i);
						out.writeObject(players.get(i).getPos().x);
						out.writeObject(players.get(i).getPos().y);
					}
				}

			} else {
				out.writeObject(0);
			}

/////////////////////////////////////////////////////////////////////
			//new UDPMain(PORT).start();

			listen(socket, logged);

		} catch (Exception e) {
			System.out.println("Wyj¹tek I/O - metoda odbierz w serwerze");
			e.printStackTrace();
			System.out.println(e.getMessage());
		} finally {
			try {
				socket.close();
			} catch (Exception e) {
				System.out.print("Nie uda³o siê zamkn¹æ socketa - serwer");
			}
		}
	}

	private void listen(Socket socket, int logged) {
		ObjectInputStream in = ins.get(logged);
		while (true) {
			try {

				char opt = (char) in.readObject();
				switch (opt) {
				case 'a':
					// bomba
					int x = (int) in.readObject();
					int y = (int) in.readObject();
					plantBomb(logged, x, y);
					break;
				case 'b':
					closeAll(socket, logged);
					break;

				case 'c': // receive position from 'who'
					int pos_x = (int) in.readObject();
					int pos_y = (int) in.readObject();
					int who = (int) in.readObject();
					move(logged, pos_x, pos_y, who);
					break;

				}

			} catch (Exception e) {
				break;
			}
		}

	}

	private void closeAll(Socket socket, int logged) {
		try {
			ins.get(logged).close();
			outs.get(logged).close();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void plantBomb(int logged, int x, int y) {
		for (int i = 0; i < outs.size(); i++) {
			if (i != logged) {
				ObjectOutputStream out = outs.get(i);
				try {
					out.writeObject('a');
					out.writeObject(x);
					out.writeObject(y);
				} catch (Exception e) {
					// e.printStackTrace();
				}

			}
		}
	}

	private  synchronized  void move(int logged, int pos_x, int pos_y, int who) {
		for (int i = 0; i < outs.size(); i++) {
			if (i != logged) { // do wszystkich oprócz tego co wys³a³
				//System.out.println(
				//		"Serwer przesuwa gracza: " + who + " u: " + i + 
				//		" na pozycje: " + pos_x + ", " + pos_y);
				ObjectOutputStream out = outs.get(i);
				try {
					out.writeObject('c');
					out.writeObject(pos_x);
					out.writeObject(pos_y);
					out.writeObject(who);
				} catch (Exception e) {
					//e.printStackTrace();
				}
			}
		}
	}
}

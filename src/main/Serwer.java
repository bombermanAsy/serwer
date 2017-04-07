package main;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.awt.Point;

public class Serwer {

    private static final int PORT = 1306;
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private ArrayList<Players> players;
    
    private final int[] posX = {50, 650, 50, 650};
    private final int[] posY = {50, 50, 550, 550};
    

    public Serwer() { 	
    	System.out.println("Serwer jedzie");
    	players = new ArrayList();
    	/*
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                final Socket socket = serverSocket.accept();
                executor.submit(() -> makeConnection(socket));
            }
        } catch (IOException x) {
            System.out.println("Wyj¹tek I/O");
        } catch (Exception e) {
            System.out.println("Nie uda³o siê po³¹czyæ");
        }
        */
    	try (ServerSocket serverSocket = new ServerSocket(PORT)) {
    		while(true) {
    			final Socket socket = serverSocket.accept();
                executor.submit(() -> makeConnection(socket));
    		}
        } catch (IOException x) {
            System.out.println("Wyj¹tek I/O");
        } catch (Exception e) {
            System.out.println("Nie uda³o siê po³¹czyæ");
        }
    	
    }

    private void makeConnection(Socket socket) {
        try {

            	ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            	int option = (int) in.readObject();
            	
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                switch (option) {
                case 1:
                	//zalogowal sie      	
                	if (players.size() < 4) {
                		players.add(new Players(posX[players.size()], posY[players.size()]));
                		out.writeObject(posX[players.size() - 1]);
                		out.writeObject(posY[players.size() - 1]);
                		
                		while (players.size() != 4) {
                		out.writeObject(players.size() - 1);
                		for (int i=0; i < players.size() - 1; i++) {
                			out.writeObject(players.get(i).getPos().x);
                			out.writeObject(players.get(i).getPos().y);
                		}
                		}
               		} 
               		else {
               			out.writeObject(0);
               		}
                	
                	break;
                case 2:
                	// postawil bombe
                	
                	
                	break;
                case 3:
                	// zginal
                	
                	
                	break;
                }
                in.close();
                out.close();
        } catch (Exception e) {
            System.out.println("Wyj¹tek I/O - metoda odbierz w serwerze");
            System.out.println(e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (Exception e) {
                System.out.print("Nie uda³o siê zamkn¹æ socketa - serwer");
            }
        }
    }

}

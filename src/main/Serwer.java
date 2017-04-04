package main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Serwer {

    private static final int PORT = 1306;
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private int numOfPlayers = 0;
    
    private final int[] posX = {50, 650, 50, 650};
    private final int[] posY = {50, 50, 550, 550};
    

    public Serwer() { 	
    	System.out.println("Serwer jedzie");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                final Socket socket = serverSocket.accept();
                executor.submit(() -> makeConnection(socket));
            }
        } catch (IOException x) {
            System.out.println("Wyj�tek I/O");
        } catch (Exception e) {
            System.out.println("Nie uda�o si� po��czy�");
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
                	if (numOfPlayers < 4) {
                		out.writeObject(posX[numOfPlayers]);
                		out.writeObject(posY[numOfPlayers]);
               			numOfPlayers++;
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
            System.out.println("Wyj�tek I/O - metoda odbierz w serwerze");
            System.out.println(e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (Exception e) {
                System.out.print("Nie uda�o si� zamkn�� socketa - serwer");
            }
        }
    }

}

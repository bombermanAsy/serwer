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

	private final int ilosc_graczy = 4;
    private static final int PORT = 1306;
    private final ExecutorService executor = Executors.newFixedThreadPool(8);
    private ArrayList<Players> players;
    private ArrayList<ObjectOutputStream> outs;
    private ArrayList<ObjectInputStream> ins;
    private static int zalogowani = 0;
    
    
    private final int[] posX = {50, 650, 50, 650};
    private final int[] posY = {50, 50, 550, 550};
    

    public Serwer() { 	
    	System.out.println("Serwer jedzie");
    	players = new ArrayList();
    	outs = new ArrayList();
    	ins = new ArrayList();
    	
    	try (ServerSocket serverSocket = new ServerSocket(PORT)) {
    		while(true) {
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
                
            //zalogowal sie      	
            if (players.size() < ilosc_graczy) {
                players.add(new Players(posX[players.size()], posY[players.size()]));
                out.writeObject(posX[players.size() - 1]);
                out.writeObject(posY[players.size() - 1]);
                		
                synchronized(executor) {
                	try {
                		while(zalogowani < ilosc_graczy) {
                			executor.wait();
                		}
                		if (logged == ilosc_graczy-1) executor.notifyAll();
                		} catch (InterruptedException e) {
                			e.printStackTrace();
                	}
                }
                out.writeObject(players.size() - 1);
                for (int i=0; i < players.size(); i++) {
                	if (i!=logged) {
                		out.writeObject(players.get(i).getPos().x);
                		out.writeObject(players.get(i).getPos().y);
                	}
                }
                		
            } 
            else {
               	out.writeObject(0);
            } 	
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
			   
			   int opt = (int) in.readObject();
			   switch(opt) {
			   case 1:
				   //bomba
				   int x = (int) in.readObject();
				   int y = (int) in.readObject();
				   plantBomb(logged, x, y);
				   break;
			   case 2:
				   closeAll(socket, logged);
				   break;
			   }
			   
			   
		   }
		   catch (Exception e) {
			   //e.printStackTrace();
			   break;
		   }
	   }
   }
    
   private void closeAll(Socket socket, int logged) {
	   try {
		   ins.get(logged).close();
		   outs.get(logged).close();
		   socket.close();
	   }
	   catch (Exception e) {
		   e.printStackTrace();
	   }
   }

   private void plantBomb(int logged, int x, int y) {
	   for (int i = 0; i < outs.size(); i++) {
		   if (i != logged) {
			   ObjectOutputStream out = outs.get(i);
			   try {
				   out.writeObject(1);
				   out.writeObject(x);
				   out.writeObject(y);
			   } catch (Exception e) {
				   //e.printStackTrace();
			   }
			   
		   }
	   }
   }
}

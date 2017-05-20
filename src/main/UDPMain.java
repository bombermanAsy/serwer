package main;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class UDPMain extends Thread {

	public int PORT;
	private float posX, posY;
	private int who;
	
	public UDPMain (int PORT) {
		this.PORT = PORT;
	}
	
	@Override
	public void run() {
		try (DatagramSocket datagramSocket = new DatagramSocket(PORT)) {

			System.out.println("Multithreading UDP Started");
			/*InetSocketAddress inet = new InetSocketAddress("localhost", PORT);
			datagramSocket.bind(inet);*/

			while (true) {

				byte[] buffer = new byte[50];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

				try {

					datagramSocket.receive(packet);
					
					ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
					DataInputStream dais = new DataInputStream(bais);

					posX = dais.readFloat();
					posY = dais.readFloat();
					who = dais.readInt();

					System.out.println(who + " -> " + posX + " :: " + posY + 
							" id: " + this.getId());
					/*try {
						sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}*/

					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					DataOutputStream daos = new DataOutputStream(baos);

					/*if(posX > 150 || posY > 150) {
						posX = 50;
						posY = 50;
					}*/
					
					daos.writeFloat(posX);
					daos.writeFloat(posY);
					daos.writeInt(who);

					byte[] buffer2 = baos.toByteArray();
					InetAddress address = packet.getAddress();
					int port = packet.getPort();

					packet = new DatagramPacket(buffer2, buffer2.length, address, port);
					datagramSocket.send(packet);
					
					//new UDPService(datagramSocket, buffer, packet).start();

				} catch (IOException e) {
					System.out.println("Loop IOException Exc: " + e.getMessage());
				}

			}

		} catch (SocketException e) {
			System.out.println("Main Socket Exc: " + e.getMessage());
		}
	}
}

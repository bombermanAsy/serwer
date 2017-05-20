package main;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPService extends Thread {

	private DatagramSocket datagramSocket;
	private byte[] buffer;
	private DatagramPacket packet;
	private float posX, posY;
	private int who;

	public UDPService(DatagramSocket datagramSocket, byte[] buffer, DatagramPacket packet) {
		this.datagramSocket = datagramSocket;
		this.buffer = buffer;
		this.packet = packet;
	}

	@Override
	public void run() {

		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
			DataInputStream dais = new DataInputStream(bais);

			posX = dais.readFloat();
			posY = dais.readFloat();
			who = dais.readInt();

			System.out.println(who + " -> " + posX + " :: " + posY + 
					" id: " + this.getId());
			try {
				sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

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
		} catch (IOException e) {
			System.out.println("UDPService IOException: " + e.getMessage());
		}
	}
}

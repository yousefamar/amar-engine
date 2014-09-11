package com.metaplains.net;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.metaplains.core.GameClient;
import com.metaplains.net.packets.*;

public class NetIOManager {
	
	public long ping;
	public long lastPing;
	
	private Socket clientSoc;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	
	private Queue<Packet> packetQueue;
	private PacketHandler packetHandler;

	public NetIOManager(GameClient client, Socket clientSoc) throws IOException {
		this.clientSoc = clientSoc;
		out = new ObjectOutputStream(clientSoc.getOutputStream());
		out.flush();
		in = new ObjectInputStream(clientSoc.getInputStream());
		
		packetQueue = new ConcurrentLinkedQueue<Packet>();
		packetHandler = new PacketHandler(client);
	}
	
	public void establishServerCommunication(int userID, String sessionID) {
		//TODO: Move stream declarations down here (on server program too).
		sendPacket(new PacketUserSession(userID, sessionID));
		//TODO: Wait for reply first (after server-side implementation) or handle exceptions elegantly like on server program.
		new Thread(new Runnable() {
			public void run() {
				try {
					while (true) acceptPacket((Packet)in.readObject());
				} catch (SocketException e) {
					System.out.println("Server DCd.");
					System.exit(0);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				} finally {
					dcClient();
					System.exit(0);
					//TODO: WTF are you doing?
				}
			}
		}, "Incoming Message Handling Thread").start();
	}

	private synchronized boolean acceptPacket(Packet packet) throws Exception {
		if (packet == null)
			return true;
		if (packet instanceof PacketDisconnect)
			throw new Exception(((PacketDisconnect) packet).reason);

		packetQueue.add(packet);
		return true;
	}
	
	public void sendPacket(Packet packet) {
		try {
			out.writeObject(packet);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void pingServer() {
		lastPing = (System.nanoTime() / 1000000);
		sendPacket(new PacketQuery(PacketQuery.PING));
	}
	
	public void handlePackets() {
		for (int i = 0; i < packetQueue.size(); i++)
			packetHandler.handlePacket(packetQueue.poll());
	}
	
	private void dcClient() {
		try {
			in.close();
			out.close();
			clientSoc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

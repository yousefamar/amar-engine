package com.metaplains.net.packets;

public class PacketChat extends Packet {
	private static final long serialVersionUID = -2257417899960189646L;

	public int userID;
	public String message;
	
	public PacketChat(int userID, String message) {
		this.userID = userID;
		this.message = message;
	}
	
	public PacketChat(String message) {
		this(0, message);
	}
}
package com.metaplains.net.packets;

public class PacketUserSession extends Packet {
	private static final long serialVersionUID = 5152574898190109848L;

	public int userID;
	public String sessionID;
	
	public PacketUserSession(int userID, String sessionID) {
		this.userID = userID;
		this.sessionID = sessionID;
	}
}
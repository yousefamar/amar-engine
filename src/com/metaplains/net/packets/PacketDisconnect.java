package com.metaplains.net.packets;

public class PacketDisconnect extends Packet {
	private static final long serialVersionUID = 1312360778960948643L;

	public String reason;
	
	public PacketDisconnect(String reason) {
		this.reason = reason;
	}
}
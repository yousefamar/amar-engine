package com.metaplains.net.packets;

public class PacketEntityPosition extends Packet {
	private static final long serialVersionUID = 1097525599468554651L;
	
	public int id, x, y;

	public PacketEntityPosition(int id, int x, int y) {
		this.id = id;
		this.x = x;
		this.y = y;
	}
}
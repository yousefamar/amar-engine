package com.metaplains.net.packets;

public class PacketEntitySpawn extends Packet {
	private static final long serialVersionUID = 2015570121550738926L;

	public String type;
	public int id, x, y;

	public PacketEntitySpawn(int id, String type, int x, int y) {
		this.id = id;
		this.type = type;
		this.x = x;
		this.y = y;
	}
}
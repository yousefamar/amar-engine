package com.metaplains.net.packets;

public class PacketPlayerWaypoint extends Packet {
	private static final long serialVersionUID = -6560358185804883108L;

	public int id, x, y;
	
	public PacketPlayerWaypoint(int id, int wpX, int wpY) {
		this.id = id;
		this.x = wpX;
		this.y = wpY;
	}
}
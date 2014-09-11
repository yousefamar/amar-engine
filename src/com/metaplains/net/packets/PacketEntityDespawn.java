package com.metaplains.net.packets;

public class PacketEntityDespawn extends Packet {
	private static final long serialVersionUID = 8162829812620008094L;

	public int id;

	public PacketEntityDespawn(int id) {
		this.id = id;
	}
}
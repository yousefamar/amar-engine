package com.metaplains.net.packets;

public class PacketQuery extends Packet {
	private static final long serialVersionUID = 3493577474928884756L;

	//TODO: Reorder.
	public static int NULL = 0;
	public static int PING = 1;
	public static int PONG = 2;
	public static int INIT = 3;
	
	public int type;

	public PacketQuery(int type) {
		this.type = type;
	}
}
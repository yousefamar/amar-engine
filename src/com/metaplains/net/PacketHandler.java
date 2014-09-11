package com.metaplains.net;

import com.metaplains.core.GameClient;
import com.metaplains.entities.*;
import com.metaplains.entities.active.Player;
import com.metaplains.gfx.gui.GUIMessageFS;
import com.metaplains.gfx.gui.GUIWorld;
import com.metaplains.net.packets.*;
import com.metaplains.world.scenes.Scene;
import com.metaplains.world.terrain.Terrain;


public class PacketHandler {

	private GameClient client;

	public PacketHandler(GameClient client) {
		this.client = client;
	}

	public void handlePacket(Packet packet) {
		if (packet == null)
			return;
		if(packet instanceof PacketQuery)
			handlePacket((PacketQuery) packet);
		else if(packet instanceof PacketEntityPosition)
			handlePacket((PacketEntityPosition) packet);
		else if(packet instanceof PacketChat)
			handlePacket((PacketChat) packet);
		else if(packet instanceof PacketPlayerWaypoint)
			handlePacket((PacketPlayerWaypoint) packet);
		else if(packet instanceof PacketEntitySpawn)
			handlePacket((PacketEntitySpawn) packet);
		else if(packet instanceof PacketEntityDespawn)
			handlePacket((PacketEntityDespawn) packet);
	}
	
	private void handlePacket(PacketQuery packet) {
		switch (packet.type) {
		case 0:
			break;
		case 1:
			client.netIOManager.sendPacket(new PacketQuery(PacketQuery.PONG));
			break;
		case 2:
			client.netIOManager.ping = (System.nanoTime() / 1000000) - client.netIOManager.lastPing;
			client.netIOManager.pingServer();
			break;
		case 3:
			((GUIMessageFS) client.currentGUIScreen).setMessage("Joining World...");
			//TODO: Loading won't show up like this you derphead.
			//client.currentScene = new ZoneMultiplayer("testMap");
			//client.currentGUIScreen = new GUIWorld(client.currentScene);
			client.netIOManager.pingServer();
			break;
		default:
			break;
		}
	}

	private void handlePacket(PacketEntityPosition packet) {
		//TODO: Optimise.
		/*synchronized (client.currentScene.entities) {
			for (Entity entity : client.currentWorld.entities) {
				if (entity.id == packet.id) {
					entity.setPosition(packet.x, packet.y);
					return;
				}
			}
		}*/
	}
	
	private void handlePacket(PacketChat packet) {
		/*Player player = client.currentScene.playerMap.get(packet.userID); //TODO: Consistency!
		if (player != null)
			player.setMessage(packet.message);*/
	}
	
	private void handlePacket(PacketPlayerWaypoint packet) {
		/*Player player = client.currentScene.playerMap.get(packet.id);
		if (player != null)
			player.setPathTo(packet.x, packet.y);*/
	}
	
	private void handlePacket(PacketEntitySpawn packet) {
		try {
			//TODO: Remove id from method.
			//client.currentScene.joinEntity(packet.id, (Entity) (Class.forName(packet.type)).getDeclaredConstructor(Scene.class, int.class, int.class).newInstance(client.currentScene, packet.x, packet.y));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void handlePacket(PacketEntityDespawn packet) {
		//client.currentScene.leaveEntity(packet.id);
	}
}
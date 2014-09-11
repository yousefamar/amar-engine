package com.metaplains.core;

import java.awt.BorderLayout;
import java.applet.Applet;

@SuppressWarnings("serial")
public class GameApplet extends Applet {
	
	public void init() {
		GameClient.game = new GameClient(Integer.parseInt(getParameter("userID")), getParameter("sessionID"));
		add(GameClient.game.screen, BorderLayout.CENTER);
	}
	
	public void destroy() {
		remove(GameClient.game.screen);
		super.destroy();
	}
}
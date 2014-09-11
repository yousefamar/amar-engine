package com.metaplains.gfx.gui;

import java.awt.Color;

import com.metaplains.core.GameScreen;
import com.metaplains.gfx.TrueTypeFont;

public class GUIMessageFS extends GUIElement {

	private String message;

	public GUIMessageFS(String message) {
		super();
		setMessage(message);
	}
	
	public GUIMessageFS() {
		this("");
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	@Override
	public void render(GameScreen screen) {
		screen.setColor(Color.BLACK);
		screen.fillRect(x, y, width, height);
		screen.setColor(Color.LIGHT_GRAY);
		screen.drawString(message, width/2, (height-screen.getTTFont().getHeight())/2, 1, 1, TrueTypeFont.ALIGN_CENTER);
	}
}
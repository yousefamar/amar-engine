package com.metaplains.gfx.gui;

import java.awt.*;

import com.metaplains.core.GameScreen;
import com.metaplains.gfx.TrueTypeFont;

public class GUIButton extends GUIElement {

	protected int id;
	private String text;

	public GUIButton(int x, int y, int width, int height, GUIElement parent, int id, String text) {
		super(x, y, width, height, parent);
		this.id = id;
		this.text = text;
	}
	
	@Override
	public boolean mousePressed(int button, int x, int y) {
		parent.elementClicked(this);
		return true;
	}

	@Override
	public void render(GameScreen screen) {
		screen.setColor(mouseOver?Color.GRAY:Color.DARK_GRAY);
		screen.fillRect(x, y, width, height);
		screen.setColor(Color.LIGHT_GRAY);
		screen.drawRect(x, y, width, height);
		screen.drawString(text, x+width/2, y+(height-screen.getTTFont().getHeight())/2-1, 1, 1, TrueTypeFont.ALIGN_CENTER);
	}
}
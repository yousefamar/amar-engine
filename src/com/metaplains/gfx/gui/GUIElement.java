package com.metaplains.gfx.gui;

import java.util.ArrayList;

import com.metaplains.core.GameClient;
import com.metaplains.core.GameScreen;

public abstract class GUIElement {
	
	protected int x, y;
	protected int width, height;
	public boolean mouseOver; //TODO: Change visibility.

	protected GUIElement parent;
	protected ArrayList<GUIElement> subElements = new ArrayList<GUIElement>();
	
	public GUIElement() {
		//TODO: Think this through and check. Consider a method of identifying guiScreens.
		this(0, 0, GameClient.game.screen.screenDims.width, GameClient.game.screen.screenDims.height, null);
	}

	public GUIElement(int x, int y, int width, int height, GUIElement parent) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.parent = parent;
	}

	public void mouseMoved(int x, int y) {
		mouseOver = isPosInBounds(x, y);
		for (GUIElement subElement : subElements)
			subElement.mouseMoved(x, y);
	}
	
	public boolean mousePressed(int button, int x, int y) {
		for (int id = subElements.size()-1; id >= 0 ; id--) {
			GUIElement subElement = subElements.get(id);
			if(subElement.mouseOver)
				return subElement.mousePressed(button, x, y);
		}
		return false;
	}
	
	public boolean mouseReleased(int button, int x, int y) {
		for (int id = subElements.size()-1; id >= 0 ; id--) {
			GUIElement subElement = subElements.get(id);
			if(subElement.mouseOver)
				return subElement.mouseReleased(button, x, y);
		}
		return false;
	}

	public boolean keyPressed(int keyCode, char keyChar) {
		for (int id = subElements.size()-1; id >= 0 ; id--) {
			GUIElement subElement = subElements.get(id);
			if (subElement.keyPressed(keyCode, keyChar))
				return true;
		}
		return false;
	}

	public boolean keyReleased(int keyCode, char keyChar) {
		for (int id = subElements.size()-1; id >= 0 ; id--) {
			GUIElement subElement = subElements.get(id);
			if (subElement.keyReleased(keyCode, keyChar))
				return true;
		}
		return false;
	}
	
	protected void elementClicked(GUIElement element) {} //TODO: Make parent pass handling methods for other events too.
	
	public void tick() {
		for (int id = subElements.size()-1; id >= 0 ; id--)
			subElements.get(id).tick();
	}
	
	protected boolean isPosInBounds(int x, int y) {
		return (x >= this.x && x < (this.x + width) && y >= this.y && y < (this.y + height));
	}
	
	public void render(GameScreen screen) {
		for (GUIElement element : subElements)
			element.render(screen);
	}
}
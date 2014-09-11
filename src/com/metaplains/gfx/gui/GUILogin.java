package com.metaplains.gfx.gui;

import org.lwjgl.input.Keyboard;

import com.metaplains.core.GameClient;


public class GUILogin extends GUIElement {

	private GameClient game;
	private int focusedFieldID;
	
	public GUILogin(GameClient game) {
		super();
		this.game = game;
		int screenWidth = GameClient.game.screen.screenDims.width;
		subElements.add(new GUITextField((screenWidth-250)/2, 260, 250, this, "Username"));
		subElements.add(new GUITextField((screenWidth-250)/2, 310, 250, this, "Password").setTextHidden());
		subElements.add(new GUIButton((screenWidth-150)/2, 360, 150, 27, this, 2, "Login"));
	}
	
	@Override
	public boolean mousePressed(int button, int x, int y) {
		if (!super.mousePressed(button, x, y)) {
			Keyboard.enableRepeatEvents(false);
			((GUITextField) subElements.get(0)).setFocus(false);
			((GUITextField) subElements.get(1)).setFocus(false);
			return false;
		}
		return true;
	}
	
	@Override
	public boolean keyPressed(int keyCode, char keyChar) {
		if (super.keyPressed(keyCode, keyChar)) {
			if (keyCode == Keyboard.KEY_TAB) {
				((GUITextField) subElements.get(focusedFieldID)).setFocus(false);
				//TODO: Change to 3 if all elements can become focusable.
				((GUITextField) subElements.get(focusedFieldID = (focusedFieldID+1)%2)).setFocus(true);
			} else if (keyCode == Keyboard.KEY_RETURN) {
				elementClicked(subElements.get(2));
			}
			return true;
		}
		return false;
	}
	
	@Override
	protected void elementClicked(GUIElement element) {
		if (element instanceof GUITextField) {
			for (GUIElement subElement : subElements)
				if (subElement instanceof GUITextField && subElement != element)
					((GUITextField) subElement).setFocus(false);
			((GUITextField) element).setFocus(true);
			focusedFieldID = subElements.indexOf(element);
			Keyboard.enableRepeatEvents(true);
		} else {
			Keyboard.enableRepeatEvents(false);
			game.login(((GUITextField) subElements.get(0)).getText(), ((GUITextField) subElements.get(1)).getText());
		}
	}
}
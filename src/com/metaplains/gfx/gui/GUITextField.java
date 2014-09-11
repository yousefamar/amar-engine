package com.metaplains.gfx.gui;

import java.awt.*;
import org.lwjgl.input.Keyboard;

import com.metaplains.core.GameClient;
import com.metaplains.core.GameScreen;
import com.metaplains.gfx.TrueTypeFont;

public class GUITextField extends GUIElement {

	private String placeHolder;
	private StringBuilder textSB;
	private String text;
	private int caretCounter;
	private boolean isFocused;
	private boolean isTextHidden;

	public GUITextField(int x, int y, int width, GUIElement parent, String placeHolder) {
		super(x, y, width, 27, parent);
		this.placeHolder = placeHolder;
		this.textSB = new StringBuilder();
		this.text = "";
		caretCounter = 10;
	}
	
	public GUITextField setTextHidden() {
		isTextHidden = true;
		return this;
	}
	
	//TODO: Consider implementing for all GUIElements.
	public void setFocus(boolean flag) {
		caretCounter = flag?0:10;
		isFocused = flag;
	}
	
	public String getText() {
		return text;
	}
	
	@Override
	public boolean mousePressed(int button, int x, int y) {
		parent.elementClicked(this);
		return true;
	}
	
	@Override
	public boolean keyPressed(int keyCode, char keyChar) {
		if (!isFocused)
			return false;
		if (keyCode == Keyboard.KEY_BACK) {
			if (textSB.length() > 0) {
				textSB.deleteCharAt(textSB.length()-1);
				text = textSB.toString();
			}
			caretCounter = 0;
		} else if (GameClient.game.screen.getFont().canDisplay(keyChar) && keyCode != Keyboard.KEY_TAB && keyCode != Keyboard.KEY_RETURN && GameClient.game.screen.getTTFont().getWidth(isTextHidden?text.replaceAll("(?s).", "*")+"*|":text+Character.toString(keyChar)+"|") < width-4) {
			textSB.append(keyChar);
			text = textSB.toString();
			caretCounter = 0;
		}
		return true;
	}
	
	@Override
	public void tick() {
		if (isFocused && caretCounter++ >= 20)
			caretCounter = 0;
	}

	@Override
	public void render(GameScreen screen) {
		screen.setColor(Color.WHITE);
		screen.fillRect(x, y, width, height);
		screen.setColor(Color.DARK_GRAY);
		screen.drawRect(x, y, width, height);
		if (text.length() > 0 || isFocused) {
			screen.setColor(isFocused?Color.DARK_GRAY:Color.GRAY);
			screen.drawString((isTextHidden?text.replaceAll("(?s).", "*"):text)+(caretCounter<10?"|":""), x, y, 1, 1, TrueTypeFont.ALIGN_LEFT);
		} else {
			screen.setColor(Color.LIGHT_GRAY);
			screen.drawString(placeHolder, x, y, 1, 1, TrueTypeFont.ALIGN_LEFT);
		}
	}
}
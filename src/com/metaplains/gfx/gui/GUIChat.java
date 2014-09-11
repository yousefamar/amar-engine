package com.metaplains.gfx.gui;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.input.Keyboard;

import com.metaplains.core.GameClient;
import com.metaplains.core.GameScreen;
import com.metaplains.gfx.TrueTypeFont;
import com.metaplains.world.scenes.Scene;


public class GUIChat extends GUIElement {

	//TODO: Consider implementing this for all GUIElements (it would be useful).
	public boolean isVisible;
	public String message;
	private StringBuilder messageSB;
	private int caretCounter;

	public GUIChat(int x, int y) {
		super(x, y, 500/*GameClient.game.screen.getScreenDims().width-97*/, 27, null);
		this.isVisible = false;
		this.messageSB = new StringBuilder();
		this.message = "";
	}
	
	public void setVisible() {
		isVisible = true;
		Keyboard.enableRepeatEvents(true);
	}
	
	@Override
	public boolean keyPressed(int keyCode, char keyChar) {
		if (!isVisible)
			return false;
		if (keyCode == Keyboard.KEY_BACK) {
			if (messageSB.length() > 0) {
				messageSB.deleteCharAt(messageSB.length()-1);
				message = messageSB.toString();
			}
			caretCounter = 0;
		} else if (keyCode == Keyboard.KEY_RETURN) {
			Keyboard.enableRepeatEvents(false);
			isVisible = false;
			//TODO: Make the actual moving object lock key movement.
			//world.camera.locked = false;
			if (message.length() > 0) {
				GameClient.game.currentScene.setPlayerMessage(new String(message));
				messageSB.setLength(0);
				message = "";
			}
		} else if (GameClient.game.screen.getFont().canDisplay(keyChar) && keyCode != Keyboard.KEY_TAB) {
			messageSB.append(keyChar);
			message = messageSB.toString();
			caretCounter = 0;
		}
		return true;
	}
	
	@Override
	public void tick() {
		if (isVisible && caretCounter++ >= 20)
			caretCounter = 0;
	}
	
	@Override
	public void render(GameScreen screen) {
		if (!isVisible)
			return;
		
		glEnable(GL_BLEND);
		glColor4f(0, 0, 0, 0.5F);
		screen.fillRect(x, y, width, height);
		glDisable(GL_BLEND);
		glColor4f(1, 1, 1, 1);
		screen.drawString(message+(caretCounter<10?"|":""), x, y, 1, 1, TrueTypeFont.ALIGN_LEFT);
	}
}
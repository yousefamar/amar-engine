package com.metaplains.core;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class InputManager {

	private int mouseX, mouseY;
	private GameClient game;
	
	public InputManager(GameClient game) {
		this.game = game;
	}

	public void handleInput() {
		while (Mouse.next()){
			int button = Mouse.getEventButton();
			int x = Mouse.getX(), y = game.screen.screenDims.height-1 - Mouse.getY();
			if (x != mouseX || y != mouseY) {
				mouseX = x;
				mouseY = y;
				game.currentGUIScreen.mouseMoved(x, y);
				if (game.currentScene != null)
					game.currentScene.mouseMoved(x, y);
			} else if (Mouse.getEventButtonState()) {
				if (!game.currentGUIScreen.mousePressed(button, x, y)) {
					if (game.currentScene != null)
						game.currentScene.mousePressed(button, x, y);
				}
			} else {
				if (!game.currentGUIScreen.mouseReleased(button, x, y)) {
					if (game.currentScene != null)
						game.currentScene.mouseReleased(button, x, y);
				}
			}
		}

		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				if (!game.currentGUIScreen.keyPressed(Keyboard.getEventKey(), Keyboard.getEventCharacter())) {
					if (game.currentScene != null)
						game.currentScene.keyPressed(Keyboard.getEventKey(), Keyboard.getEventCharacter());
				}
			} else {
				if (!game.currentGUIScreen.keyReleased(Keyboard.getEventKey(), Keyboard.getEventCharacter())) {
					if (game.currentScene != null)
						game.currentScene.keyReleased(Keyboard.getEventKey(), Keyboard.getEventCharacter());
				}
			}
		}
	}
}
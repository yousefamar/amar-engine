package com.metaplains.gfx.gui;

import com.metaplains.core.GameClient;

public class GUIMainMenu extends GUIElement {

	private GameClient game;

	public GUIMainMenu(GameClient game) {
		super();
		this.game = game;
		int centreWidthBegin = (GameClient.game.screen.screenDims.width - 150)/2;
		subElements.add(new GUIButton(centreWidthBegin, 220, 150, 27, this, 0, "Single Player"));
		subElements.add(new GUIButton(centreWidthBegin, 270, 150, 27, this, 1, "Join"));
		subElements.add(new GUIButton(centreWidthBegin, 320, 150, 27, this, 2, "Host"));
		subElements.add(new GUIButton(centreWidthBegin, 370, 150, 27, this, 3, "Options"));
		subElements.add(new GUIButton(centreWidthBegin, 420, 150, 27, this, 4, "Bla"));
	}
	
	@Override
	protected void elementClicked(GUIElement element) {
		switch (((GUIButton) element).id) {
		case 0:
			//TODO: Add single player support.
			game.startSinglePlayerGame();
			break;
		case 1:
			//TODO: Allow joining custom servers and think about the implications.
			game.joinMultiplayerGame("50.87.101.121", 9980);
			break;
		case 2:
			//game.netIOManager.hostGame(2);
			break;
		case 3:

			break;
		case 4:

			break;
		default:
			break;
		}
	}
}
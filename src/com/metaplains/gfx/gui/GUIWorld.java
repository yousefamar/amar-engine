package com.metaplains.gfx.gui;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import java.awt.*;
import com.metaplains.core.GameClient;
import com.metaplains.core.GameScreen;
import com.metaplains.gfx.GL;
import com.metaplains.gfx.Shader;
import com.metaplains.gfx.Texture;
import com.metaplains.gfx.TrueTypeFont;
import com.metaplains.world.scenes.Scene;


public class GUIWorld extends GUIElement {

	private int fps;
	private int tps;
	private int renderTickCounter;
	private int tickCounter;
	private long lastFPS = (System.nanoTime() / 1000000);
	private long lastTPS = (System.nanoTime() / 1000000);
	private float usedRAM;

	public GUIWorld() {
		super();
		Dimension screenDims = GameClient.game.screen.screenDims;
		//this.subElements.add(new GUIActionBar(99, screenDims.height - 78, world.user));
		//this.subElements.add(new GUIMinimap(0, screenDims.height - 98, world));
		this.subElements.add(new GUIChat(5, screenDims.height - 32));
	}
	
	public void setChatVisible() {
		((GUIChat) this.subElements.get(0)).setVisible();
	}

	public void tick(){
		super.tick();
		
		if ((System.nanoTime() / 1000000) - lastTPS > 1000) {
			tps = tickCounter;
			tickCounter = 0;
			lastTPS += 1000;
		}
		
		if (tickCounter == 0)
			usedRAM = ((float)(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()))/(1024F * 1024F);
		
		tickCounter++;
	}
	
	@Override
	public void render(GameScreen screen) {
		if ((System.nanoTime() / 1000000) - lastFPS > 1000) {
			fps = renderTickCounter;
			renderTickCounter = 0;
			lastFPS += 1000;
		}

		super.render(screen);
//		if (world.user.heldStructure != null) {
//			Composite tempComp = gfx.getComposite();
//			AlphaComposite alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6F);
//		    gfx.setComposite(alphaComp);
//			world.user.heldStructure.render(gfx);
//			gfx.setComposite(tempComp);
//		}

		//renderTestImage(5, 90);
		
		screen.setColor(Color.WHITE);
		screen.drawString("FPS: "+fps, 0, 0, 1, 1, TrueTypeFont.ALIGN_LEFT);
		screen.drawString("TPS: "+tps, 0, 20, 1, 1, TrueTypeFont.ALIGN_LEFT);
		screen.drawString("RAM: "+usedRAM+" MB", 0, 40, 1, 1, TrueTypeFont.ALIGN_LEFT);
		//if (!GameClient.game.currentScene.isSinglePlayer())
		//	screen.drawString("Ping: "+GameClient.game.netIOManager.ping+" ms", 0, 60, 1, 1, TrueTypeFont.ALIGN_LEFT);

		renderTickCounter++;
	}
	
	private void renderTestImage(int x, int y) {
		//TODO: Use screen method (and consider putting glEnable(GL_TEXTURE_2D); in it).
		glEnable(GL_TEXTURE_2D);
		glColor3f(1, 1, 1);
		glBindTexture(GL_TEXTURE_2D, Texture.NOSIGNAL);
		GL.useProgram(Shader.STD);
		glBegin(GL_QUADS);
		glTexCoord2f(0, 1);
		glVertex3f(x, y+100, 0);
		glTexCoord2f(1, 1);
		glVertex3f(x+100, y+100, 0);
		glTexCoord2f(1, 0);
		glVertex3f(x+100, y, 0);
		glTexCoord2f(0, 0);
		glVertex3f(x, y, 0);
		glEnd();
		glDisable(GL_TEXTURE_2D);
	}
}
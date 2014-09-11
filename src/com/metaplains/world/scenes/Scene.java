package com.metaplains.world.scenes;

import com.metaplains.core.*;
import com.metaplains.entities.active.*;
import com.metaplains.gfx.cam.Camera;
import com.metaplains.world.EntityManager;

public abstract class Scene {

	//TODO: Encapsulate.
	public Camera camera;
	public Player player;
	public EntityManager entityManager;
	
	public void init() {
		entityManager = new EntityManager(this);
	}

	public abstract boolean isSinglePlayer();
	
	protected void setPlayerPathTo(int x, int y) {}
	
	public void setPlayerMessage(String message) {}

	public void mouseMoved(int x, int y) {
	}
	
	public void mousePressed(int button, int x, int y) {
	}

	public void mouseReleased(int button, int x, int y) {
	}
	
	public void keyPressed(int keyCode, char keyChar) {
	}
	
	public void keyReleased(int keyCode, char keyChar) {
	}
	
	public void tick() {
		entityManager.tick();
		camera.tick();
	}
	
	public void render(GameScreen screen) {
		if (camera==null)
			return;
		camera.onRender(true);

		
		//screen.renderFog(1.0F, 1.0F, 1.0F, 1.0F);
		//water.renderReflection(screen);
	    //water.renderRefractionAndDepth(screen);
		//screen.renderAxes();
		//camera.frustum.render();
		
		//glUseProgram(Shader.TEST);
		//terrain.render(screen);
		
		//entityManager.updateLighting(screen);
		entityManager.render(screen, false);
		//glUseProgram(Shader.NONE);
	}

	public void destroy() {
		entityManager.destroy();
	}
}
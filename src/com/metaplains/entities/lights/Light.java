package com.metaplains.entities.lights;

import com.metaplains.core.GameScreen;
import com.metaplains.entities.Entity;
import com.metaplains.world.scenes.Scene;

public abstract class Light extends Entity {
	
	public Light(Scene scene) {
		super(scene);
	}
	
	public abstract void updateDepthTextures(GameScreen screen);

	@Override
	public void destroy() {
		scene.entityManager.destroyLight(this);
		super.destroy();
	}
}
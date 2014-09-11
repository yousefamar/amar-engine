package com.metaplains.entities.meta;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.metaplains.core.GameClient;
import com.metaplains.core.GameScreen;
import com.metaplains.entities.EntityTangible;
import com.metaplains.entities.active.Player;
import com.metaplains.gfx.cam.ClippingSphere;
import com.metaplains.utils.Vec3F;
import com.metaplains.world.scenes.Scene;

public class PortalScene extends EntityTangible {

	private Scene destScene;
	private CollisionShape colShape;

	public PortalScene(Scene scene, float x, float y, float z, Scene destScene, CollisionShape colShape) {
		super(scene, x, y, z);
		this.destScene = destScene;
		this.colShape = colShape;
	}

	@Override
	public void onCollision(EntityTangible entity) {
		if (entity instanceof Player)
			GameClient.game.joinScene(destScene);
	}
	
	@Override
	public ClippingSphere getClippingSphere() {
		return null;
	}
	
	@Override
	public void render(GameScreen screen, boolean shadowPass) {
	}

	@Override
	public float getMass() {
		return 0.0F;
	}

	@Override
	public CollisionShape getCollisionShape() {
		return this.colShape;
	}

	@Override
	public Vec3F getInertiaTensor() {
		return new Vec3F(0.0F, 0.0F, 0.0F);
	}

	@Override
	public boolean isKinematic() {
		return false;
	}
}
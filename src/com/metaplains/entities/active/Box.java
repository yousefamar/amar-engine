package com.metaplains.entities.active;

import static org.lwjgl.opengl.GL11.*;

import javax.vecmath.Vector3f;
import com.bulletphysics.collision.shapes.*;
import com.metaplains.core.GameScreen;
import com.metaplains.entities.EntityTangible;
import com.metaplains.gfx.Model;
import com.metaplains.gfx.Texture;
import com.metaplains.gfx.cam.ClippingSphere;
import com.metaplains.utils.Vec3F;
import com.metaplains.world.scenes.Scene;

public class Box extends EntityTangible {

	public Box(Scene scene, float x, float y, float z) {
		super(scene, x, y, z);
	}
	
	@Override
	public ClippingSphere getClippingSphere() {
		return null;
	}
	
	@Override
	public void render(GameScreen screen, boolean shadowPass) {
		glBindTexture(GL_TEXTURE_2D, Texture.NOSIGNAL);
		screen.renderModel(Model.CUBE);
	}

	@Override
	public float getMass() {
		return 1.0F;
	}

	@Override
	public CollisionShape getCollisionShape() {
		return new BoxShape(new Vector3f(0.5F, 0.5F, 0.5F));
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
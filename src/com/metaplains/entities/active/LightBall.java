package com.metaplains.entities.active;

import static org.lwjgl.opengl.GL11.*;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;
import com.metaplains.core.GameScreen;
import com.metaplains.entities.EntityTangible;
import com.metaplains.entities.lights.LightPoint;
import com.metaplains.gfx.GL;
import com.metaplains.gfx.Model;
import com.metaplains.gfx.Shader;
import com.metaplains.gfx.Texture;
import com.metaplains.gfx.cam.ClippingSphere;
import com.metaplains.utils.Vec3F;
import com.metaplains.world.scenes.Scene;

public class LightBall extends EntityTangible {

	public LightBall(Scene scene, float x, float y, float z) {
		super(scene, x, y, z);
	}
	
	@Override
	public void init() {
		this.addChild(new LightPoint(scene));
		((RigidBody) collisionObject).setGravity(new Vector3f(0,0,0));
		((RigidBody) collisionObject).setDamping(0.5F, 1);
	}
	
	@Override
	public ClippingSphere getClippingSphere() {
		return null;
	}
	
	@Override
	public void render(GameScreen screen, boolean shadowPass) {
		if (shadowPass)
			return;
		glDisable(GL_LIGHTING);
		GL.useProgram(Shader.STD);
		glBindTexture(GL_TEXTURE_2D, Texture.NONE);
		glColor3f(1.0F, 1.0F, 1.0F);
		screen.renderModel(Model.SPHERE);
		glEnable(GL_LIGHTING);
	}

	@Override
	public float getMass() {
		return 1.0F;
	}

	@Override
	public CollisionShape getCollisionShape() {
		return new SphereShape(0.2F);
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
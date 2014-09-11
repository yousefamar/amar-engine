package com.metaplains.entities.inert.decor;

import javax.vecmath.Vector3f;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.CylinderShape;
import com.metaplains.core.GameScreen;
import com.metaplains.entities.EntityTangible;
import com.metaplains.gfx.Model;
import com.metaplains.gfx.cam.ClippingSphere;
import com.metaplains.utils.Vec3F;
import com.metaplains.world.scenes.Scene;

public class Tree extends EntityTangible {

	public Tree(Scene scene, float x, float y, float z) {
		super(scene, x, y, z);
	}

	@Override
	public ClippingSphere getClippingSphere() {
		return null;
	}
	
	@Override
	public void render(GameScreen screen, boolean shadowPass) {
		//TODO: Standardize.
		//TODO: Use VBOs exclusively?
		screen.renderModel(Model.TREE);
	}

	@Override
	public float getMass() {
		return 0.0F;
	}

	@Override
	public CollisionShape getCollisionShape() {
		return new CylinderShape(new Vector3f(0.5F, 3, 0.5F)); //TODO: Offset center and check if sizes are correct.
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
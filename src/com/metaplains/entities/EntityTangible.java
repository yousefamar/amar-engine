package com.metaplains.entities;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;
import com.metaplains.utils.Vec3F;
import com.metaplains.world.scenes.Scene;

public abstract class EntityTangible extends Entity implements Tickable {

	public CollisionObject collisionObject; //Potentially null. TODO: Consider changing type to CollisionObject.
	
	public EntityTangible(Scene scene, float x, float y, float z) {
		super(scene, x, y, z);
		if (!isKinematic() && getMass() > 0)
			scene.entityManager.scheduleEntityTick(this);
	}
	
	public void onCollision(EntityTangible entity) {
	}
	
	@Override
	public void tick() {
		if (collisionObject instanceof RigidBody) {
			Transform trans = ((RigidBody) collisionObject).getMotionState().getWorldTransform(new Transform());
			transform.setTransform(trans);
			scene.entityManager.scheduleEntityTick(this);
		}
	}
	
	public abstract float getMass();
	
	public abstract CollisionShape getCollisionShape();
	
	public abstract Vec3F getInertiaTensor();
	
	public abstract boolean isKinematic();
}
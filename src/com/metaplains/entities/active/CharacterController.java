package com.metaplains.entities.active;

import javax.vecmath.Vector3f;

import org.lwjgl.input.Keyboard;

import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.dispatch.PairCachingGhostObject;
import com.bulletphysics.collision.shapes.ConvexShape;
import com.bulletphysics.dynamics.character.KinematicCharacterController;
import com.bulletphysics.linearmath.Transform;
import com.metaplains.entities.Entity;
import com.metaplains.utils.FastMath;
import com.metaplains.utils.Vec3F;

public class CharacterController {

	private Player player;
	public PairCachingGhostObject ghostObject;
	public KinematicCharacterController controller;
	
	public CharacterController(Player player, ConvexShape convexShape, float stepHeight) {
		this.player = player;
		ghostObject = new PairCachingGhostObject();
		ghostObject.setCollisionShape(convexShape);
		ghostObject.setCollisionFlags(CollisionFlags.CHARACTER_OBJECT);
		//ghostObject.setWorldTransform(new Transform().origin.set(arg0, arg1, arg2))
		//ghostObject.setUserPointer(this);
		controller = new KinematicCharacterController(ghostObject, convexShape, stepHeight);
		//controller.setJumpSpeed(50);
		//controller.setFallSpeed(30);
		//controller.setGravity(30);
		//controller.se.setUseViewDirection(true);
	}
	
	public void tick() {
		//TODO: WTF are you doing...
		float forward = 0, strafe = 0, hover = 0;
		if(Keyboard.isKeyDown(Keyboard.KEY_W))
			forward--;
		if(Keyboard.isKeyDown(Keyboard.KEY_S))
			forward++;
		//if(Keyboard.isKeyDown(Keyboard.KEY_SPACE))
		//	hover++;
		//if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
		//	hover--;
		if(Keyboard.isKeyDown(Keyboard.KEY_D))
			strafe++;
		if(Keyboard.isKeyDown(Keyboard.KEY_A))
			strafe--;
		if(forward!=0||strafe!=0||hover!=0) {
			//camera.translatePosition(new Vec3F(strafe*10, 0, forward*10));

			//TODO: Come up with a more stable algorithm for this.
			//TODO: Think of a way of combining vectors.
			//Vec3D surge = new Vec3D(0, 0, -fwd).rotateX(pitch).rotateY(yaw).translate(position.x, position.y, position.z).translate(new Vec3D(strafe, 0, 0).rotateY(yaw)).normalise().scale(0.1F);
			//translatePosition(surge);
			float yaw = player.yaw, speedMultiplier = (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)?0.1F:0.01F);
			float cy = FastMath.cos(yaw);
			float sy = FastMath.sin(yaw);
			Vec3F surge = new Vec3F(-sy*forward + cy*strafe, hover, cy*forward + sy*strafe).normalise().scale(speedMultiplier);//The scale depends on general friction.
//			Vector3f camDir = cam.getDirection().clone();//.multLocal(0.6f);
//			Vector3f camLeft = cam.getLeft().clone();//.multLocal(0.4f);
//			Vector3f walkDirection = new Vector3f();
//			walkDirection.addLocal(camDir.multLocal(fwd));
//			walkDirection.addLocal(camLeft.multLocal(strafe));
			controller.setWalkDirection(surge.toVector3fJavax());

			//TODO: Take roll into account.
		} else {
			controller.setWalkDirection(new Vector3f());
		}
	}
	
	public Transform getWorldTransform(Transform out) {
		return ghostObject.getWorldTransform(out);
	}
}
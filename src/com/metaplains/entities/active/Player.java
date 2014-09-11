package com.metaplains.entities.active;

import static com.metaplains.gfx.GL.*;
import static org.lwjgl.opengl.GL11.*;

import javax.vecmath.Vector3f;

import org.lwjgl.input.Mouse;

import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.shapes.CapsuleShape;
import com.bulletphysics.collision.shapes.ConvexShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.metaplains.core.GameScreen;
import com.metaplains.entities.EntityTangible;
import com.metaplains.entities.inert.buildings.House;
import com.metaplains.gfx.GL;
import com.metaplains.gfx.Model;
import com.metaplains.gfx.Shader;
import com.metaplains.gfx.Texture;
import com.metaplains.gfx.cam.CameraFP;
import com.metaplains.gfx.cam.ClippingSphere;
import com.metaplains.gfx.cam.ICameraTarget;
import com.metaplains.utils.Quat4F;
import com.metaplains.utils.Transform;
import com.metaplains.utils.Vec3F;
import com.metaplains.world.scenes.Scene;

public class Player extends EntityTangible implements ICameraTarget {

	//TODO: Think of a better system for managing widths and heights.
	public String lastMsg; 
	private int msgTimer;
	
	public CharacterController controller;
	private Transform[] childEntities;
	
	//TODO: Integrate into transform.
	public float yaw, pitch, roll;
	
	public Player(Scene scene, float x, float y, float z) {
		super(scene, x, y, z);
		controller = new CharacterController(this, getCollisionShape(), 0.5F);
		
		childEntities = new Transform[6];
		/*
		 *   0
		 * 2 1 3
		 *  4 5
		 *   ^ Facing away.
		 */
		childEntities[0] = new Transform(0, 0.8F, 0);
		childEntities[1] = new Transform(0, 0, 0);
		childEntities[2] = new Transform(1, 0, 0);
		childEntities[3] = new Transform(-1, 0, 0);
		childEntities[4] = new Transform(0.4F, -1, 0);
		childEntities[5] = new Transform(-0.4F, -1, 0);
		scene.entityManager.scheduleEntityTick(this);
	}
	
	@Override
	public void init() {
		//rigidBody.setCollisionFlags(rigidBody.getCollisionFlags() | CollisionFlags.CUSTOM_MATERIAL_CALLBACK);
	}
	
	public void mouseMoved(int x, int y) {
		if (/*Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) &&*/ Mouse.isButtonDown(1)) {
			//TODO: !!!
			int dx = Mouse.getDX(), dy = Mouse.getDY();
			pitch-=dy;
			yaw+=dx;
			if (pitch > 89.9F)
				pitch = 89.9F;
			else if (pitch < -89.9F)
				pitch = -89.9F;
			lookDir.set(new Vec3F(0, 0, -1).rotateX(pitch).rotateY(yaw));
			transform.setRotation(new Quat4F().look(new Vec3F(0, 0, -1).rotateY(-yaw)));
			//transMatrix.setTransform(controller.getWorldTransform(new com.bulletphysics.linearmath.Transform()));
			//transMatrix.matrix.rotateX(Mouse.getDX()).rotateY();
			//transMatrix.updateBuffer();
			//controller.ghostObject.setWorldTransform(new com.bulletphysics.linearmath.Transform(transMatrix.matrix.toMatrix4f()));
		}
	}
	
	public void jump() {
		controller.controller.jump();
	}
	
	@Override
	public void tick() {
		//this.rigidBody.getMotionState().setWorldTransform(controller.getWorldTransform(new Transform()));
		//Transform trans = rigidBody.getMotionState().getWorldTransform(new Transform());
		controller.tick();
		Vec3F newPos = new Vec3F(controller.getWorldTransform(new com.bulletphysics.linearmath.Transform()).origin);
		transform.setTranslation(newPos);
		//transMatrix.setRrotation
		newPos.y += 1.6F/2F;
		position.set(newPos);
		//super.tick();
//		Vector3f camDir = cam.getDirection().clone();//.multLocal(0.6f);
//		Vector3f camLeft = cam.getLeft().clone();//.multLocal(0.4f);
//		Vector3f walkDirection = new Vector3f();
//		walkDirection.addLocal(camDir.multLocal(fwd));
//		walkDirection.addLocal(camLeft.multLocal(strafe));
//		walkDirection.setY(0);
//		controller.setWalkDirection(walkDirection);
		if (msgTimer > 0)
			msgTimer--;
		scene.entityManager.scheduleEntityTick(this);
	}
	
	public void setMessage(String message) {
		this.lastMsg = message;
		msgTimer = 100;
	}
	
	@Override
	public ClippingSphere getClippingSphere() {
		return null;
	}

	@Override
	public void render(GameScreen screen, boolean shadowPass) {
		if (scene.camera instanceof CameraFP)
			return;
		glEnable(GL_TEXTURE_2D);
		pushMatrix();
		multMatrix(childEntities[0].matrix.mat);
		screen.renderModel(Model.HEAD);
		//TODO: Dafuq?
		popMatrix();
		pushMatrix();
		multMatrix(childEntities[1].matrix.mat);
		screen.renderModel(Model.TORSO);
		popMatrix();
		glBindTexture(GL_TEXTURE_2D, Texture.NONE);
		for (int i = 2; i < 6; i++) {
			pushMatrix();
			multMatrix(childEntities[i].matrix.mat);
			glColor3f(1.0F, 0.0F, 0.0F);
			screen.renderModel(Model.SPHERE);
			popMatrix();
		}
		glDisable(GL_TEXTURE_2D);
		
		/*if (msgTimer > 0) {
			glPushMatrix();
			glTranslatef(x, y-40, 0);
			//glTranslatef(0, screen.getTTFont().getHeight(), 0);
			glDisable(GL_CULL_FACE);
			glDisable(GL_LIGHTING);
			glScalef(1, 1, 1);
			screen.setColor(Color.BLACK);
			screen.drawString(lastMsg, 1, 1, 1, 1, TrueTypeFont.ALIGN_CENTER);
			screen.setColor(Color.WHITE);
			screen.drawString(lastMsg, 0, 0, 1, 1, TrueTypeFont.ALIGN_CENTER);
			glEnable(GL_LIGHTING);
			glEnable(GL_CULL_FACE);
			glPopMatrix();
		}*/
	}

	@Override
	public float getMass() {
		return 1.0F;
	}

	@Override
	public ConvexShape getCollisionShape() {
		return new CapsuleShape(0.5F, 1.8F);
	}

	@Override
	public Vec3F getInertiaTensor() {
		return new Vec3F(0.0F, 0.0F, 0.0F);
	}

	@Override
	public boolean isKinematic() {
		return true;
	}
}
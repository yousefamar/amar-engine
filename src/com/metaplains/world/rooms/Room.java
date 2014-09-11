package com.metaplains.world.rooms;

import static com.metaplains.gfx.GL.*;
import static org.lwjgl.opengl.GL11.*;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.linearmath.Transform;
import com.metaplains.core.GameScreen;
import com.metaplains.entities.EntityTangible;
import com.metaplains.gfx.Model;
import com.metaplains.gfx.Shader;
import com.metaplains.gfx.Texture;
import com.metaplains.gfx.cam.ClippingSphere;
import com.metaplains.utils.Vec3F;
import com.metaplains.world.EntityManager;
import com.metaplains.world.scenes.Scene;

public class Room extends EntityTangible {

	public Room(Scene scene, float x, float y, float z) {
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
		//screen.renderModel(Model.TREE);
		//Shader.useProgram(Shader.NONE);
		//glDisable(GL_LIGHTING);
		glBindTexture(GL_TEXTURE_2D, Texture.NONE);
		glColor3f(0.5F, 0.5F, 1.0F);
		glBegin(GL_QUADS);
		glNormal3f(0, 1, 0);
		glVertex3f(-20, 1-1, 20);
		glNormal3f(0, 1, 0);
		glVertex3f(20, 1-1, 20);
		glNormal3f(0, 1, 0);
		glVertex3f(20, 1-1, -20);
		glNormal3f(0, 1, 0);
		glVertex3f(-20, 1-1, -20);
		glEnd();
//		glColor3f(1.0F, 0.0F, 0.0F);
//		screen.renderModel(Model.TREE);
		//glEnable(GL_LIGHTING);
		//Shader.useProgram(Shader.STD);
	}

	@Override
	public float getMass() {
		return 0.0F;
	}

	@Override
	public CollisionShape getCollisionShape() {
		CompoundShape houseShape = new CompoundShape();
		Matrix4f transMat = new Matrix4f();
		transMat.setIdentity();
		Transform trans = new Transform(transMat);
		houseShape.addChildShape(trans, new BoxShape(new Vector3f(20, 1F, 20)));//new StaticPlaneShape(new Vector3f(0, 1, 0), -1));
		return houseShape;
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
package com.metaplains.gfx.cam;

import static org.lwjgl.opengl.GL11.*;
import static com.metaplains.gfx.GL.*;
import static org.lwjgl.util.glu.GLU.*;
import java.nio.*;
import org.lwjgl.BufferUtils;
import com.metaplains.utils.*;

public abstract class Camera {

	public float distance = 1F;
	public Quat4F rotation = new Quat4F();
	public Vec3F eyeVec = new Vec3F(0F, 0F, 0F);
	public Vec3F lookVec = new Vec3F(0F, 0F, -1F);
	public Vec3F upVec = new Vec3F(0F, 1F, 0F);
	public Frustum frustum = new Frustum();

	public void setDollyDistance(float distance) {
		this.distance = distance;
		eyeVec.set(Geom.dir(lookVec, eyeVec).normalise().scale(distance).translate(lookVec));
	}
	
	public void look(Vec3F dir) {	
		lookVec.set(Geom.add(eyeVec, dir));
		rotation.look(dir);
	}
	
	public void translatePosition(Vec3F vec) {
		this.translatePosition(vec.x, vec.y, vec.z);
	}
	
	public void translatePosition(float x, float y, float z) {
		eyeVec.translate(x, y, z);
		lookVec.translate(x, y, z);
	}
	
	public void setPosition(Vec3F vec) {
		this.setPosition(vec.x, vec.y, vec.z);
	}
	
	public void setPosition(float x, float y, float z) {
		eyeVec.set(x, y, z);
		lookVec.set(x, y, z);
	}
	
	/*public void setTransform(Transform trans) {
		Matrix4f transMat = new Matrix4f(trans.matrix.mat);
		Vector3f eyeVec = new Vector3f(0F, 0F, 0F);
		Vector3f lookVec = new Vector3f(0F, 0F, -1F);
		Vector3f upVec = new Vector3f(0F, 1F, 0F);
		
		transMat.transform(eyeVec);
		transMat.transform(lookVec);
		transMat.transform(upVec);
		
		this.eyeVec.setVec(eyeVec);
		this.lookVec.setVec(lookVec);
		this.upVec.setVec(upVec);
	}*/
	
	public void setAngles(float yaw, float pitch, float roll) {
		if (pitch > 89.9F)
			pitch = 89.9F;
		else if (pitch < -89.9F)
			pitch = -89.9F;
		rotation.set(yaw, pitch, roll);
		lookVec.set(new Vec3F(0, 0, -distance).rotateX(-pitch).rotateY(yaw).translate(eyeVec));
		float sinRoll = FastMath.sin(roll);
		upVec.set(-sinRoll, FastMath.cos(roll), sinRoll);
	}
	
	//TODO: Make this a Vec3f function (RotateAround).
	public void setKite(float yaw, float pitch, float roll) {
		if (pitch > -0.1F)
			pitch = -0.1F;
		else if (pitch < -89.9F)
			pitch = -89.9F;
		rotation.set(yaw, pitch, roll);
		eyeVec.set(new Vec3F(0, 0, distance).rotateX(-pitch).rotateY(yaw).translate(lookVec));
		float sinRoll = FastMath.sin(roll);
		upVec.set(-sinRoll, FastMath.cos(roll), sinRoll);
	}

	public Vec2I perspective(int x, int y) {
		//Dimension screenDims = GameClient.game.screen.getScreenDims();
		FloatBuffer modelview = BufferUtils.createFloatBuffer(16);
		glGetFloat(GL_MODELVIEW_MATRIX, modelview);

		FloatBuffer projection = BufferUtils.createFloatBuffer(16);
		glGetFloat(GL_PROJECTION_MATRIX, projection);
		
		IntBuffer viewport = BufferUtils.createIntBuffer(16);
		glGetInteger(GL_VIEWPORT, viewport);
		
		FloatBuffer position = BufferUtils.createFloatBuffer(3);
		FloatBuffer winZ = BufferUtils.createFloatBuffer(1);
		
		float winX = (float) x, winY = (float) viewport.get(3) - (float) y;
		glReadPixels(x, (int) winY, 1, 1, GL_DEPTH_COMPONENT, GL_FLOAT, winZ);
		gluUnProject(winX, winY, winZ.get(), modelview, projection, viewport, position);
		return new Vec2I((int)position.get(0), (int)position.get(2));
	}
	
	public abstract void tick();
	
	public void onRender(boolean updateFrustum) {
		setMatrixMode(VIEW);
		lookAt(eyeVec, lookVec, upVec);
		setMatrixMode(MODEL);
		if (updateFrustum)
			frustum.updateFrustum();
	}
}

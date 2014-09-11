package com.metaplains.utils;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

public class Transform {
	public Mat4F matrix = new Mat4F();
	public FloatBuffer matrixBuffer;
	public Vec3F position = new Vec3F();
	//public Quat4F rotation = new Quat4F();
	
	public Transform() {
	}

	public Transform(float x, float y, float z) {
		this();
		setTranslation(x, y, z);
	}
	
	public Transform(Transform transform) {
		this();
		setTransform(transform);
	}
	
	public Transform setTransform(Transform transform) {
		matrix.set(transform.matrix.mat);
		position.set(matrix.mat[12], matrix.mat[13], matrix.mat[14]);
		//rotation.set(new Quat4F(transform.getRotation(new Quat4f())));
		updateBuffer();
		return this;
	}

	public Transform setTransform(com.bulletphysics.linearmath.Transform transform) {
		transform.getOpenGLMatrix(matrix.mat);
		position.set(matrix.mat[12], matrix.mat[13], matrix.mat[14]);
		//rotation.set(new Quat4F(transform.getRotation(new Quat4f())));
		updateBuffer();
		return this;
	}
	
	public Transform multTransform(Transform transform) {
		matrix.multiply(transform.matrix);
		position.set(matrix.mat[12], matrix.mat[13], matrix.mat[14]);
		//rotation.set(/*TODO: Matrix to Quaternion. */);
		updateBuffer();
		return this;
	}
	
	private void updateBuffer() {
		//TODO: Remove temp if not multithreading and use toFloatBuffer().
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		buffer.clear();
		buffer.put(matrix.mat);
		buffer.flip();
		matrixBuffer = buffer;
	}
	
	public Transform setTranslation(float x, float y, float z) {
		matrix.setTranslation(x, y, z);
		position.set(matrix.mat[12], matrix.mat[13], matrix.mat[14]);
		updateBuffer();
		return this;
	}
	
	public Transform setTranslation(Vec3F vec) {
		setTranslation(vec.x, vec.y, vec.z);
		return this;
	}
	
	public Vec3F getTranslation() {
		return position.clone();
	}
	
	public Transform setRotation(Quat4F quat) {
		matrix.setRotation(quat);
	    updateBuffer();
	    return this;
	}
	
	public Transform translate(float x, float y, float z) {
		matrix.translate(x, y, z);
		position.set(matrix.mat[12], matrix.mat[13], matrix.mat[14]);
		updateBuffer();
		return this;
	}
	
/*	public Transform rotateX(float angle) {
		matrix.multiply(getRotX(angle).matrix);
		return this;
	}
	
	public Transform rotateY(float angle) {
		matrix.multiply(getRotY(angle).matrix);
		return this;
	}
	
	public Transform rotateZ(float angle) {
		matrix.multiply(getRotZ(angle).matrix);
		return this;
	}
	
	public Transform scale(float scaleX, float scaleY, float scaleZ) {
		matrix.multiply(getScale(scaleX, scaleY, scaleZ).matrix);
		return this;
	}
	
	public static Transform getTrans(float x, float y, float z) {
		return new Transform(new Mat4F(new float[]{
				 1, 0, 0, x,
				 0, 1, 0, y,
				 0, 0, 1, z,
				 0, 0, 0, 1
		}).transpose());
	}
	
	public static Transform getRotX(float angle) {
		float ca = FastMath.cos(angle);
		float sa = FastMath.sin(angle);
		return new Transform(new Mat4F(new float[]{
				 1, 0, 0, 0,
				 0, ca, -sa, 0,
				 0, sa, ca, 0,
				 0, 0, 0, 1
		}).transpose());
	}
	
	public static Transform getRotY(float angle) {
		float ca = FastMath.cos(angle);
		float sa = FastMath.sin(angle);
		return new Transform(new Mat4F(new float[]{
				 ca, 0, sa, 0,
				 0, 1, 0, 0,
				 -sa, 0, ca, 0,
				 0, 0, 0, 1
		}).transpose());
	}
	
	public static Transform getRotZ(float angle) {
		float ca = FastMath.cos(angle);
		float sa = FastMath.sin(angle);
		return new Transform(new Mat4F(new float[]{
				 ca, -sa, 0, 0,
				 sa, ca, 0, 0,
				 0, 0, 1, 0,
				 0, 0, 0, 1
		}).transpose());
	}
	
	public static Transform getScale(float scaleX, float scaleY, float scaleZ) {
		return new Transform(new Mat4F(new float[]{
				 scaleX, 0, 0, 0,
				 0, scaleY, 0, 0,
				 0, 0, scaleZ, 0,
				 0, 0, 0, 1
		}).transpose());
	}*/
}
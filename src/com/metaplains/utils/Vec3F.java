package com.metaplains.utils;

import javax.vecmath.Vector3f;

public class Vec3F implements Cloneable {
	
	public static final Vec3F ZERO = new Vec3F();
	public static final Vec3F UNIT_X = new Vec3F(1,0,0);
	public static final Vec3F UNIT_Y = new Vec3F(0,1,0);
	public static final Vec3F UNIT_Z = new Vec3F(0,0,1);
	public static final Vec3F NORTH = new Vec3F(0,0,-1);
	public static final Vec3F EAST = new Vec3F(1,0,0);
	public static final Vec3F UP = new Vec3F(0,1,0);

	public float x, y, z;
	
	public Vec3F(float x, float y, float z) {
		set(x, y, z);
	}
	
	public Vec3F() {
		this(0, 0, 0);
	}

	public void set(Vec3F vec) {
		set(vec.x, vec.y, vec.z);
	}

	public Vec3F(Vector3f vec) {
		set(vec.x, vec.y, vec.z);
	}

	public void set(javax.vecmath.Vector3f vec) {
		set(vec.x, vec.y, vec.z);
	}

	public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vec3F translate(Vec3F vec) {
		return this.translate(vec.x, vec.y, vec.z);
	}

	public Vec3F translate(float x, float y, float z) {
		this.set(this.x + x, this.y + y, this.z + z);
		return this;
	}
	
	public Vec3F rotateX(float angle) {
		float ca = FastMath.cos(angle);
		float sa = FastMath.sin(angle);
		this.set(x, ca*y+sa*z, ca*z-sa*y);
		return this;
	}

	public Vec3F rotateY(float angle) {
		float ca = FastMath.cos(angle);
		float sa = FastMath.sin(angle);
		this.set(ca*x-sa*z, y, sa*x+ca*z);
		return this;
	}

	public Vec3F rotateZ(float angle) {
		float ca = FastMath.cos(angle);
		float sa = FastMath.sin(angle);
		this.set(ca*x+sa*y, sa*x+ca*y, z);
		return this; 
	}

	public Vec3F rotateA(Vec3F axis, int angle) {
		// TODO: Understand the math to implement this.
		float ca = FastMath.cos(angle);
		float sa = FastMath.sin(angle);
		this.set(x, ca*y+sa*z, ca*z-sa*y);
		return this;
	}

	public Vec3F rotate(Quat4F quat) {
		if (quat.w > 1)
			quat.normalise();
		float s = (float) FastMath.sqrt(1.0F-quat.w*quat.w);
		if (s < 0.001) {
			x = 1;
			y = z = 0;
		} else {
			x = quat.x / s;
			y = quat.y / s;
			z = quat.z / s;
		}
		return this;
	}

	public Vec3F scale(float amount) {
		this.set(x*amount, y*amount, z*amount);
		return this;
	}
	
	public Vec3F scaleX(float amount) {
		this.set(x*amount, y, z);
		return this;
	}

	public Vec3F scaleY(float amount) {
		this.set(x, y*amount, z);
		return this;
	}
	
	public Vec3F scaleZ(float amount) {
		this.set(x, y, z*amount);
		return this;
	}
	
	public Vec3F normalise() {
		float mag = Geom.mag(this);
		if (mag != 0)
			this.set(x/mag, y/mag, z/mag);
		return this;
	}

	public Vec3F reset() {
		this.set(0, 0, 0);
		return this;
	}
	
	@Override
	public Vec3F clone() {
		try {
			return (Vec3F)super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public String toString() {
		return "("+x+", "+y+", "+z+")";
	}
	
	public javax.vecmath.Vector3f toVector3fJavax() {
		return new javax.vecmath.Vector3f(x, y, z);
	}
	
	public org.lwjgl.util.vector.Vector3f toVector3fLWJGL() {
		return new org.lwjgl.util.vector.Vector3f(x, y, z);
	}
}
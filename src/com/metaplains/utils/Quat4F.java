package com.metaplains.utils;

import javax.vecmath.Quat4f;
import org.lwjgl.util.vector.Quaternion;

public class Quat4F implements Cloneable {

	public static final Quat4F ZERO = new Quat4F();
	
	public float x, y, z, w;
	
	public Quat4F(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	public Quat4F() {
		this(0, 0, 0, 1);
	}
	
	public Quat4F(Quat4f quat) {
		this(quat.x, quat.y, quat.z, quat.w);
	}

	public void set(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	public void set(Quat4F quat) {
		set(quat.x, quat.y, quat.z, quat.w);
	}
	
	public void set(float yaw, float pitch, float roll) {
		//TODO: Take account of roll.
		look(new Vec3F(0,0,-1).rotateX(-pitch).rotateY(yaw));
	}
	
	//TODO: Take account of up vector for roll.
	public Quat4F look(Vec3F dir) {
		//TODO: Why does this work even though the math is wrong?
		dir.normalise();
		Vec3F right = Vec3F.EAST;//Geom.cross(dir, up).normalise();
		Vec3F up = Geom.cross(dir, right).normalise();
		float t = up.x + right.y + dir.z;
		
		/*w = (float) (FastMath.sqrt(1.0F + t) * 0.5F);
		System.out.println(w);
		float w4r = 1.0F / (4.0F * w);
		x = (up.z - dir.y) * w4r;
		y = (dir.x - right.z) * w4r;
		z = (right.y - up.x) * w4r;*/
		
		if (t >= 0.0F) {
			float s = (float) FastMath.sqrt(t + 1.0F);
			this.w = (0.5F * s);
			s = 0.5F / s;
			this.x = ((right.z - dir.y) * s);
			this.y = ((dir.x - up.z) * s);
			this.z = ((up.y - right.x) * s);
		} else if ((up.x > right.y) && (up.x > dir.z)) {
			float s = (float) FastMath.sqrt(1.0F + up.x - right.y - dir.z);
			this.x = (s * 0.5F);
			s = 0.5F / s;
			this.y = ((up.y + right.x) * s);
			this.z = ((dir.x + up.z) * s);
			this.w = ((right.z - dir.y) * s);
		} else if (right.y > dir.z) {
			float s = (float) FastMath.sqrt(1.0F + right.y - up.x - dir.z);
			this.y = (s * 0.5F);
			s = 0.5F / s;
			this.x = ((up.y + right.x) * s);
			this.z = ((right.z + dir.y) * s);
			this.w = ((dir.x - up.z) * s);
		} else {
			float s = (float) FastMath.sqrt(1.0F + dir.z - up.x - right.y);
			this.z = (s * 0.5F);
			s = 0.5F / s;
			this.x = ((dir.x + up.z) * s);
			this.y = ((right.z + dir.y) * s);
			this.w = ((up.y - right.x) * s);
		}
		return this;
	}

	public Quat4F normalise() {
		float mag = Geom.mag(this);
		if (mag != 0)
			this.set(x/mag, y/mag, z/mag, w/mag);
		return this;
	}

	@Override
	public Quat4F clone() {
		return new Quat4F(x, y, z, w);
	}

	@Override
	public String toString() {
		return "("+x+", "+y+", "+z+", "+w+")";
	}
	
	public Quat4f toQuat4f() {
		Quat4f quat = new Quat4f();
		quat.set(x, y, z, w);
		return quat;
	}
	
	public Quaternion toQuaternion() {
		return new Quaternion(x, y, z, w);
	}
}
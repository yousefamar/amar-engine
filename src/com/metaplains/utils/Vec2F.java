package com.metaplains.utils;

public class Vec2F {

	public float x;
	public float y;

	public Vec2F(float x, float y) {
		setVec(x, y);
	}

	public void setVec(Vec2F vec) {
		setVec(vec.x, vec.y);
	}
	
	public void setVec(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Vec2F translate(Vec2F vec) {
		return this.translate(vec.x, vec.y);
	}

	public Vec2F translate(float x, float y) {
		this.setVec(this.x + x, this.y + y);
		return this;
	}
	
	public Vec2F rotateZ(double theta) {
		float ct = FastMath.cos((float) Math.toDegrees(theta));
		float st = FastMath.sin((float) Math.toDegrees(theta));
		this.setVec(ct*x-st*y, st*x+ct*y);
		return this; 
	}

	public Vec2F scale(float amount) {
		this.setVec(x*amount, y*amount);
		return this;
	}
	
	public Vec2F scaleXY(float xAmount, float yAmount) {
		this.setVec(x*xAmount, y*yAmount);
		return this;
	}
	
	public Vec2F scaleX(float amount) {
		this.setVec(x*amount, y);
		return this;
	}

	public Vec2F scaleY(float amount) {
		this.setVec(x, y*amount);
		return this;
	}
	
	/*public Vec2D normalise() {
		float mag = Geom.mag(this);
		this.setVec(x/mag, y/mag);
		return this;
	}*/

	public Vec2F reset() {
		this.setVec(0F, 0F);
		return this;
	}
	
	public String toString() {
		return "("+x+", "+y+")";
	}
}

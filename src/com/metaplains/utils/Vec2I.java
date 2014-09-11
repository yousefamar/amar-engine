package com.metaplains.utils;

public class Vec2I {

	public int x;
	public int y;

	public Vec2I(int x, int y) {
		setVec(x, y);
	}

	public void setVec(Vec2I vec) {
		setVec(vec.x, vec.y);
	}
	
	public void setVec(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Vec2I translate(Vec2I vec) {
		return this.translate(vec.x, vec.y);
	}

	public Vec2I translate(int x, int y) {
		this.setVec(this.x + x, this.y + y);
		return this;
	}
	
	public Vec2I rotateZ(double theta) {
		float ct = FastMath.cos((float) Math.toDegrees(theta));
		float st = FastMath.sin((float) Math.toDegrees(theta));
		this.setVec((int) (ct*x-st*y), (int) (st*x+ct*y));
		return this; 
	}

	public Vec2I scale(float amount) {
		this.setVec((int) (x*amount), (int) (y*amount));
		return this;
	}
	
	public Vec2I scaleXY(float xAmount, float yAmount) {
		this.setVec((int) (x*xAmount), (int) (y*yAmount));
		return this;
	}
	
	public Vec2I scaleX(float amount) {
		this.setVec((int) (x*amount), y);
		return this;
	}

	public Vec2I scaleY(float amount) {
		this.setVec(x, (int) (y*amount));
		return this;
	}
	
	/*public Vec2D normalise() {
		float mag = Geom.mag(this);
		this.setVec(x/mag, y/mag);
		return this;
	}*/

	public Vec2I reset() {
		this.setVec(0, 0);
		return this;
	}
	
	public String toString() {
		return "("+x+", "+y+")";
	}
}

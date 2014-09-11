package com.metaplains.utils;

public class AABB {

	public float minX, minY, minZ;
	public float maxX, maxY, maxZ;
	public float width, height;

	public AABB(float x, float y, float z, float width, float height) {
		this.minX = x;
		this.minY = y;
		this.minZ = z;
		this.maxX = x + width;
		this.maxY = y + height;
		this.maxZ = z + width;
		this.width = width;
		this.height = height;
	}
	
	public void setPosition(float x, float y, float z) {
		this.minX = x;
		this.minY = y;
		this.minZ = z;
		this.maxX = x + width;
		this.maxY = y + height;
		this.maxZ = z + width;
	}
	
	public void setSize(float width, float height) {
		this.maxX = this.minX + width;
		this.maxY = this.minY + height;
		this.maxZ = this.minZ + width;
		this.width = width;
		this.height = height;
	}
	
	public boolean contains(float x, float y, float z) {
		return (x >= minX && x <= maxX) && (y >= minY && y <= maxY) && (z >= minZ && z <= maxZ);
	}
}
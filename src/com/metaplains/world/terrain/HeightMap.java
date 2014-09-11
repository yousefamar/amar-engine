package com.metaplains.world.terrain;

public class HeightMap {

	public int width, height;
	public float[][] heights;

	public HeightMap(int width, int height) {
		this.width = width;
		this.height = height;
		heights = new float[width][height];
	}
	
	public float getHeight(int x, int z) {
		try {
			return heights[x][z];
		}catch (ArrayIndexOutOfBoundsException e) {
			return Float.NEGATIVE_INFINITY;
		}
	}
}
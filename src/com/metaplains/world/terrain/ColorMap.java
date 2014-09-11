package com.metaplains.world.terrain;

public class ColorMap {

	public int width, height;
	public float[][][] colors;

	public ColorMap(int width, int height) {
		this.width = width;
		this.height = height;
		colors = new float[width][height][4];
	}
	
	public float[] getColor(int x, int z) {
		try {
			return colors[x][z];
		}catch (ArrayIndexOutOfBoundsException e) {
			return new float[]{1.0F, 1.0F, 1.0F, 1.0F};
		}
	}
}
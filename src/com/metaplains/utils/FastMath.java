package com.metaplains.utils;

import javax.vecmath.Quat4f;

public class FastMath {

	public static Vec3F QuaternionToEuclidean(Quat4f rotation) {
		Vec3F rotationaxes = new Vec3F(0,0,0);
		rotationaxes.x = (float)Math.asin(2 * (rotation.w * rotation.y - rotation.z * rotation.x));
		float test = rotation.x * rotation.y + rotation.z * rotation.w;
		if (test == .5f) {
			rotationaxes.y = 2 * (float)Math.atan2(rotation.x, rotation.w);
			rotationaxes.z = 0;
		} else if (test == -.5f) {
			rotationaxes.y = -2 * (float)Math.atan2(rotation.x, rotation.w);
			rotationaxes.z = 0;
		} else {
			rotationaxes.y = (float)Math.atan(2 * (rotation.w * rotation.z + rotation.y * rotation.y) / (1 - 2 * (rotation.y * rotation.y + rotation.z * rotation.z)));
			rotationaxes.z = (float)Math.atan(2 * (rotation.w * rotation.x + rotation.y * rotation.z) / (1 - 2 * (rotation.x * rotation.x + rotation.y * rotation.y)));
		}
		return rotationaxes;
	}

	private static float sinBuffer[] = loadSinBuffer();
	private static float cosBuffer[] = loadCosBuffer();
	private static float tanBuffer[] = loadTanBuffer();

	// TODO: Investigate modulus operator for negative numbers.

	public static float sin(float degree) {
		try {
			return sinBuffer[(int)(degree*100.0F)];
		} catch (ArrayIndexOutOfBoundsException e) {
			while (degree < 0)
				degree += 360;
			while (degree >= 360)
				degree -= 360;
			return sinBuffer[(int)(degree*100.0F)];
		}
	}
	
	public static float cos(float degree) {
		try {
			return cosBuffer[(int)(degree*100.0F)];
		} catch (ArrayIndexOutOfBoundsException e) {
			while (degree < 0)
				degree += 360;
			while (degree >= 360)
				degree -= 360;
			return cosBuffer[(int)(degree*100.0F)];
		}
	}
	
	public static float tan(float degree) {
		try {
			return tanBuffer[(int)(degree*100.0F)];
		} catch (ArrayIndexOutOfBoundsException e) {
			while (degree < 0)
				degree += 360;
			while (degree >= 360)
				degree -= 360;
			return tanBuffer[(int)(degree*100.0F)];
		}
	}

	private static float[] loadSinBuffer() {
		float sinBuffer[] = new float[36000];
		for (int i = 0; i < sinBuffer.length; i++)
			sinBuffer[i] = (float) Math.sin(Math.toRadians(((double)i)/100.0D));
		return sinBuffer;
	}

	private static float[] loadCosBuffer() {
		float cosBuffer[] = new float[36000];
		for (int i = 0; i < cosBuffer.length; i++)
			cosBuffer[i] = (float) Math.cos(Math.toRadians(((double)i)/100.0D));
		return cosBuffer;
	}
	
	private static float[] loadTanBuffer() {
		float tanBuffer[] = new float[36000];
		for (int i = 0; i < tanBuffer.length; i++)
			tanBuffer[i] = (float) Math.tan(Math.toRadians(((double)i)/100.0D));
		return tanBuffer;
	}
	
	public static float abs(float num) {
		if (num<0) num*=-1;
		return num;
	}

	//TODO: Account for negative numbers.
	
	public static int floor(float num) {
		return (int) num;
	}
	
	public static int ceil(float num) {
		return floor(num)+1;
	}
	
	public static int roundInt(float num) {
		return (num-floor(num))<0.5F?floor(num):ceil(num);
	}
	
	public static double pow(float num, int pow) {
		//TODO: Find a faster algorithm.
		return Math.pow(num, pow);
	}
	
	public static double sqrt(float num) {
		//TODO: Find a faster algorithm.
		return Math.sqrt(num);
	}

	public static String mergeCoords(int x, int y, int z) {
		return x+""+y+""+z;
	}
	
	public static String mergeCoords(int x, int z) {
		return x+""+z;
	}
}
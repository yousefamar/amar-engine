package com.metaplains.utils;

public class Noise {

	public static double perlin2D(int seed, double x, double y) {
		float gain = 0.25F;//0.707106781F;
		double octaves = 6;
		double total = 0;
		for (int i = 0; i < octaves; i++) {
			double frequency = FastMath.pow(2, i)/20.0F;
			double amplitude = FastMath.pow(gain, i);
			total += biLerpedSmoothNoise(seed, x*frequency, y*frequency)*amplitude;
		}
		return total;
	}

	public static double perlin3D(int seed, double x, double y, double z) {
		float gain = 0.25F;//0.707106781F;
		double octaves = 4;
		double total = 0;
		for (int i = 0; i < octaves; i++) {
			double frequency = FastMath.pow(2, i);
			double amplitude = FastMath.pow(gain, i);
			total += triLerpedSmoothNoise(seed, x*frequency, y*frequency, z*frequency)*amplitude;
		}
		return total;
	}

	public static double fBm(int seed, double x, double y, double z) {
		float gain = 0.5F;
		double lacunarity = 2;
		double frequency = 1.0F;
		double amplitude = 10;
		double octaves = 4;
		double total = 0;
		for (int i = 0; i < octaves; i++) {
			total += triLerpedSmoothNoise(seed, x*frequency, y*frequency, z*frequency)*amplitude;
			frequency *= lacunarity;
			amplitude *= gain;
		}
		return total;
	}

	public static double biLerpedSmoothNoise(int seed, double x, double y) {
		int xi = (int)x;
		int yi = (int)y;
		double muX = x-xi;
		double muY = y-yi;
		return biLerp(smooth2D(seed, xi, yi), smooth2D(seed, xi+1, yi), smooth2D(seed, xi, yi+1), smooth2D(seed, xi+1, yi+1), muX, muY);
	}

	public static double triLerpedSmoothNoise(int seed, double x, double y, double z) {
		int xi = (int)x;
		int yi = (int)y;
		int zi = (int)z;
		double muX = x-xi;
		double muY = y-yi;
		double muZ = z-zi;
		return lerp(biLerp(smooth3D(seed, xi, yi, zi), smooth3D(seed, xi+1, yi, zi), smooth3D(seed, xi, yi+1, zi), smooth3D(seed, xi+1, yi+1, zi), muX, muY),
				biLerp(smooth3D(seed, xi, yi, zi+1), smooth3D(seed, xi+1, yi, zi+1), smooth3D(seed, xi, yi+1, zi+1), smooth3D(seed, xi+1, yi+1, zi+1), muX, muY), muZ);
	}

	public static double triLerp(double cs[][][], double muX, double muY, double muZ){
		return lerp(biLerp(cs[0][0][0], cs[1][0][0], cs[0][1][0], cs[1][1][0], muX, muY),
				biLerp(cs[0][0][1], cs[1][0][1], cs[0][1][1], cs[1][1][1], muX, muY), muZ);
	}

	public static double biLerp(double c00, double c10, double c01, double c11, double muX, double muY) {
		return lerp(lerp(c00, c10, muX), lerp(c01, c11, muX), muY);
	}

	public static double lerp(double x0, double x1, double mu){
		return x0+(x1-x0)*mu;
	}

	public static double smooth2D(int seed, int x, int y) {
		double corners = (noise(seed,x-1,y-1,0)+noise(seed,x+1,y-1,0)+noise(seed,x-1,y+1,0)+noise(seed,x+1,y+1,0))/16;
		double sides = (noise(seed,x-1,y,0)+noise(seed,x+1,y,0)+noise(seed,x,y-1,0)+noise(seed,x,y+1,0))/8;
		return corners + sides + noise(seed,x,y,0)/4;
	}

	public static double smooth3D(int seed, int x, int y, int z){
		double edges = (noise(seed,x-1,y-1,z)+noise(seed,x+1,y-1,z)+noise(seed,x-1,y+1,z)+noise(seed,x+1,y+1,z)
				+noise(seed,x,y-1,z-1)+noise(seed,x-1,y,z-1)+noise(seed,x+1,y,z-1)+noise(seed,x,y+1,z-1)
				+noise(seed,x,y-1,z+1)+noise(seed,x-1,y,z+1)+noise(seed,x+1,y,z+1)+noise(seed,x,y+1,z+1))/16;
		double corners = (noise(seed,x-1,y-1,z-1)+noise(seed,x+1,y-1,z-1)+noise(seed,x-1,y+1,z-1)+noise(seed,x+1,y+1,z-1)
				+noise(seed,x-1,y-1,z+1)+noise(seed,x+1,y-1,z+1)+noise(seed,x-1,y+1,z+1)+noise(seed,x+1,y+1,z+1))/32;
		double sides = (noise(seed,x,y-1,z)+noise(seed,x-1,y,z)+noise(seed,x+1,y,z)+noise(seed,x,y+1,z)+noise(seed,x,y,z-1)+noise(seed,x,y,z+1))/8;
		return edges + corners + sides + noise(seed,x,y,z)/4;
	}

	/**
	 * Generate a linearly congruent random number in the range [-1.0, 1.0) implicitly.
	 * Primes and generators taken from libnoise as the original large ones made patterns.
	 */
	public static double noise(int seed, int x, int y, int z){
		int n = (1619*x + 31337*y + 6971*z + 1013*seed)&0x7FFFFFFF;
		n = (n>>13)^n;
		return (((n*(n*n*60493+19990303)+1376312589)&0x7FFFFFFF)/1073741824.0) - 1;
	}
}
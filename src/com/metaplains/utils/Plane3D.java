package com.metaplains.utils;

public class Plane3D {

	public Vec3F point, normal;

	public Plane3D(Vec3F point, Vec3F normal) {
		this.point = point;
		this.normal = normal;
	}
	
	public Plane3D(Vec3F v1, Vec3F v2, Vec3F v3) {
		this.point = v2;
		this.normal = Geom.normal(v1, v2, v3);
	}
	
}

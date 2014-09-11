package com.metaplains.gfx.cam;

import com.metaplains.utils.Vec3F;

public interface ICameraTarget {
	
	//TODO Make methods.
	public Vec3F position = new Vec3F(0F, 0F, 0F);
	public Vec3F lookDir = new Vec3F(0F, 0F, -1F);
	
}
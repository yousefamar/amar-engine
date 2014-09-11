package com.metaplains.gfx.cam;

public class CameraChase extends Camera {

	private ICameraTarget target;
	
	public CameraChase(ICameraTarget target) {
		this.target = target;
	}
	
	public void mouseMoved(int x, int y) {
	}
	
	@Override
	public void tick() {
	}
}
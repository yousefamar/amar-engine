package com.metaplains.gfx.cam;

public class CameraFP extends Camera {

	private ICameraTarget target;
	
	public CameraFP(ICameraTarget target) {
		this.target = target;
	}
	
	@Override
	public void tick() {
		setPosition(target.position);
		look(target.lookDir);
	}
}
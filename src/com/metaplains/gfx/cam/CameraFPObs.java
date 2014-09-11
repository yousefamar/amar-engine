package com.metaplains.gfx.cam;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.metaplains.utils.FastMath;
import com.metaplains.utils.Vec3F;

public class CameraFPObs extends Camera {

	public boolean locked;
	public float speedMultiplier = 1.0F;
	
	private float yaw, pitch, roll;
	
	public void mouseMoved(int x, int y) {
		if (!locked && /*Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) &&*/ Mouse.isButtonDown(1)) {
			//TODO: !!!
			//camera.setRotation(camera.yaw + Mouse.getDX(), camera.pitch + Mouse.getDY(), camera.roll);
			setAngles(yaw += Mouse.getDX(), pitch += Mouse.getDY(), roll);
		}
	}
	
	@Override
	public void tick() {
		//TODO: WTF are you doing...
		if (!locked) {
			float forward = 0, strafe = 0, hover = 0;
			if(Keyboard.isKeyDown(Keyboard.KEY_W))
				forward--;
			if(Keyboard.isKeyDown(Keyboard.KEY_S))
				forward++;
			if(Keyboard.isKeyDown(Keyboard.KEY_SPACE))
				hover++;
			if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
				hover--;
			if(Keyboard.isKeyDown(Keyboard.KEY_D))
				strafe++;
			if(Keyboard.isKeyDown(Keyboard.KEY_A))
				strafe--;
			if(forward!=0||strafe!=0||hover!=0) {
				//camera.translatePosition(new Vec3F(strafe*10, 0, forward*10));
				
				//TODO: Come up with a more stable algorithm for this.
				//TODO: Think of a way of combining vectors.
				//Vec3D surge = new Vec3D(0, 0, -fwd).rotateX(pitch).rotateY(yaw).translate(position.x, position.y, position.z).translate(new Vec3D(strafe, 0, 0).rotateY(yaw)).normalise().scale(0.1F);
				float cy = FastMath.cos(yaw);
				float sy = FastMath.sin(yaw);
				Vec3F surge = new Vec3F(-sy*forward + cy*strafe, hover, cy*forward + sy*strafe).normalise().scale(speedMultiplier);//The scale depends on general friction.
				translatePosition(surge);
				//TODO: Take roll into account.
			}
		}
	}
}
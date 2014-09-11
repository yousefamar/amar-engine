package com.metaplains.world.scenes;

import org.lwjgl.input.Keyboard;
import com.bulletphysics.dynamics.RigidBody;
import com.metaplains.core.GameClient;
import com.metaplains.entities.active.Ball;
import com.metaplains.entities.active.Box;
import com.metaplains.entities.active.LightBall;
import com.metaplains.entities.active.Player;
import com.metaplains.gfx.Shader;
import com.metaplains.gfx.cam.CameraFP;
import com.metaplains.gfx.gui.GUIWorld;
import com.metaplains.utils.Geom;
import com.metaplains.world.rooms.Room;

public class SceneHome extends Scene {

	@Override
	public void init() {
		super.init();
		camera = new CameraFP(player);
		//camera.translatePosition(128, 50, 128);
		//camera.setDollyDistance(200);
		//camera.setKite(0F, -45F, 0F);
		entityManager.spawnEntity(player = new Player(this, 0, 5, 0));
		entityManager.spawnEntity(new Room(this, 0, 0, 0));
	}
	
	@Override
	public void mouseMoved(int x, int y) {
		//TODO: Grab cursor and draw a custom one.
		player.mouseMoved(x, y);
		//((CameraFPObs)camera).mouseMoved(x, y);
	}
	
	@Override
	public void keyPressed(int keyCode, char keyChar) {
		if (keyCode == Keyboard.KEY_SPACE) {
			player.jump();
		} else if (keyCode == Keyboard.KEY_R) {
			Shader.loadLocalShaderProgs();
		} else if (keyCode == Keyboard.KEY_P) {
			entityManager.spawnEntity(player = new Player(this, camera.eyeVec.x, camera.eyeVec.y, camera.eyeVec.z));
		} else if (keyCode == Keyboard.KEY_C) {
			Box box = new Box(this, camera.lookVec.x, camera.lookVec.y, camera.lookVec.z);
			entityManager.spawnEntity(box);
			//TODO: Write a getter to avoid casting.
			((RigidBody) box.collisionObject).setLinearVelocity(Geom.dir(camera.eyeVec, camera.lookVec).scale(10.0F).toVector3fJavax());
		} else if (keyCode == Keyboard.KEY_B) {
			Ball ball = new Ball(this, camera.lookVec.x, camera.lookVec.y, camera.lookVec.z);
			entityManager.spawnEntity(ball);
			((RigidBody) ball.collisionObject).setLinearVelocity(Geom.dir(camera.eyeVec, camera.lookVec).scale(10.0F).toVector3fJavax());
		} else if (keyCode == Keyboard.KEY_L) {
			LightBall lightBall = new LightBall(this, camera.lookVec.x, camera.lookVec.y, camera.lookVec.z);
			entityManager.spawnEntity(lightBall);
			((RigidBody) lightBall.collisionObject).setLinearVelocity(Geom.dir(camera.eyeVec, camera.lookVec).scale(10.0F).toVector3fJavax());
		} else if (keyCode == Keyboard.KEY_T) {
			((GUIWorld) GameClient.game.currentGUIScreen).setChatVisible();
		} else if (keyCode == Keyboard.KEY_ESCAPE) {
			GameClient.game.isCloseRequested = true;
		}
	}
	
	@Override
	public void setPlayerMessage(String message) {
		player.setMessage(message);
	}
	
	@Override
	public boolean isSinglePlayer() {
		return true;
	}
}
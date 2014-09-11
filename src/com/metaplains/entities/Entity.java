package com.metaplains.entities;

import static com.metaplains.gfx.GL.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import java.util.ArrayList;

import com.metaplains.core.GameClient;
import com.metaplains.core.GameScreen;
import com.metaplains.entities.lights.Light;
import com.metaplains.gfx.Shader;
import com.metaplains.gfx.cam.ClippingSphere;
import com.metaplains.utils.Transform;
import com.metaplains.world.scenes.Scene;

public class Entity {

	public Scene scene;
	public Transform transform;
	//TODO: Encapsulate.
	public Entity parent;
	public ArrayList<Entity> childEntities;
	
	public Entity(Scene scene, float x, float y, float z) {
		this.scene = scene;
		//TODO: Manage IDs in PacketHandler.
		transform = new Transform(x, y, z);
	}

	public Entity(Scene scene) {
		this(scene, 0, 0, 0);
	}
	
	public void init() {
	}
	
	public Transform getWorldTransform() {
		if (parent!=null)
			return parent.getWorldTransform().multTransform(transform);
		return new Transform(transform);
	}
	
	/**
	 * @return A bounding sphere that is tested against for frustum culling. If null, the entity will not be automatically clipped.
	 */
	//TODO: Allow for multiple spheres.
	public ClippingSphere getClippingSphere() {
		//TODO: Default clipping sphere method.
		return null;
	}
	
	public void render(GameScreen screen, boolean shadowPass) {
		//NB: An entity renders itself first then super.render()s to render all child entities.
		if (childEntities == null)
			return;
		for (Entity entity : childEntities) {
			pushMatrix();
			multMatrix(entity.transform.matrix.mat);
			useProgram(Shader.STD);
			glColor4f(1, 1, 1, 1);
			entity.render(screen, shadowPass);
			popMatrix();
		}
	}
	
	public boolean addChild(Entity entity) {
		entity.parent = this;
		if (childEntities == null)
			childEntities = new ArrayList<Entity>();
		if (childEntities.add(entity)) {
			//TODO: Find a better way to do this.
			if (entity instanceof EntityTangible)
				GameClient.game.currentScene.entityManager.registerDynamicEntity((EntityTangible) entity);
			else if(entity instanceof Light)
				GameClient.game.currentScene.entityManager.lights.add((Light) entity);
			return true;
		}
		return false;
	}
	
	public boolean hasChildren() {
		return childEntities != null && !childEntities.isEmpty();
	}
	
	public boolean removeChild(Entity entity) {
		if (childEntities == null)
			return false;
		return childEntities.remove(entity);
	}
	
	public void destroy() {
		if (parent != null) {
			parent.removeChild(this);
			parent = null;
		}
		while(hasChildren())
			childEntities.get(0).destroy();
	}
}
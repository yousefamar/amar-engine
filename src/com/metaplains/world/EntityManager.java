package com.metaplains.world;

import static com.metaplains.gfx.GL.*;
import static org.lwjgl.opengl.GL11.*;

import java.util.*;
import javax.vecmath.*;
import com.bulletphysics.dynamics.*;
import com.bulletphysics.collision.dispatch.*;
import com.bulletphysics.collision.narrowphase.ManifoldPoint;
import com.bulletphysics.collision.narrowphase.PersistentManifold;
import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.collision.broadphase.*;
import com.bulletphysics.dynamics.constraintsolver.*;
import com.bulletphysics.linearmath.*;
import com.bulletphysics.util.ObjectArrayList;
import com.metaplains.core.GameClient;
import com.metaplains.core.GameScreen;
import com.metaplains.entities.*;
import com.metaplains.entities.active.Player;
import com.metaplains.entities.lights.Light;
import com.metaplains.gfx.DebugDrawer;
import com.metaplains.gfx.GL;
import com.metaplains.gfx.Model;
import com.metaplains.gfx.Shader;
import com.metaplains.gfx.Texture;
import com.metaplains.world.rooms.Room;
import com.metaplains.world.scenes.Scene;
import com.sun.org.apache.bcel.internal.generic.LoadInstruction;

public class EntityManager extends InternalTickCallback {

	private Entity rootEntity;
	//private HashMap<Integer, Entity> entityMap = new HashMap<Integer, Entity>();
	private Queue<Tickable> entitiesToTick = new LinkedList<Tickable>();
	//TODO: Encapsulate.
	public LinkedList<Light> lights = new LinkedList<Light>();
	private DynamicsWorld dynamicsWorld;
	private DebugDrawer debugDrawer;

	public EntityManager(Scene scene) {
		this.rootEntity = new Entity(scene);
		CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
		Vector3f worldAabbMin = new Vector3f(-100, -100, -100);
		Vector3f worldAabbMax = new Vector3f(356, 356, 356);
		int maxProxies = 1024;
		AxisSweep3 overlappingPairCache = new AxisSweep3(worldAabbMin, worldAabbMax, maxProxies);
		SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, overlappingPairCache, solver, collisionConfiguration);
		dynamicsWorld.setInternalTickCallback(this, dynamicsWorld.getWorldUserInfo());
		dynamicsWorld.setGravity(new Vector3f(0, -9.8F, 0));
		overlappingPairCache.getOverlappingPairCache().setInternalGhostPairCallback(new GhostPairCallback());

		debugDrawer = new DebugDrawer(GameClient.game.screen);
		debugDrawer.setDebugMode(1023);
		dynamicsWorld.setDebugDrawer(debugDrawer);
		
		/*CollisionShape groundShape = new BoxShape(new Vector3f(128.0F, 1.0F, 128.0F));

		collisionShapes.add(groundShape);

		Transform groundTransform = new Transform();
		groundTransform.setIdentity();
		groundTransform.origin.set(new Vector3f(128.0F, 0.0F, 128.0F));

		dynamicsWorld.addRigidBody(new RigidBody(new RigidBodyConstructionInfo(0F, new DefaultMotionState(groundTransform), groundShape, new Vector3f(0, 0, 0))));*/
	}

	//TODO: Solve safety risk (entity in ticklist but not in entity list).
	public void spawnEntity(Entity entity) {
		joinEntity(entity);
		entity.init();
	}

	public boolean joinEntity(Entity entity) {
		return rootEntity.addChild(entity);
	}

	//TODO: Split into multiple methods.
	public void registerDynamicEntity(EntityTangible entity) {
		Matrix4f mat = entity.getWorldTransform().matrix.toMatrix4f();
		
		if (entity instanceof Player) {
			((Player) entity).controller.ghostObject.setWorldTransform(new Transform(mat));
			entity.collisionObject = ((Player) entity).controller.ghostObject;
			((Player) entity).controller.ghostObject.setUserPointer(entity);
			dynamicsWorld.addCollisionObject(((Player) entity).controller.ghostObject);
			dynamicsWorld.addAction(((Player) entity).controller.controller);
			return;
		}

		CollisionShape colShape = entity.getCollisionShape();
		Vector3f localInertia = entity.getInertiaTensor().toVector3fJavax();
		float mass = entity.getMass();
		if (mass != 0.0F)
			colShape.calculateLocalInertia(mass, localInertia);
		RigidBody rigidBody = new RigidBody(mass, new DefaultMotionState(new Transform(mat)), colShape, localInertia);
		if (entity.isKinematic()) {
			rigidBody.setCollisionFlags(rigidBody.getCollisionFlags() | CollisionFlags.KINEMATIC_OBJECT);
			rigidBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
		}
		entity.collisionObject = rigidBody;
		rigidBody.setUserPointer(entity);
		dynamicsWorld.addRigidBody(rigidBody);
		
//		if (entity.hasChildren())
//			for (Entity childEntity : entity.childEntities)
//				if(childEntity instanceof EntityTangible)
//					registerDynamicEntity((EntityTangible) childEntity);
	}

	public void scheduleEntityTick(Tickable entity) {
		entitiesToTick.add(entity);
	}
	
	public boolean destroyLight(Light light) {
		return lights.remove(light);
	}
	
	@Override
	public void internalTick(DynamicsWorld world, float timeStep) {
		int numManifolds = dynamicsWorld.getDispatcher().getNumManifolds();
		for (int i = 0; i < numManifolds; i++) {
			PersistentManifold contactManifold = dynamicsWorld.getDispatcher().getManifoldByIndexInternal(i);
			CollisionObject colObjA = (CollisionObject) contactManifold.getBody0();
			CollisionObject colObjB = (CollisionObject) contactManifold.getBody1();
			EntityTangible entityA = (EntityTangible) colObjA.getUserPointer();
			EntityTangible entityB = (EntityTangible) colObjB.getUserPointer();
			if (entityA != null && entityB != null) {
				entityA.onCollision(entityB);
				entityB.onCollision(entityA);
				//TODO: Enter queue.
			}
			
			//TODO: Pass all collision information into collision event methods.
			/*int numContacts = contactManifold.getNumContacts();
			for (int j = 0; j < numContacts; j++) {
				ManifoldPoint pt = contactManifold.getContactPoint(j);
				if (pt.getDistance() < 0.0F) {
					Vector3f ptA = pt.getPositionWorldOnA(new Vector3f());
					Vector3f ptB = pt.getPositionWorldOnB(new Vector3f());
					Vector3f normalOnB = pt.normalWorldOnB;
				}
			}*/
		}
	}
	
	public void tick() {
		int size = entitiesToTick.size();
		for (int i = 0; i < size; i++)
			entitiesToTick.poll().tick();

		//TODO: Standardize.
		dynamicsWorld.stepSimulation(1.0F, 10);
		
		/*for (int j=dynamicsWorld.getNumCollisionObjects()-1; j>=0; j--) {
			CollisionObject obj = dynamicsWorld.getCollisionObjectArray().getQuick(j);
			RigidBody body = RigidBody.upcast(obj);
			if (body != null && body.getMotionState() != null) {
				Transform trans = new Transform();
				//body.getMotionState().getWorldTransform(trans);
				ballMotionState.getWorldTransform(trans);
				System.out.printf("world pos = %f,%f,%f\n", trans.origin.x, trans.origin.y, trans.origin.z);
			}
		}*/
		//TODO: Kill entities that go too far away.
	}

	public void updateLighting(GameScreen screen) {
		for (Light light : lights)
			light.updateDepthTextures(screen);
	}
	
	public void render(GameScreen screen, boolean shadowPass) {
		pushMatrix();
		multMatrix(rootEntity.transform.matrix.mat);
		rootEntity.render(screen, shadowPass);
		popMatrix();
		if (!shadowPass) {
			glDisable(GL_LIGHTING);
			useProgram(Shader.STD);
			glBindTexture(GL_TEXTURE_2D, Texture.NONE);
			dynamicsWorld.debugDrawWorld();
			glEnable(GL_LIGHTING);
		}
	}

	public void destroy() {
		rootEntity.destroy();
	}
}
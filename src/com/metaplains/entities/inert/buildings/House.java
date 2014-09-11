package com.metaplains.entities.inert.buildings;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.vecmath.Vector3f;
import com.bulletphysics.collision.shapes.*;
import com.metaplains.core.GameScreen;
import com.metaplains.entities.EntityTangible;
import com.metaplains.entities.meta.PortalScene;
import com.metaplains.gfx.Model;
import com.metaplains.gfx.TexturedPolygon;
import com.metaplains.gfx.Model.Mesh;
import com.metaplains.gfx.cam.ClippingSphere;
import com.metaplains.utils.Vec3F;
import com.metaplains.world.scenes.Scene;
import com.metaplains.world.scenes.SceneHome;

public class House extends EntityTangible {

	public House(Scene scene, float x, float y, float z) {
		super(scene, x, y, z);
		this.addChild(new PortalScene(scene, 2, 1.5F, 3.5F, new SceneHome(), new BoxShape(new Vector3f(1.0F, 1.5F, 0.5F))));
	}

	@Override
	public ClippingSphere getClippingSphere() {
		return null;
	}
	
	@Override
	public void render(GameScreen screen, boolean shadowPass) {
		//TODO: Standardize.
		//TODO: Use VBOs exclusively?
		screen.renderModel(Model.HOUSE);
	}

	@Override
	public float getMass() {
		return 0.0F;
	}

	@Override
	public CollisionShape getCollisionShape() {
		Model model = Model.HOUSE;
		int totalVerts = model.vertices.size();
		int totalTris = 0;
		for (Mesh mesh : model.meshes)
			totalTris += mesh.faces.size();
		int vertStride = 3 * 4;
		int indexStride = 3 * 4;

		ByteBuffer vertices = ByteBuffer.allocateDirect(totalVerts * 3 * 4).order(ByteOrder.nativeOrder());
		ByteBuffer indices = ByteBuffer.allocateDirect(totalTris * 3 * 4).order(ByteOrder.nativeOrder());
		vertices.clear();
		indices.clear();

		int i = 0;
		for (Vec3F vert : model.vertices) {
			vertices.putFloat((i*3 + 0)*4, vert.x);
			vertices.putFloat((i*3 + 1)*4, vert.y);
			vertices.putFloat((i*3 + 2)*4, vert.z);
			i++;
		}
		
		for (Mesh mesh : model.meshes)
			for (TexturedPolygon face : mesh.faces)
				for (Integer id : face.vertexIDs)
					indices.putInt(id-1);
		indices.flip();
		
		TriangleIndexVertexArray indexVertexArrays = new TriangleIndexVertexArray(totalTris, indices, indexStride, totalVerts, vertices, vertStride);
		BvhTriangleMeshShape houseShape = new BvhTriangleMeshShape(indexVertexArrays, true);
		return houseShape;
	}

	@Override
	public Vec3F getInertiaTensor() {
		return new Vec3F(0.0F, 0.0F, 0.0F);
	}

	@Override
	public boolean isKinematic() {
		return false;
	}
}
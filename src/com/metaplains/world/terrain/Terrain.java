package com.metaplains.world.terrain;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

import java.nio.*;
import java.util.*;
import org.lwjgl.BufferUtils;
import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.dynamics.RigidBody;
import com.metaplains.core.GameClient;
import com.metaplains.core.GameScreen;
import com.metaplains.entities.EntityTangible;
import com.metaplains.entities.inert.buildings.House;
import com.metaplains.entities.inert.decor.Tree;
import com.metaplains.gfx.GL;
import com.metaplains.gfx.Shader;
import com.metaplains.gfx.Texture;
import com.metaplains.gfx.VBOHelper;
import com.metaplains.gfx.cam.ClippingSphere;
import com.metaplains.gfx.cam.ICameraTarget;
import com.metaplains.utils.*;
import com.metaplains.world.scenes.Scene;

public class Terrain extends EntityTangible {

	public int seed;
	private Random prng;
	public HeightMap heightMap = new HeightMap(257, 257);
	public Vec3F[][] normalMap = new Vec3F[257][257];
	private QuadtreeNode<AABB> boundTree = new QuadtreeNode<AABB>(new AABB(0, 0, 0, 256, 256));
	private final int tileSize = 1; //TODO: Changing this breaks everything.
	private int terrainID;
	
	public Terrain(Scene scene, float x, float y, float z) {
		super(scene, x, y, z);
	}
	
	public void generate(int seed) { //TODO: Think about entity generation (e.g. trees).
		float maxVal = Float.MIN_VALUE, minVal = Float.MAX_VALUE;
		int halfSize = 128;
		for (int z = 0; z<257; z++) {
			for (int x = 0; x<257; x++) {
				double fade = (FastMath.abs(x-halfSize)*FastMath.abs(x-halfSize)+FastMath.abs(z-halfSize)*FastMath.abs(z-halfSize))/16384.0F;
				fade = fade>1.0F?0.0F:1.0F-fade;
				heightMap.heights[x][z] = (float) (((Noise.perlin2D(seed, x, z)+1.0F)/2.0F) * fade * 64);//(1.0F-((abs(x-halfSize)/256.0F)+(abs(y-halfSize)/256.0F)));
				if (heightMap.heights[x][z]>maxVal)
					maxVal = heightMap.heights[x][z];
				if (heightMap.heights[x][z]<minVal)
					minVal = heightMap.heights[x][z];
			}
		}
		//System.out.println("Max = "+maxVal+", Min = "+minVal);
	}
	
	@SuppressWarnings("unused")
	public Terrain load(String mapName) {
		//TODO: Reorder and enable saving.
		
		if (true || !GameClient.game.saveManager.loadTerrain(mapName, this)) {
			seed = 0; //TODO: Randomize seeds.
			prng = new Random(seed);
			generate(seed);
			//updateTerrainColors();
			spawnTrees(seed);
			spawnHouse(seed);
			calculateNormals();
			subdivideAABB(boundTree);
			//GameClient.game.saveManager.saveTerrain(worldName, this);
		}
		//buildTerrainDisplayList();
		buildTerrainVBO();
		return this;
	}

	private void buildTerrainVBO() {
		terrainID = VBOHelper.createVBOID();
		FloatBuffer buffer = BufferUtils.createFloatBuffer((3+3/*+4*/+2)*4*256*256);
		int tileSize = 1;
		Vec3F normal;
		for (int z = 0, zp = 0; z < heightMap.height-1; z++, zp+=tileSize) {
			for (int x = 0, xp = 0; x < heightMap.width-1; x++, xp+=tileSize) {
				buffer.put(xp).put(heightMap.getHeight(x, z+1)).put(zp+tileSize);
				normal = normalMap[x][z+1];
				buffer.put(normal.x).put(normal.y).put(normal.z);
				//buffer.put(1.0F).put(1.0F).put(1.0F).put(1.0F);
				buffer.put(0.0F).put(1.0F);

				buffer.put(xp+tileSize).put(heightMap.getHeight(x+1, z+1)).put(zp+tileSize);
				normal = normalMap[x+1][z+1];
				buffer.put(normal.x).put(normal.y).put(normal.z);
				//buffer.put(1.0F).put(1.0F).put(1.0F).put(1.0F);
				buffer.put(1.0F).put(1.0F);

				buffer.put(xp+tileSize).put(heightMap.getHeight(x+1, z)).put(zp);
				normal = normalMap[x+1][z];
				buffer.put(normal.x).put(normal.y).put(normal.z);
				//buffer.put(1.0F).put(1.0F).put(1.0F).put(1.0F);
				buffer.put(1.0F).put(0.0F);

				buffer.put(xp).put(heightMap.getHeight(x, z)).put(zp);
				normal = normalMap[x][z];
				buffer.put(normal.x).put(normal.y).put(normal.z);
				//buffer.put(1.0F).put(1.0F).put(1.0F).put(1.0F);
				buffer.put(0.0F).put(0.0F);
			}
		}
		buffer.flip();
		VBOHelper.bufferData(terrainID, buffer, VBOHelper.STATIC_DRAW);
	}

	private void buildTerrainDisplayList() {
		terrainID = glGenLists(1);
		glNewList(terrainID, GL_COMPILE);
		//glEnable(GL_TEXTURE_2D);
		
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, Texture.TERRAIN);
		
		glActiveTexture(GL_TEXTURE1);
		glBindTexture(GL_TEXTURE_2D, Texture.NOSIGNAL);
		
		glUniform1i(glGetUniformLocation(Shader.TERRAIN, "texture0"), 0);
		glUniform1i(glGetUniformLocation(Shader.TERRAIN, "texture1"), 1);
		
		float[] color;
		Vec3F normal;
		glBegin(GL_QUADS);
		for (int z = 0, zp = 0; z < heightMap.height-1; z++, zp+=tileSize) {
			for (int x = 0, xp = 0; x < heightMap.width-1; x++, xp+=tileSize) {
				//Verts in the same position have to be separate due to differing texture coords.
				//color = colorMap.getColor(x, z+1);
				//glColor4f(color[0], color[1], color[2], color[3]);
				normal = normalMap[x][z+1];
				glNormal3f(normal.x, normal.y, normal.z);
				glTexCoord2f(0, 1);
				glVertex3f(xp, heightMap.getHeight(x, z+1), zp+tileSize);
				//color = colorMap.getColor(x+1, z+1);
				//glColor4f(color[0], color[1], color[2], color[3]);
				normal = normalMap[x+1][z+1];
				glNormal3f(normal.x, normal.y, normal.z);
				glTexCoord2f(1, 1);
				glVertex3f(xp+tileSize, heightMap.getHeight(x+1, z+1), zp+tileSize);
				//color = colorMap.getColor(x+1, z);
				//glColor4f(color[0], color[1], color[2], color[3]);
				normal = normalMap[x+1][z];
				glNormal3f(normal.x, normal.y, normal.z);
				glTexCoord2f(1, 0);
				glVertex3f(xp+tileSize, heightMap.getHeight(x+1, z), zp);
				//color = colorMap.getColor(x, z);
				//glColor4f(color[0], color[1], color[2], color[3]);
				normal = normalMap[x][z];
				glNormal3f(normal.x, normal.y, normal.z);
				glTexCoord2f(0, 0);
				glVertex3f(xp, heightMap.getHeight(x, z), zp);
			}
		}
		glEnd();
		glActiveTexture(GL_TEXTURE0);
		//glDisable(GL_TEXTURE_2D);
		glEndList();		
	}
	
	private void renderElements(IntBuffer indices) {
		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_NORMAL_ARRAY);
		//glEnableClientState(GL_COLOR_ARRAY);
		glEnableClientState(GL_TEXTURE_COORD_ARRAY);
		
		glBindBuffer(GL_ARRAY_BUFFER, terrainID);
		
		int stride = 32; //(v3,n3,c4,t2)*4bytes
		glVertexPointer(3, GL_FLOAT, stride, 0);
		glNormalPointer(GL_FLOAT, stride, 12);
		//glColorPointer(4, GL_FLOAT, stride, 24);
		//glTexCoordPointer(2, GL_FLOAT, stride, 32);
		glTexCoordPointer(2, GL_FLOAT, stride, 24);
		
		glDrawElements(GL_QUADS, indices);
		
		glDisableClientState(GL_TEXTURE_COORD_ARRAY);
		//glDisableClientState(GL_COLOR_ARRAY);
		glDisableClientState(GL_NORMAL_ARRAY);
		glDisableClientState(GL_VERTEX_ARRAY);
	}
	
	private void renderImmediate() {
		glBegin(GL_QUADS);
		renderInBounds(boundTree);
		glEnd();
	}

	private void renderInBounds(QuadtreeNode<AABB> root) {
		if (scene.camera.frustum.isAABBInFrustum(root.object)) {
			if ((root.object.maxZ - root.object.minZ <= 1) && (root.object.maxX - root.object.minX <= 1)) {
				renderTile((int) root.object.minX, (int) root.object.minZ);
			} else {
				renderInBounds(root.c00);
				renderInBounds(root.c01);
				renderInBounds(root.c10);
				renderInBounds(root.c11);
			}
		}
	}
	
	private void renderTile(int x, int z) {
		//float[] color;
		Vec3F normal;
		//color = colorMap.getColor(x, z+1);
		//glColor4f(color[0], color[1], color[2], color[3]);
		normal = normalMap[x][z+1];
		glNormal3f(normal.x, normal.y, normal.z);
		glTexCoord2f(0, 1);
		glVertex3f(x, heightMap.getHeight(x, z+1), z+1);
		//color = colorMap.getColor(x+1, z+1);
		//glColor4f(color[0], color[1], color[2], color[3]);
		normal = normalMap[x+1][z+1];
		glNormal3f(normal.x, normal.y, normal.z);
		glTexCoord2f(1, 1);
		glVertex3f(x+1, heightMap.getHeight(x+1, z+1), z+1);
		//color = colorMap.getColor(x+1, z);
		//glColor4f(color[0], color[1], color[2], color[3]);
		normal = normalMap[x+1][z];
		glNormal3f(normal.x, normal.y, normal.z);
		glTexCoord2f(1, 0);
		glVertex3f(x+1, heightMap.getHeight(x+1, z), z);
		//color = colorMap.getColor(x, z);
		//glColor4f(color[0], color[1], color[2], color[3]);
		normal = normalMap[x][z];
		glNormal3f(normal.x, normal.y, normal.z);
		glTexCoord2f(0, 0);
		glVertex3f(x, heightMap.getHeight(x, z), z);
	}

	private void calculateNormals() {
		for (int z = 0; z < heightMap.height; z++) {
			for (int x = 0; x < heightMap.width; x++) {
				Vec3F vecCen = new Vec3F(x, heightMap.getHeight(x, z), z);
				normalMap[x][z] = Geom.normal(new Vec3F(x+1, heightMap.getHeight(x+1, z), z), vecCen, new Vec3F(x, heightMap.getHeight(x, z-1), z-1))
									.translate(Geom.normal(new Vec3F(x-1, heightMap.getHeight(x-1, z), z), vecCen, new Vec3F(x, heightMap.getHeight(x, z+1), z+1)))
									.normalise(); 
			}
		} 
	}
	
	private void subdivideAABB(QuadtreeNode<AABB> root) {
		if ((root.object.maxZ - root.object.minZ <= 1) && (root.object.maxX - root.object.minX <= 1)) {
			root.object.setPosition(root.object.minX, Math.min(
					Math.min(heightMap.getHeight((int)root.object.minX, (int)root.object.minZ),
							heightMap.getHeight((int)root.object.minX, (int)root.object.maxZ)),
					Math.min(heightMap.getHeight((int)root.object.maxX, (int)root.object.minZ),
							heightMap.getHeight((int)root.object.maxX, (int)root.object.maxZ))), root.object.minZ);
			root.object.setSize(root.object.width, Math.max(
					Math.max(heightMap.getHeight((int)root.object.minX, (int)root.object.minZ),
							heightMap.getHeight((int)root.object.minX, (int)root.object.maxZ)),
					Math.max(heightMap.getHeight((int)root.object.maxX, (int)root.object.minZ),
							heightMap.getHeight((int)root.object.maxX, (int)root.object.maxZ))) - root.object.minY);
			return;
		}
		root.subdivide();
		float halfWidth = root.object.width/2.0F;
		root.c00.object = new AABB(root.object.minX, root.object.minY, root.object.minZ, halfWidth, root.object.height);
		root.c01.object = new AABB(root.object.minX, root.object.minY, root.object.minZ + halfWidth, halfWidth, root.object.height);
		root.c10.object = new AABB(root.object.minX + halfWidth, root.object.minY, root.object.minZ, halfWidth, root.object.height);
		root.c11.object = new AABB(root.object.minX + halfWidth, root.object.minY, root.object.minZ + halfWidth, halfWidth, root.object.height);
		subdivideAABB(root.c00);
		subdivideAABB(root.c01);
		subdivideAABB(root.c10);
		subdivideAABB(root.c11);
		root.object.setPosition(root.object.minX, Math.min(Math.min(root.c00.object.minY, root.c01.object.minY), Math.min(root.c10.object.minY, root.c11.object.minY)), root.object.minZ);
		root.object.setSize(root.object.width, Math.max(Math.max(root.c00.object.maxY, root.c01.object.maxY), Math.max(root.c10.object.maxY, root.c11.object.maxY)) - root.object.minY);
	}
	
	private void spawnTrees(int seed) {
		for (int z = 0; z<257; z++) {
			for (int x = 0; x<257; x++) {
				float height = heightMap.getHeight(x, z);
				if (height > 0.4F*64.0F && height < 0.5F*64.0F && prng.nextFloat() > 0.995F) //TODO: Standardize height scaling.
					scene.entityManager.spawnEntity(new Tree(scene, x, height, z));
			}
		}
	}
	
	private void spawnHouse(int seed) {
		int cenX = prng.nextInt(257-64)+32, cenZ = prng.nextInt(257-64)+32;
		//TODO: Get dims from entity.
		float meanHeight = 0;
		for (int z = cenZ-5; z <= cenZ+5; z++)
			for (int x = cenX-5; x <= cenX+5; x++)
				meanHeight += heightMap.getHeight(x, z);
		meanHeight /= 11*11;
		for (int z = cenZ-5; z <= cenZ+5; z++)
			for (int x = cenX-5; x <= cenX+5; x++)
				heightMap.heights[x][z] = meanHeight;//(x==cenX-5 || x==cenX+5 || z==cenZ-5 || z==cenZ+5)?meanHeight-(0.001F*64.0F):meanHeight;
		scene.entityManager.spawnEntity(new House(scene, cenX, meanHeight, cenZ));
	}

	@Override
	public ClippingSphere getClippingSphere() {
		return null;
	}
	
	@Override
	public void render(GameScreen screen, boolean shadowPass) {
		if (shadowPass)
			return;
		GL.useProgram(Shader.TERRAIN);
		//TODO: Mult parent transforms!!!
		glUniformMatrix4(glGetUniformLocation(Shader.TERRAIN, "transMatrix"), false, transform.matrixBuffer);
		glUniform1i(glGetUniformLocation(Shader.TERRAIN, "isDaytime"), glIsEnabled(GL_LIGHT0)?1:0);
		//glEnable(GL_TEXTURE_2D);
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, Texture.TEMPSAND);
		
		glActiveTexture(GL_TEXTURE1);
		glBindTexture(GL_TEXTURE_2D, Texture.GRASSDARK);
		
		glUniform1i(glGetUniformLocation(Shader.TERRAIN, "texture0"), 0);
		glUniform1i(glGetUniformLocation(Shader.TERRAIN, "texture1"), 1);
		
		glUniform1f(glGetUniformLocation(Shader.TERRAIN, "tex0Scale"), 2.0F);
		glUniform1f(glGetUniformLocation(Shader.TERRAIN, "tex1Scale"), 4.0F);
		//renderImmediate();
		//glCallList(terrainID);
		//VBOHelper.render(terrainID, 262144);
		glEnable(GL_BLEND);
		renderElements(buildElementBuffer());
		glDisable(GL_BLEND);
		//glDisable(GL_TEXTURE_2D);
		glActiveTexture(GL_TEXTURE0);
		GL.useProgram(Shader.STD);
		//renderTerrainQuadtree(screen, boundTree);
	}

	IntBuffer elementBuffer = BufferUtils.createIntBuffer(4*heightMap.width*heightMap.height);
	private IntBuffer buildElementBuffer() {
		elementBuffer.clear();
		populateElementBuffer(boundTree);
		elementBuffer.flip();
		return elementBuffer;
	}
	
	private void populateElementBuffer(QuadtreeNode<AABB> root) {
		if (scene.camera.frustum.isAABBInFrustum(root.object)) {
			if ((root.object.maxZ - root.object.minZ <= 1) && (root.object.maxX - root.object.minX <= 1)) {
				if(Geom.dist(ICameraTarget.position, new Vec3F(root.object.minX+root.object.width/2, ICameraTarget.position.y, root.object.minZ+root.object.width/2))<100) {
					//TODO: NB: Assumes tileSize of 1.
					int start = ((int)root.object.minX + (int)root.object.minZ*(heightMap.width-1))*4;
					elementBuffer.put(start).put(start+1).put(start+2).put(start+3);
				}
			} else {
				populateElementBuffer(root.c00);
				populateElementBuffer(root.c01);
				populateElementBuffer(root.c10);
				populateElementBuffer(root.c11);
			}
		}
	}

	private void renderTerrainQuadtree(GameScreen screen, QuadtreeNode<AABB> node) {
		screen.renderAABBFixed(node.object);
		if (node.hasChildren) {
			renderTerrainQuadtree(screen, node.c00);
			renderTerrainQuadtree(screen, node.c01);
			renderTerrainQuadtree(screen, node.c10);
			renderTerrainQuadtree(screen, node.c11);
		}
	}

	@Override
	public float getMass() {
		return 0.0F;
	}

	@Override
	public CollisionShape getCollisionShape() {
		int totalVerts = 257*257;
		int totalTris = 256*256*2;
		int vertStride = 3 * 4;
		int indexStride = 3 * 4;

		ByteBuffer vertices = ByteBuffer.allocateDirect(totalVerts * 3 * 4).order(ByteOrder.nativeOrder());
		ByteBuffer indices = ByteBuffer.allocateDirect(totalTris * 3 * 4).order(ByteOrder.nativeOrder());
		vertices.clear();
		indices.clear();

		for (int z = 0; z < heightMap.height; z++) {
			for (int x = 0; x < heightMap.width; x++) {
				int index = (z*257)+x;
				vertices.putFloat((index*3 + 0) * 4, x);
				vertices.putFloat((index*3 + 1) * 4, heightMap.getHeight(x, z));
				vertices.putFloat((index*3 + 2) * 4, z);
			}
		}
		
		for (int z = 0; z < heightMap.height-1; z++) {
			for (int x = 0; x < heightMap.width-1; x++) {
				indices.putInt((z*257)+x);
				indices.putInt((z*257)+x+1);
				indices.putInt(((z+1)*257)+x+1);

				indices.putInt((z*257)+x);
				indices.putInt(((z+1)*257)+x+1);
				indices.putInt(((z+1)*257)+x);
			}
		}
		indices.flip();
		
		TriangleIndexVertexArray indexVertexArrays = new TriangleIndexVertexArray(totalTris, indices, indexStride, totalVerts, vertices, vertStride);
		BvhTriangleMeshShape terrainShape = new BvhTriangleMeshShape(indexVertexArrays, true);
		return terrainShape;
	}

	@Override
	public Vec3F getInertiaTensor() {
		return new Vec3F(0.0F, 0.0F, 0.0F);
	}

	@Override
	public boolean isKinematic() {
		return true;
	}
	
	public void destroy() {
		super.destroy();
		VBOHelper.destroyVBO(terrainID);
	}
}
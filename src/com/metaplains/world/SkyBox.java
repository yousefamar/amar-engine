package com.metaplains.world;

import static com.metaplains.gfx.GL.*;
import static org.lwjgl.opengl.GL11.*;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

import com.metaplains.core.GameScreen;
import com.metaplains.entities.Entity;
import com.metaplains.gfx.GL;
import com.metaplains.gfx.Shader;
import com.metaplains.gfx.Texture;
import com.metaplains.utils.Mat4F;
import com.metaplains.utils.Vec3F;
import com.metaplains.world.scenes.Scene;

public class SkyBox extends Entity {

	private Scene scene;
	private Vec3F sunPos;
	private int skyBoxListID;
	
	public SkyBox(Scene scene) {
		super(scene);
		this.scene = scene;
		this.sunPos = new Vec3F(0, 1, 0);
		loadSkyboxList();
		setAmbientLight(0, 0, 0, 1);
		setDiffuseLight(1, 1, 1, 0);
		setSpecularLight(1, 1, 1, 1);
	}
	
	public void setSunAngle(float angle) {
		sunPos.set(0, 1, 0);
		sunPos.rotateZ(angle);
		setDiffuseLight(1, 0, 0, 1);
		if (sunPos.y < 0.0F)
			glDisable(GL_LIGHT0);
		else
			glEnable(GL_LIGHT0);
	}
	
	private void setAmbientLight(float r, float g, float b, float a) {
		FloatBuffer ambient = BufferUtils.createFloatBuffer(4);
		ambient.put(0).put(0).put(0).put(1).flip();
		glLight(GL_LIGHT0, GL_AMBIENT, ambient);
	}
	
	private void setDiffuseLight(float r, float g, float b, float a) {
		FloatBuffer diffuse = BufferUtils.createFloatBuffer(4);
		diffuse.put(1).put(1).put(1).put(0).flip();
		glLight(GL_LIGHT0, GL_DIFFUSE, diffuse);
	}
	
	private void setSpecularLight(float r, float g, float b, float a) {
		FloatBuffer specular = BufferUtils.createFloatBuffer(4);
		specular.put(1).put(1).put(1).put(1).flip();
		glLight(GL_LIGHT0, GL_SPECULAR, specular);

		//FloatBuffer specularMat = BufferUtils.createFloatBuffer(4);
		//specularMat.put(1.0f).put(1.0f).put(1.0f).put(1.0f).flip();
		//glMaterial(GL_FRONT, GL_SPECULAR, specularMat);
		//glMaterialf(GL_FRONT, GL_SHININESS, 50.0f);
	}

	private void loadSkyboxList() {
		this.skyBoxListID = glGenLists(1);
		glNewList(skyBoxListID, GL_COMPILE);
		float f13 = 1F/3F, f23 = 2F/3F;
		glBegin(GL_QUADS);
		//Front
		glTexCoord2f(0.25F, f23);
		glVertex3f(0.5f, -0.5f, -0.5f);
		glTexCoord2f(0.5F, f23);
		glVertex3f(-0.5f, -0.5f, -0.5f);
		glTexCoord2f(0.5F, f13);
		glVertex3f(-0.5f, 0.5f, -0.5f);
		glTexCoord2f(0.25F, f13);
		glVertex3f(0.5f, 0.5f, -0.5f);
	    //Left
		glTexCoord2f(0, f23);
		glVertex3f(0.5f, -0.5f, 0.5f);
		glTexCoord2f(0.25F, f23);
		glVertex3f(0.5f, -0.5f, -0.5f);
		glTexCoord2f(0.25F, f13);
		glVertex3f(0.5f, 0.5f, -0.5f);
		glTexCoord2f(0, f13);
		glVertex3f(0.5f, 0.5f, 0.5f);
		//Back
		glTexCoord2f(0.75F, f23);
		glVertex3f(-0.5f, -0.5f, 0.5f);
		glTexCoord2f(1, f23);
		glVertex3f(0.5f, -0.5f, 0.5f);
		glTexCoord2f(1, f13);
		glVertex3f(0.5f, 0.5f, 0.5f);
		glTexCoord2f(0.75F, f13);
		glVertex3f(-0.5f, 0.5f, 0.5f);
		//Right
		glTexCoord2f(0.5F, f23);
		glVertex3f(-0.5f, -0.5f, -0.5f);
		glTexCoord2f(0.75F, f23);
		glVertex3f(-0.5f, -0.5f, 0.5f);
		glTexCoord2f(0.75F, f13);
		glVertex3f(-0.5f, 0.5f, 0.5f);
		glTexCoord2f(0.5F, f13);
		glVertex3f(-0.5f, 0.5f, -0.5f);
		//Top
		glTexCoord2f(0.5F, f13);
		glVertex3f(-0.5f, 0.5f, -0.5f);
		glTexCoord2f(0.5F, 0);
		glVertex3f(-0.5f, 0.5f, 0.5f);
		glTexCoord2f(0.25F, 0);
		glVertex3f(0.5f, 0.5f, 0.5f);
		glTexCoord2f(0.25F, f13);
		glVertex3f(0.5f, 0.5f, -0.5f);
		//Bottom
		glTexCoord2f(0.5F, f23);
		glVertex3f(-0.5f, -0.5f, -0.5f);
		glTexCoord2f(0.5F, 1);
		glVertex3f(-0.5f, -0.5f, 0.5f);
		glTexCoord2f(0.25F, 1);
		glVertex3f(0.5f, -0.5f, 0.5f);
		glTexCoord2f(0.25F, f23);
		glVertex3f(0.5f, -0.5f, -0.5f);
		glEnd();
		glEndList();
	}
	
	@Override
	public void render(GameScreen screen, boolean shadowPass) {
		if (shadowPass)
			return;
		pushMatrix();
		//TODO: Think about object relationships here.

		translate(scene.camera.eyeVec.x, scene.camera.eyeVec.y, scene.camera.eyeVec.z);
		
		glPushAttrib(GL_ENABLE_BIT);
		glEnable(GL_TEXTURE_2D);
		glDisable(GL_DEPTH_TEST);
		glDisable(GL_LIGHTING);
		glDisable(GL_BLEND);
		glDisable(GL_CULL_FACE);

		glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		//TODO: Create blend shader.
	    glBindTexture(GL_TEXTURE_2D, Texture.SKYBOX);
	    useProgram(Shader.STD);
		glCallList(skyBoxListID);

		glPopAttrib();
		popMatrix();
		
		if (sunPos.y >= 0.0F) {
			FloatBuffer position = BufferUtils.createFloatBuffer(4);
			position.put(sunPos.x).put(sunPos.y).put(sunPos.z).put(0).flip();
			glLight(GL_LIGHT0, GL_POSITION, position);
		}
	}
}

package com.metaplains.entities.lights;

import static com.metaplains.gfx.GL.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.util.glu.GLU.*;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTFramebufferObject;

import com.metaplains.core.GameClient;
import com.metaplains.core.GameScreen;
import com.metaplains.gfx.FBOHelper;
import com.metaplains.utils.Mat4F;
import com.metaplains.utils.Transform;
import com.metaplains.utils.Vec3F;
import com.metaplains.world.scenes.Scene;

public class LightPoint extends Light {

	//TODO: Consider making this a parameter.
	public final int shadowMapRes = 600;

	private int fboID;
	public int depthTexID;
	private Mat4F proj;
	private Mat4F view;
	private Mat4F modl;
	private float[] bias = new float[]{	0.5F, 0.0F, 0.0F, 0.0F, 
										0.0F, 0.5F, 0.0F, 0.0F,
										0.0F, 0.0F, 0.5F, 0.0F,
										0.5F, 0.5F, 0.5F, 1.0F};
	
	public LightPoint(Scene scene) {
		super(scene);
		fboID = FBOHelper.createFBOID();
		//TODO: Lol.
		depthTexID = FBOHelper.genShadowFBO(fboID, shadowMapRes);
	}
	
	private void setTextureMatrix()	{
		// Grab modelview and transformation matrices
		//TODO: Consider storing the modelViewProjection matrix somewhere instead of calculating it every time.
		proj = getMatrix(PROJECTION);
		view = getMatrix(VIEW);
		modl = getMatrix(MODEL);

		glMatrixMode(GL_TEXTURE);
		glActiveTexture(GL_TEXTURE7);
		glLoadMatrix(new Mat4F(bias).multiply(proj).multiply(view).multiply(modl).toFloatBuffer());
		
		glMatrixMode(GL_MODELVIEW);
	}
	
	public void updateDepthTextures(GameScreen screen) {
		//First step: Render from the light POV to a FBO, story depth values only
		EXTFramebufferObject.glBindFramebufferEXT(GL_FRAMEBUFFER, fboID);

		// In the case we render the shadowmap to a higher resolution, the viewport must be modified accordingly.
		glPushAttrib(GL_VIEWPORT_BIT);
		glViewport(0, 0, shadowMapRes, shadowMapRes);
		
		// Clear previous frame values
		glClear(GL_DEPTH_BUFFER_BIT);

		//Disable color rendering, we only want to write to the Z-Buffer
		glColorMask(false, false, false, false); 

		
		
		
		setMatrixMode(PROJECTION);
		pushMatrix();
		setIdentity();
//		ortho(0, shadowMapRes, 0, shadowMapRes, 0.1F, 1000.0F);
//		translate(shadowMapRes/2.0F, shadowMapRes/2.0F, 0);
//		scale(10, 10, 10);
		perspective(90.0F, 1, 0.1F, 1000.0F);
		setMatrixMode(VIEW);
		pushMatrix();
		Transform worldTrans = getWorldTransform();
		lookAt(worldTrans.position, worldTrans.position.clone().translate(0, -1, 0), Vec3F.UNIT_X);
		setMatrixMode(MODEL);
		pushMatrix();
		glLoadIdentity();
		//glTranslatef(screen.screenDims.width/2.0F, screen.screenDims.height/2.0F, 0);
		//glScalef(40, 40, 1);
		//System.out.println(transMatrix.position);
		//GameClient.game.currentWorld.camera.onRender(false);

		// Culling switching, rendering only backface, this is done to avoid self-shadowing
		glCullFace(GL_FRONT);
		GameClient.game.currentScene.entityManager.render(screen, true);

		//Save modelview/projection matrice into texture7, also add a biais
		setTextureMatrix();
		
		
		

		// Now rendering from the camera POV, using the FBO to generate shadows
		EXTFramebufferObject.glBindFramebufferEXT(GL_FRAMEBUFFER, 0);

		glPopAttrib();
		glViewport(0, 0, screen.screenDims.width, screen.screenDims.height);

		//Enabling color write (previously disabled for light POV z-buffer rendering)
		glColorMask(true, true, true, true); 

		// Clear previous frame values
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		popMatrix();
		setMatrixMode(VIEW);
		popMatrix();
		setMatrixMode(PROJECTION);
		popMatrix();
//		ortho(0, shadowMapRes, 0, shadowMapRes, 0.1F, 1000.0F);
//		translate(shadowMapRes/2.0F, shadowMapRes/2.0F, 0);
//		scale(10, 10, 10);
		setMatrixMode(MODEL);
		//glOrtho(0, (float) screen.screenDims.width, 0, (float) screen.screenDims.height, 0.1F, 1000.0F);
//		gluPerspective(90F, ((float) screen.screenDims.width / (float) screen.screenDims.height), 0.1F, 1000.0F);
//		glMatrixMode(GL_MODELVIEW);
//		glLoadIdentity();
//		GameClient.game.currentScene.camera.onRender(false);

		glActiveTexture(GL_TEXTURE0);
		glCullFace(GL_BACK);

	}
}
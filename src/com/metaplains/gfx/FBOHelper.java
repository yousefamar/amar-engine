package com.metaplains.gfx;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL30.*;
import java.nio.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

public class FBOHelper {

	public static int createFBOID() {
		//System.out.println(glGetString(GL_VERSION));
		if (GLContext.getCapabilities().GL_EXT_framebuffer_object) {
			/*GL11.glEnable(GL11.GL_TEXTURE_2D);
			
			GL11.glEnable(GL11.GL_BLEND);	
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			
			// Set up orthographic projection for 2D
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glOrtho(0, 800, 0, 600, -100, 100);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);*/
			
			// Create a FBO
			// Generate the fbo
			return EXTFramebufferObject.glGenFramebuffersEXT();
			
			/*IntBuffer buffer = BufferUtils.createIntBuffer(1);
			EXTFramebufferObject.glGenFramebuffersEXT(buffer);
			return buffer.get(0);*/
		}
		return 0;
	}

	/*public static int genShadowFBO(int fboID, int resolution) {
		// Try to use a texture depth component
		IntBuffer intBuf = BufferUtils.createIntBuffer(1);
		glGenTextures(intBuf);
		intBuf.rewind();
		int texID = intBuf.get(0);
		glBindTexture(GL_TEXTURE_CUBE_MAP, texID);	// Make it a cubemap
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_BASE_LEVEL, 0);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAX_LEVEL, 0);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
//		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_DEPTH_TEXTURE_MODE, GL_LUMINANCE);
//		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_R_TO_TEXTURE);
//		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_COMPARE_FUNC, GL_LEQUAL);
		for (int i = 0; i < 6; i++)
			glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X+i, 0, GL_DEPTH_COMPONENT, resolution, resolution, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (FloatBuffer)null);

		EXTFramebufferObject.glBindFramebufferEXT(GL_FRAMEBUFFER, fboID);
		glDrawBuffer(GL_NONE);
		glReadBuffer(GL_NONE);
		//for (int i = 0; i < 6; i++)
			EXTFramebufferObject.glFramebufferTexture2DEXT(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_CUBE_MAP_POSITIVE_X, texID, 0);
		
		int fbStatus = EXTFramebufferObject.glCheckFramebufferStatusEXT(GL_FRAMEBUFFER); 
		if(fbStatus != GL_FRAMEBUFFER_COMPLETE)
			System.err.println("Error: Framebuffer status incomplete ("+fbStatus+"); cannot use FBO.");

		glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
		EXTFramebufferObject.glBindFramebufferEXT(GL_FRAMEBUFFER, 0);
		return texID;
	}*/

	public static int genShadowFBO(int fboID, int resolution) {
		// Try to use a texture depth component
		IntBuffer intBuf = BufferUtils.createIntBuffer(1);
		glGenTextures(intBuf);
		intBuf.rewind();
		int texID = intBuf.get(0);
		glBindTexture(GL_TEXTURE_2D, texID);

		// GL_LINEAR does not make sense for depth texture. However, next tutorial shows usage of GL_LINEAR and PCF
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

		// Remove artifact on the edges of the shadowmap
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);//GL_CLAMP_TO_BORDER);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);//GL_CLAMP_TO_BORDER);

		// This is to allow usage of shadow2DProj function in the shader
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_R_TO_TEXTURE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_FUNC, GL_LEQUAL);
		glTexParameteri(GL_TEXTURE_2D, GL_DEPTH_TEXTURE_MODE, GL_INTENSITY); 

		// No need to force GL_DEPTH_COMPONENT24, drivers usually give you the max precision if available
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, resolution, resolution, 0, GL_DEPTH_COMPONENT, GL_UNSIGNED_BYTE, (ByteBuffer)null);
		glBindTexture(GL_TEXTURE_2D, 0);

		// create a framebuffer object
		EXTFramebufferObject.glBindFramebufferEXT(GL_FRAMEBUFFER, fboID);

		// Instruct openGL that we won't bind a color texture with the currently bound FBO
		glDrawBuffer(GL_NONE);
		glReadBuffer(GL_NONE);

		// attach the texture to FBO depth attachment point
		EXTFramebufferObject.glFramebufferTexture2DEXT(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, texID, 0);

		// check FBO status

		int fbStatus = EXTFramebufferObject.glCheckFramebufferStatusEXT(GL_FRAMEBUFFER); 
		if(fbStatus != GL_FRAMEBUFFER_COMPLETE)
			System.err.println("Error: Framebuffer status incomplete ("+fbStatus+"); cannot use FBO.");

		// switch back to window-system-provided framebuffer
		EXTFramebufferObject.glBindFramebufferEXT(GL_FRAMEBUFFER, 0);
		return texID;
	}
}
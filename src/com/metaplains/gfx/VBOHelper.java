package com.metaplains.gfx;

import static org.lwjgl.opengl.GL15.*;
import java.nio.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

public class VBOHelper {

	public static final int STATIC_DRAW = GL_STATIC_DRAW;
	public static final int DYNAMIC_DRAW = GL_DYNAMIC_DRAW;
	public static final int STREAM_DRAW = GL_STREAM_DRAW;
	
	public static int createVBOID() {
		if (GLContext.getCapabilities().GL_ARB_vertex_buffer_object) {
			IntBuffer buffer = BufferUtils.createIntBuffer(1);
			glGenBuffers(buffer);
			return buffer.get(0);
		}
		return 0;
	}

	public static void bufferElementData(int vboID, IntBuffer buffer, int usage) {
		if (GLContext.getCapabilities().GL_ARB_vertex_buffer_object) {
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboID);
			glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, usage);
		}
	}
	
	public static void bufferData(int vboID, FloatBuffer buffer, int usage) {
		if (GLContext.getCapabilities().GL_ARB_vertex_buffer_object) {
			glBindBuffer(GL_ARRAY_BUFFER, vboID);
			glBufferData(GL_ARRAY_BUFFER, buffer, usage);
		}
	}

	public static void destroyVBO(int vboID) {
		if (GLContext.getCapabilities().GL_ARB_vertex_buffer_object) {
			glDeleteBuffers(vboID);
		}
	}
	
	/*public static void render(int idsID, int vboID, int vertCount) {
		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_NORMAL_ARRAY);
		//glEnableClientState(GL_COLOR_ARRAY);
		glEnableClientState(GL_TEXTURE_COORD_ARRAY);
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idsID);
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		
		int stride = 32; //(v3,n3,c4,t2)*4bytes
		glVertexPointer(3, GL_FLOAT, stride, 0);
		glNormalPointer(GL_FLOAT, stride, 12);
		//glColorPointer(4, GL_FLOAT, stride, 24);
		//glTexCoordPointer(2, GL_FLOAT, stride, 32);
		glTexCoordPointer(2, GL_FLOAT, stride, 24);
		
		glDrawArrays(GL_QUADS, 0, vertCount);
		glDrawElements(GL_QUADS, indices);
		GL12.glDrawRangeElements(GL11.GL_TRIANGLES, 0, vertCount, indexBufferSize, GL11.GL_UNSIGNED_INT, 0);
		GL12.glDrawRangeElements(GL11.GL_TRIANGLES, 0, vertCount, myIntDirectBuffer);
		
		
		glDisableClientState(GL_TEXTURE_COORD_ARRAY);
		//glDisableClientState(GL_COLOR_ARRAY);
		glDisableClientState(GL_NORMAL_ARRAY);
		glDisableClientState(GL_VERTEX_ARRAY);
	}*/
}
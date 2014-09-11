package com.metaplains.gfx;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.*;

import java.util.Stack;
import com.metaplains.core.GameClient;
import com.metaplains.entities.lights.*;
import com.metaplains.utils.FastMath;
import com.metaplains.utils.Geom;
import com.metaplains.utils.Mat4F;
import com.metaplains.utils.Vec3F;

public final class GL {

	public static final int PROJECTION = 1;
	public static final int VIEW = 2;
	public static final int MODEL = 3;
	
	@SuppressWarnings("unchecked")
	private static Stack<Mat4F>[] matrixStacks = (Stack<Mat4F>[]) new Stack[4];
	static {
		matrixStacks[PROJECTION] = new Stack<Mat4F>();
		matrixStacks[PROJECTION].push(new Mat4F());
		matrixStacks[VIEW] = new Stack<Mat4F>();
		matrixStacks[VIEW].push(new Mat4F());
		matrixStacks[MODEL] = new Stack<Mat4F>();
		matrixStacks[MODEL].push(new Mat4F());
	}
	private static Mat4F currentMatrix;
	private static int matrixMode;
	
	private static Mat4F normalMatrix = new Mat4F();
	
	//TODO: Localise, encapsulate.
	private static void updateShaderMatrices(int programID) {
		glUniformMatrix4(glGetUniformLocation(programID, "projectionMatrix"), false, matrixStacks[PROJECTION].peek().toFloatBuffer());
		glUniformMatrix4(glGetUniformLocation(programID, "viewMatrix"), false, matrixStacks[VIEW].peek().toFloatBuffer());
		glUniformMatrix4(glGetUniformLocation(programID, "modelMatrix"), false, matrixStacks[MODEL].peek().toFloatBuffer());
	}
	
	public static void setMatrixMode(int matrixID) {
		GL.currentMatrix = matrixStacks[matrixMode=matrixID].peek();
	}
	
	public static void pushMatrix() {
		matrixStacks[matrixMode].push(currentMatrix=currentMatrix.clone());
	}
	
	public static void popMatrix() {
		matrixStacks[matrixMode].pop();
		currentMatrix = matrixStacks[matrixMode].peek();
	}
	
	public static void setIdentity() {
		currentMatrix.setIdentity();
	}
	
	public static void setMatrix(Mat4F matrix) {
		currentMatrix.set(matrix);
	}
	
	public static void setMatrix(float... matrix) {
		currentMatrix.set(matrix);
	}
	
	public static void multMatrix(Mat4F matrix) {
		currentMatrix.multiply(matrix);
	}
	
	public static void multMatrix(float... matrix) {
		currentMatrix.multiply(matrix);
	}
	
	public static Mat4F getMatrix(int matrixID) {
		return matrixStacks[matrixID].peek().clone();
	}
	
	public static void translate(float x, float y, float z) {
		currentMatrix.translate(x, y, z);
	}
	
	public static void scale(float x, float y, float z) {
		currentMatrix.scale(x, y, z);
	}
	
	private static void frustum(float left, float right, float bottom, float top, float zNear, float zFar) {
		multMatrix( (2.0F*zNear)/(right-left), 0, 0, 0,
					0, (2.0F*zNear)/(top-bottom), 0, 0,
					(right+left)/(right-left), (top+bottom)/(top-bottom), -(zFar+zNear)/(zFar-zNear), -1,
					0, 0, (2.0F*zFar*zNear)/(zFar-zNear), 0 );
	}

	public static void ortho(float left, float right, float bottom, float top, float near, float far) {
		multMatrix( 2.0F/(right-left), 0, 0, 0,
					0, 2.0F/(top-bottom), 0, 0,
					0, 0, -2.0F/(far-near), 0,
					-(right+left)/(right-left), -(top+bottom)/(top-bottom), -(far+near)/(far-near), 1 );
	}
	
	/**
	 * Replaces gluPerspective. Sets the frustum to perspective mode.
	 * @param fov Field of vision in degrees in the y direction
	 * @param aspect Aspect ratio of the viewport
	 * @param zNear The near clipping distance
	 * @param zFar The far clipping distance
	 */
	public static void perspective(float fov, float aspect, float zNear, float zFar) {
		/*float fH = FastMath.tan(fov/2)*zNear;
		float fW = fH*aspect;
		frustum(-fW, fW, -fH, fH, zNear, zFar);*/
		/*float radians = fov/2.0F;
		float deltaZ = zFar - zNear;
		float sine = FastMath.sin(radians);
		if ((deltaZ == 0.0F) || (sine == 0.0F) || (aspect == 0.0F))
			return;
		float cotangent = FastMath.cos(radians) / sine;
		multMatrix( cotangent / aspect, 0, 0, 0,
				0, cotangent, 0, 0,
				0, 0, -(zFar + zNear) / deltaZ, -1.0F,
				0, 0, -2.0F * zNear * zFar / deltaZ, 0.0F );*/
		float scaleY = 1.0F/FastMath.tan(fov/2.0F);
		float frustLen = zFar - zNear;
		multMatrix( scaleY/aspect, 0, 0, 0,
				0, scaleY, 0, 0,
				0, 0, -((zFar + zNear) / frustLen), -1,
				0, 0, -((2.0F * zFar * zNear) / frustLen), 0 );
	}
	
	public static void lookAt(Vec3F eye, Vec3F look, Vec3F up) {
		//TODO: Avoid declaring new variables; this has to be blazing fast!
		Vec3F forward = Geom.dir(eye, look).normalise();
		Vec3F side = Geom.cross(forward, up).normalise();
		up = Geom.cross(side, forward);
		setMatrix( side.x, up.x, -forward.x, 0,
					side.y, up.y, -forward.y, 0,
					side.z, up.z, -forward.z, 0,
					0, 0, 0, 1);
		translate(-eye.x, -eye.y, -eye.z);
	}

	//TODO: Consider moving this to Entity/GameScreen and take out glGetUniformLocation calls and tex7 binds.
	public static void useProgram(int programID) {
		glUseProgram(programID);
		if (programID > Shader.NONE) {
			//TODO: Create a scene for everything and have GUIs as entities.
			updateShaderMatrices(programID);
			if(programID == Shader.STD)
				glUniform1i(glGetUniformLocation(programID, "texture"), 0);
			if (GameClient.game.currentScene != null) {
				//TODO: Manage lighting client-side.
				if (glIsEnabled(GL_LIGHTING)) {
					glUniform1i(glGetUniformLocation(programID, "isLightingEnabled"), 1);
					for (Light light : GameClient.game.currentScene.entityManager.lights) {
						glActiveTexture(GL_TEXTURE7);
						glBindTexture(GL_TEXTURE_2D, ((LightPoint)light).depthTexID);
						glActiveTexture(GL_TEXTURE0);
						glUniform1i(glGetUniformLocation(programID, "shadowMap"), 7);
						glUniform1f(glGetUniformLocation(programID, "shadowMapRes"), ((LightPoint)light).shadowMapRes);
						break;
					}
				} else {
					glUniform1i(glGetUniformLocation(programID, "isLightingEnabled"), 0);
				}
			}
		}
	}
}
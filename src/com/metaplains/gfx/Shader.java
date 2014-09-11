package com.metaplains.gfx;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import org.lwjgl.opengl.*;

import java.io.*;
import java.net.URL;

/**
 * A class with static methods to handle shader program creation operations.
 * @author Paraknight
 */
public class Shader {

	public static final int VERTEX_SHADER = GL_VERTEX_SHADER;
	public static final int FRAGMENT_SHADER = GL_FRAGMENT_SHADER;
	
	public static int NONE;
	public static int STD;
	public static int TERRAIN;
	public static int WATER;
	
	private static int COMMON_VERT;
	private static int COMMON_FRAG;
	
	public static void loadLocalShaderProgs() {
		COMMON_VERT = loadLocalShader("common.vert", VERTEX_SHADER);
		COMMON_FRAG = loadLocalShader("common.frag", FRAGMENT_SHADER);
		
		//TODO: Consider directory walk.
		NONE = 0;
		STD = loadLocalShaderProg("standard");
		TERRAIN = loadLocalShaderProg("terrain");
		WATER = loadLocalShaderProg("water");
	}

	public static int createProgram() {
		int programID = glCreateProgram();
		if (programID == 0)
			flagError("An error has occured while creating a shader program object.");
		glAttachShader(programID, COMMON_VERT);
		glAttachShader(programID, COMMON_FRAG);
		return programID;
	}
	
	public static void attachShader(int programID, int shaderID) {
		glAttachShader(programID, shaderID);
	}
	
	public static void linkAndValidateProgram(int programID) {
		glLinkProgram(programID);
		glValidateProgram(programID);
	}
	
	public static int loadShader(URL url, int type) {
		int shaderID = glCreateShader(type);
		if (shaderID == 0)
			flagError("An error has occured while creating a shader object.");
		StringBuilder shaderSrc = new StringBuilder();
		try {
			InputStream in = url.openStream();
			InputStreamReader ir = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(ir);
			String line;
			while ((line = br.readLine()) != null) {
				shaderSrc.append(line).append('\n');
			}
			br.close();
			ir.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		glShaderSource(shaderID, shaderSrc);
		glCompileShader(shaderID);
		if (glGetShader(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
			String urlStr = url.toString();
			flagError("Unable to compile GLSL shader \""+urlStr.substring(urlStr.lastIndexOf("/")+1, urlStr.length())+"\":\n"+
					ARBShaderObjects.glGetInfoLogARB(shaderID, ARBShaderObjects.glGetObjectParameteriARB(shaderID, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB)));
		}
		return shaderID;
	}

	private static int loadLocalShader(String fileName, int type) {
		return loadShader(Shader.class.getResource("shaders/"+fileName), type);
	}
	
	private static int loadLocalShaderProg(String name) {
		int programID = createProgram();
		glAttachShader(programID, loadLocalShader(name+".vert", VERTEX_SHADER));
		glAttachShader(programID, loadLocalShader(name+".frag", FRAGMENT_SHADER));
		glLinkProgram(programID);
		glValidateProgram(programID);
		return programID;
	}
	
	private static void flagError(String errorMessage) {
		//TODO: Standardise error output and crash.
		System.err.println(errorMessage);
		Display.destroy();
		System.exit(1);
	}
}
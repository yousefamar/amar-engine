package com.metaplains.world.terrain;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.ARBMultitexture.*;
import static org.lwjgl.opengl.ARBVertexProgram.*;
import static org.lwjgl.opengl.ARBFragmentProgram.*;
import java.nio.DoubleBuffer;
import org.lwjgl.BufferUtils;
import com.metaplains.core.GameScreen;
import com.metaplains.entities.Entity;
import com.metaplains.entities.Tickable;
import com.metaplains.gfx.Shader;
import com.metaplains.gfx.Texture;
import com.metaplains.gfx.cam.ClippingSphere;
import com.metaplains.world.scenes.Scene;

public class Water extends Entity implements Tickable {

	private float waveTime = 0.0F,
				  waveWidth = 0.1F,
				  waveHeight = 1.0F,
				  waveFreq = 0.1F;
	private int waveTimeLoc = glGetUniformLocation(Shader.WATER, "waveTime");
	private int waveWidthLoc = glGetUniformLocation(Shader.WATER, "waveWidth");
	private int waveHeightLoc = glGetUniformLocation(Shader.WATER, "waveHeight");
	private int waterListID;

	//TODO: Customisation parameters.
	
	public Water(Scene scene) {
		super(scene, 0, 0, 0); //TODO: Huff.
		loadWaterDisplayList();
		scene.entityManager.scheduleEntityTick(this);
	}
	
	private void loadWaterDisplayList() {
		waterListID = glGenLists(1);
		glNewList(waterListID, GL_COMPILE);
		glEnable(GL_BLEND);
		glBegin(GL_QUADS);
		float waterHeight = 0.2F*64.0F;
		glVertex3f(0, waterHeight, 256);
		glVertex3f(256, waterHeight, 256);
		glVertex3f(256, waterHeight, 0);
		glVertex3f(0, waterHeight, 0);
		/*for (int z = 0; z < 257; z++) {
			for (int x = 0; x < 257; x++) {
				glVertex3f(x, waterHeight, z+1);
				glVertex3f(x+1, waterHeight, z+1);
				glVertex3f(x+1, waterHeight, z);
				glVertex3f(x, waterHeight, z);
			}
		}*/
		glEnd();
		glDisable(GL_BLEND);
		glEndList();
	}

	@Override
	public void tick() {
		waveTime += waveFreq;
		scene.entityManager.scheduleEntityTick(this);
	}

	@Override
	public ClippingSphere getClippingSphere() {
		return null;
	}
	
	@Override
	public void render(GameScreen screen, boolean shadowPass) {
		if (shadowPass)
			return;
		glUseProgram(Shader.WATER);
		/* Change time */
		glUniform1f(waveTimeLoc, waveTime);
		glUniform1f(waveWidthLoc, waveWidth);
		glUniform1f(waveHeightLoc, waveHeight);
		glCallList(waterListID);
		glUseProgram(Shader.STD);
	}
}
package com.metaplains.gfx;

import static com.metaplains.gfx.GL.*;
import static org.lwjgl.opengl.GL11.*;

import java.awt.Color;

import javax.vecmath.Vector3f;

import com.bulletphysics.linearmath.DebugDrawModes;
import com.bulletphysics.linearmath.IDebugDraw;
import com.metaplains.core.GameScreen;

public class DebugDrawer extends IDebugDraw {

	private final Vector3f tmpVec = new Vector3f();
	private int debugMode;

	private GameScreen screen;
	
	public DebugDrawer(GameScreen screen) {
		this.screen = screen;
	}
	
	@Override
	public void drawLine(Vector3f from, Vector3f to, Vector3f color) {
		glDisable(GL_LIGHTING);
		glBegin(GL_LINES);
		glColor3f(color.x, color.y, color.z);
		glVertex3f(from.x, from.y, from.z);
		glVertex3f(to.x, to.y, to.z);
		glEnd();
		glEnable(GL_LIGHTING);
	}

	@Override
	public void drawContactPoint(Vector3f pointOnB, Vector3f normalOnB, float distance, int lifeTime, Vector3f color) {
		if ((debugMode & DebugDrawModes.DRAW_CONTACT_POINTS) != 0) {
			glDisable(GL_LIGHTING);
			Vector3f to = tmpVec;
			to.scaleAdd(distance*100f, normalOnB, pointOnB);
			Vector3f from = pointOnB;

			to.normalize(normalOnB);
			to.scale(10f);
			to.add(pointOnB);
			glLineWidth(3f);
			glPointSize(6f);
			glBegin(GL_POINTS);
			glColor3f(color.x, color.y, color.z);
			glVertex3f(from.x, from.y, from.z);
			glEnd();

			glBegin(GL_LINES);
			glColor3f(color.x, color.y, color.z);
			glVertex3f(from.x, from.y, from.z);
			glVertex3f(to.x, to.y, to.z);
			glEnd();

			glLineWidth(1f);
			glPointSize(1f);

			//glRasterPos3f(from.x, from.y, from.z);
			//char buf[12];
			//sprintf(buf," %d",lifeTime);
			// TODO: BMF_DrawString(BMF_GetFont(BMF_kHelvetica10),buf);
			glEnable(GL_LIGHTING);
		}
	}


	@Override
	public void reportErrorWarning(String paramString) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw3dText(Vector3f location, String textString) {
		pushMatrix();
		translate(location.x, location.y, location.z);
		//TODO: Actually make it face you.
		//glRotated(-world.camera.yaw, 0, 1, 0);
		//glRotated(world.camera.pitch, 1, 0, 0);
		//glRotated(-world.camera.roll, 0, 0, 1);
		translate(0, screen.getTTFont().getHeight(), 0);
		glDisable(GL_CULL_FACE);
		glDisable(GL_LIGHTING);
		scale(1, -1, 1);
		screen.setColor(Color.BLACK);
		screen.drawString(textString, 1, 1, 1, 1, TrueTypeFont.ALIGN_CENTER);
		screen.setColor(Color.WHITE);
		screen.drawString(textString, 0, 0, 1, 1, TrueTypeFont.ALIGN_CENTER);
		glEnable(GL_LIGHTING);
		glEnable(GL_CULL_FACE);
		popMatrix();
	}

	@Override
	public void setDebugMode(int debugMode) {
		this.debugMode = debugMode;
	}

	@Override
	public int getDebugMode() {
		return debugMode;
	}
}
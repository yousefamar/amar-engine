package com.metaplains.core;

import static com.metaplains.gfx.GL.*;
import static org.lwjgl.opengl.GL11.*;
import java.awt.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.lwjgl.input.Keyboard;

import com.metaplains.gfx.*;
import com.metaplains.utils.AABB;

@SuppressWarnings("serial")
public class GameScreen extends Canvas {

	public final Dimension screenDims;

	private GameClient game;
	private TrueTypeFont ttFont;
	
	public GameScreen(GameClient game) {
		this.game = game;
		
		screenDims = new Dimension(1280, 720);
		setSize(screenDims);
		setPreferredSize(screenDims);
		setBackground(Color.BLACK);
		setFocusTraversalKeysEnabled(false);
		setFocusable(true);
		requestFocus();
		setIgnoreRepaint(true);
		/*addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				screenDims.set(getSize());
			}
		});*/
		setVisible(true);
	}
	
	private Thread gameThread;
	
	@Override
	public final void addNotify() {
		super.addNotify();
		gameThread = new Thread(GameClient.game, "Main Game Thread");
		gameThread.start();
	}
	
	@Override
	public final void removeNotify() {
		GameClient.game.isCloseRequested = true;
		try {
			gameThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		super.removeNotify();
	}
	
	public void init() {
		glViewport(0, 0, screenDims.width, screenDims.height);
		
		glShadeModel(GL_SMOOTH);
		glClearColor(0, 0, 0, 1);
		glEnable(GL_CULL_FACE); //TODO: Test.
		glClearDepth(1);
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);
		glClearStencil(0);
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);//FASTEST);
		glEnable(GL_LIGHTING);
		glEnable(GL_LIGHT0);
		//glColorMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE);
		//glEnable(GL_COLOR_MATERIAL);
		setFont(new Font("Arial", Font.PLAIN, 18));

		/*glMatrixMode(GL_PROJECTION);
		glPushMatrix();
		glLoadIdentity();
		glOrtho(0, screenDims.width+2, screenDims.height+2, 0, -1, 1);
		glViewport(0, 0, screenDims.width+2, screenDims.height+2);
		glMatrixMode(GL_MODELVIEW);*/
		
		System.out.println("Loading Textures..."); //TODO: Load all resources from here?
		Texture.loadTextures();
		System.out.println("Loading Models...");
		Model.loadModels();
		System.out.println("Loading Shaders...");
		Shader.loadLocalShaderProgs();

		setMatrixMode(PROJECTION);
		setIdentity();
		//TODO: Update on resize.
		perspective(90F, ((float) screenDims.width / (float) screenDims.height), 0.1F, 1000.0F);
	}
	
	public void setColor3i(int red, int green, int blue) {
		glColor3ub((byte)red, (byte)green, (byte)blue);
	}
	
	public void setColor(Color color) {
		setColor3i(color.getRed(), color.getGreen(), color.getBlue());
	}
	
	public void drawRect(int x, int y, int width, int height) {
		//TODO: Avoid this like hellfire.
		//useProgram(Shader.COLOR);
		glBegin(GL_LINE_LOOP);
		glVertex3f(x, y+height, 0);
		glVertex3f(x+width, y+height, 0);
		glVertex3f(x+width, y, 0);
		glVertex3f(x, y, 0);
		glEnd();
		//useProgram(Shader.STD);
	}
	
	public void fillRect(int x, int y, int width, int height) {
		//useProgram(Shader.COLOR);
		glBegin(GL_QUADS);
		glVertex3f(x, y+height, 0);
		glVertex3f(x+width, y+height, 0);
		glVertex3f(x+width, y, 0);
		glVertex3f(x, y, 0);
		glEnd();
		//useProgram(Shader.STD);
	}
	
	public void drawTexturedRect(int x, int y, int width, int height) {
		glBegin(GL_QUADS);
		//glColor3f(1, 1, 1);
		glNormal3f(0, 0, -1);
		glTexCoord2f(0, 1);
		glVertex3f(x, y+height, 0);
		glTexCoord2f(1, 1);
		glVertex3f(x+width, y+height, 0);
		glTexCoord2f(1, 0);
		glVertex3f(x+width, y, 0);
		glTexCoord2f(0, 0);
		glVertex3f(x, y, 0);
		glEnd();
	}
	
	@Override
	public void setFont(Font font) {
		super.setFont(font);
		this.ttFont = new TrueTypeFont(font, true);//, new char[]{'\u0149'});
	}
	
	public TrueTypeFont getTTFont() {
		return ttFont;
	}
	
	public void drawString(String string, int x, int y, float scaleX, float scaleY, int format) {
		ttFont.drawString(x, y, string, scaleX, scaleY, format);
		glBindTexture(GL_TEXTURE_2D, Texture.NONE);
	}

	public void renderModel(Model model) {
		//TODO: Create default model or similar.
		if (model.id != 0)
			glCallList(model.id);
	}
	
	public void renderAABBFixed(AABB aabb) {
		glColor3f(1, 0.62F, 0);
		glBegin(GL_LINE_LOOP);
		glVertex3f(aabb.minX, aabb.minY, aabb.minZ);
		glVertex3f(aabb.minX, aabb.minY, aabb.maxZ);
		glVertex3f(aabb.minX, aabb.maxY, aabb.maxZ);
		glVertex3f(aabb.minX, aabb.maxY, aabb.minZ);
		glEnd();
		glBegin(GL_LINE_LOOP);
		glVertex3f(aabb.maxX, aabb.minY, aabb.minZ);
		glVertex3f(aabb.maxX, aabb.minY, aabb.maxZ);
		glVertex3f(aabb.maxX, aabb.maxY, aabb.maxZ);
		glVertex3f(aabb.maxX, aabb.maxY, aabb.minZ);
		glEnd();
		glBegin(GL_LINES);
		glVertex3f(aabb.minX, aabb.minY, aabb.minZ);
		glVertex3f(aabb.maxX, aabb.minY, aabb.minZ);
		glVertex3f(aabb.minX, aabb.minY, aabb.maxZ);
		glVertex3f(aabb.maxX, aabb.minY, aabb.maxZ);
		glVertex3f(aabb.minX, aabb.maxY, aabb.maxZ);
		glVertex3f(aabb.maxX, aabb.maxY, aabb.maxZ);
		glVertex3f(aabb.minX, aabb.maxY, aabb.minZ);
		glVertex3f(aabb.maxX, aabb.maxY, aabb.minZ);
		glEnd();
	}
	
	public void renderFog(float... fogColor) {
		// TODO: Consider option for toggling fog modes?
		ByteBuffer temp = ByteBuffer.allocateDirect(16);
		temp.order(ByteOrder.nativeOrder());
		temp.asFloatBuffer().put(fogColor).flip();
		glFogi(GL_FOG_MODE, GL_LINEAR);
		glFog(GL_FOG_COLOR, temp.asFloatBuffer());
		glFogf(GL_FOG_DENSITY, 0.2f);
		glHint(GL_FOG_HINT, GL_NICEST);
		glFogf(GL_FOG_START, 5F);
		glFogf(GL_FOG_END, 100F);
		glEnable(GL_FOG);
	}
	
	public void renderAxes() {
		glDisable(GL_LIGHTING);
		glBegin(GL_LINES);
		glNormal3f(0, 1, 0);
		
		/* Draw x-axis line. */
		glColor3f(1, 0, 0);
		glVertex3f(0, 0, 0);
		glVertex3f(200, 0, 0);

		/* Draw y-axis line. */
		glColor3f(0, 1, 0);
		glVertex3f(0, 0, 0);
		glVertex3f(0, 200, 0);

		/* Draw z-axis line. */
		glColor3f(0, 0, 1);
		glVertex3f(0, 0, 0);
		glVertex3f(0, 0, 200);

		/* Draw chunk lines. */
		glColor3f(1, 0, 1);
		for (int i = 0; i < 4; i++) {
			glVertex3f(i * 32 + 32, 0, 0);
			glVertex3f(i * 32 + 32, 0, 200);
			glVertex3f(0, 0, i * 32 + 32);
			glVertex3f(200, 0, i * 32 + 32);
		}
		glEnd();
		glEnable(GL_LIGHTING);
	}
	
	public void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		setMatrixMode(MODEL);
		setIdentity();
		if(game.currentScene != null)
			game.currentScene.render(this);
		
		/* Render GUI */
		//TODO: Clean this up.
		setMatrixMode(PROJECTION);
		pushMatrix();
		setIdentity();
		ortho(0, screenDims.width, screenDims.height, 0, -1, 1);
		
		setMatrixMode(VIEW);
		pushMatrix();
		setIdentity();
		setMatrixMode(MODEL);
		pushMatrix();
		setIdentity();
		glDisable(GL_DEPTH_TEST);
		glDisable(GL_LIGHTING);
		useProgram(Shader.STD);
		glBindTexture(GL_TEXTURE_2D, Texture.NONE);
		game.currentGUIScreen.render(this);
		glEnable(GL_LIGHTING);
		glEnable(GL_DEPTH_TEST);
		
		popMatrix();
		setMatrixMode(VIEW);
		popMatrix();
		setMatrixMode(PROJECTION);
		popMatrix();
	}
}
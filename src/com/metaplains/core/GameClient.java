package com.metaplains.core;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Random;
import javax.swing.JFrame;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import com.metaplains.gfx.gui.*;
import com.metaplains.net.NetIOManager;
import com.metaplains.world.scenes.Scene;
import com.metaplains.world.scenes.SceneIsland;

public class GameClient implements Runnable {
	
	//TODO: Consider making these all not static.
	public static GameClient game;
	public boolean isCloseRequested;
	
	
	//TODO: Move initialisations to constructor.
	public GameScreen screen;
	public InputManager inputManager;
	public SaveManager saveManager;
	public NetIOManager netIOManager;
	public GUIElement currentGUIScreen;
	public Scene currentScene;
	
	private int userID;
	private String sessionID;
	
	public GameClient(int userID, String sessionID) {
		this.userID = userID;
		this.sessionID = sessionID;
		this.screen = new GameScreen(this);
		this.inputManager = new InputManager(this);
		this.saveManager = new SaveManager();
	}
	
	private void tickGame() {
		//TODO: Consider creating a separate thread for network IO.
		if (netIOManager != null)
			netIOManager.handlePackets();
		inputManager.handleInput();
		if(currentScene != null)
			currentScene.tick();
		currentGUIScreen.tick();
	}
	
	@Override
	public void run() {
		try {
			Display.setParent(screen);
			//Display.setFullscreen(true);
			//Display.setVSyncEnabled(true);
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		screen.init();

		showMainMenu();
		
		long time = getTimeMS();
		long lastSecond = time;
		long lastGameTick = time;
		int tpsCounter = 0;
		while (!isCloseRequested && !Display.isCloseRequested()) {
			//TODO: Find a more stable way to cap TPS to 20.
			time = getTimeMS();
			if (time - lastSecond > 1000) {
				tpsCounter = 0;
				lastSecond += 1000;
			}
			if (tpsCounter >= 20)
				lastGameTick = time;
			if (time - lastGameTick > 50) {
				tickGame();
				tpsCounter++;
				lastGameTick += 50;
			}
			screen.render();
			Display.sync(100);
			Display.update();
		}
		Display.destroy();
		exit(0);
	}
		
	private long getTimeMS() {
		return (System.nanoTime() / 1000000);
	}
	
	public void login(String username, String password) {
		currentGUIScreen = new GUIMessageFS("Logging in...");
		//TODO: Don't do this; multithread (also the sleep later on).
		screen.render();
		Display.update();
		
		String parameters = "username="+username+"&password="+password;
		HttpURLConnection connection = null;
		String response;
		try {
			connection = (HttpURLConnection) new URL("http://www.metaplains.com/usr/javaLogin.php").openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			connection.setRequestProperty("Content-Length",	Integer.toString(parameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
			out.writeBytes(parameters);
			out.flush();
			out.close();

			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			response = in.readLine();
			in.close();
		} catch (Exception e) {
			response = "-1:Unable to connect to login server.";
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		if (response == null)
			response = "-1:No response from login server (end of stream).";
		
		String[] data = response.split(":");
		int prefix = Integer.parseInt(data[0].trim());
		if (prefix < 0) {
			((GUIMessageFS) currentGUIScreen).setMessage(data[1].trim());
			screen.render();
			Display.update();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			currentGUIScreen = new GUILogin(this);
		} else {
			userID = prefix;
			sessionID = data[1].trim();
			currentGUIScreen = new GUIMainMenu(this);
		}
	}
	
	public void joinScene(Scene scene) {
		if (currentScene != null)
			currentScene.destroy();
		currentScene = scene;
		scene.init();
	}
	
	public void showMainMenu() {
		currentGUIScreen = (userID>=0)?new GUIMainMenu(this):new GUILogin(this);
	}
	
	public void startSinglePlayerGame() {
		joinScene(new SceneIsland("testMap"));
		currentGUIScreen = new GUIWorld();
	}
	
	public void joinMultiplayerGame(String host, int port) {
		currentGUIScreen = new GUIMessageFS("Connecting to Game Server...");
		try {
			netIOManager = new NetIOManager(this, new Socket(host, port));
		} catch (UnknownHostException e) {
			System.err.println("Could not resolve host.");
			System.exit(1);
		} catch (ConnectException e) {
			System.err.println("Could not connect to host.");
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		((GUIMessageFS) currentGUIScreen).setMessage("Authenticating SessionID...");
		netIOManager.establishServerCommunication(userID, sessionID);
	}
	
	public void catchException(Exception e) {
		e.printStackTrace();
		System.exit(1);
	}
	
	public void exit(int status) {
		System.exit(status);
	}
	
	public static void main(String[] args) {
		//TODO: Create launcher with exe and favicon and alles drum und dran.
		final JFrame frame = new JFrame("MMO Client");
		//frame.setIconImage(Toolkit.getDefaultToolkit().getImage(Main.class.getResource("icon16.png")));
		game = new GameClient(Math.abs(new Random().nextInt()), null); //TODO: Only for debug.
		frame.add(game.screen, BorderLayout.CENTER);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				frame.remove(game.screen);
			}
		});
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
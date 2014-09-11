package com.metaplains.core;

import java.io.*;
import com.metaplains.utils.Vec3F;
import com.metaplains.world.terrain.Terrain;

public class SaveManager {

	public String saveDir;
	
	public SaveManager() {
		saveDir = System.getProperty("user.home")+File.separator+".mmo"+File.separator;
		File saveFile = new File(saveDir);
		if (!saveFile.exists() && !saveFile.mkdirs())
			System.err.println("Unable to create save directory.");
		File mapFile = new File(saveDir, "worlds");
		if (!mapFile.exists() && !mapFile.mkdirs())
			System.err.println("Unable to create world save directory.");
	}
	
	public boolean loadTerrain(String worldName, Terrain terrain) {
		File worldFile = new File(saveDir + "worlds" + File.separator + worldName + ".world");
		if (!worldFile.exists())
			return false;
		try {
			FileInputStream fis = new FileInputStream(worldFile);
			DataInputStream in = new DataInputStream(fis);
			terrain.seed = in.readInt();
			for (int z = 0; z < terrain.heightMap.height; z++) {
				for (int x = 0; x < terrain.heightMap.width; x++) {
					terrain.heightMap.heights[x][z] = in.readFloat();
					terrain.normalMap[x][z] = new Vec3F(in.readFloat(), in.readFloat(), in.readFloat());
				}
			}
			in.close();
			fis.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public boolean saveTerrain(String worldName, Terrain terrain) {
		try {
			FileOutputStream fis = new FileOutputStream(new File(saveDir + "worlds" + File.separator + worldName + ".world"));
			DataOutputStream out = new DataOutputStream(fis);
			out.writeInt(terrain.seed);
			for (int z = 0; z < terrain.heightMap.height; z++) {
				for (int x = 0; x < terrain.heightMap.width; x++) {
					out.writeFloat(terrain.heightMap.heights[x][z]);
					out.writeFloat(terrain.normalMap[x][z].x);
					out.writeFloat(terrain.normalMap[x][z].y);
					out.writeFloat(terrain.normalMap[x][z].z);
				}
			}
			out.close();
			fis.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	//private synchronized static void saveChunk(Chunk chunk) {

	/*public static void save(final Chunk chunk) {
		new Thread(new Runnable() {
            public void run() { saveChunk(chunk); }
        }).start();
	}
	
	public static void load(final Chunk chunk) {
		//TODO: Figure out the best way to do this.
		//new Thread(new Runnable() {
        //    public void run() { 
            	loadChunk(chunk);
        //    }
        //}).start();
	}*/
}
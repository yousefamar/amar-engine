package com.metaplains.gfx;

import java.io.*;
import java.util.HashMap;

public class MaterialLibrary {

	public HashMap<String, Material> materials = new HashMap<String, Material>();
	public Material defaultMaterial;
	private String fileName;
	
	public MaterialLibrary(String fileName) {
		this.fileName = fileName;
	}
	
	public void loadMaterials() {
		materials.clear();
		try {
			InputStream in = MaterialLibrary.class.getResource("models/materials/"+fileName).openStream();
			InputStreamReader ir = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(ir);
			String line;
			Material material = null;
			while ((line = br.readLine()) != null) {
				if (line.length()<2 || line.startsWith("#"))
    				continue;
    			String[] sline = line.split("\\s+");
    			if (sline[0].equals("newmtl")) {
    				boolean setDefault = false;
    				if (defaultMaterial == null)
    					setDefault = true;
    				materials.put(sline[1], material = new Material(sline[1]));
    				if (setDefault)
    					defaultMaterial = material;
    			} else if (sline[0].startsWith("map_")) {
    				material.textureID = Texture.loadTextureMipmaps(Texture.loadImage(sline[1]));
    			}
    		}
			br.close();
			ir.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//TODO: Complete all MTL file specifications.
	public class Material {
		public String name;
		public int textureID;
		
		public Material(String name) {
			this.name = name;
		}
	}
}
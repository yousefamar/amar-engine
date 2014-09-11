package com.metaplains.gfx;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1i;

import java.io.*;
import java.util.*;
import com.metaplains.gfx.MaterialLibrary.Material;
import com.metaplains.utils.*;

/**
 * A class that can load, parse and store WaveFront .obj files.
 * For efficiency, support for polygons with more than 3 vertices has been deprecated.
 * The .obj files should be exported in such a way to use only triangles.
 * @author Paraknight
 */
public class Model {
	//TODO: static HashMap<Class<Entity>, Model> modelMap = new HashMap<Class<Entity>, Model>();
	//public static HashMap<String, Model> modelMap = new HashMap<String, Model>();

	public static Model CUBE;
	public static Model SPHERE;
	public static Model TREE;
	public static Model HOUSE;
	public static Model MOHAWK;
	public static Model HEAD;
	public static Model TORSO;
	public static Model QUAD;
	
	public static void loadModels() {
		/*try {
			walkDir(new File(Model.class.getResource("models").toURI()));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}*/
		
		CUBE = loadModel("cube").buildDisplayList();
		SPHERE = loadModel("icosphere").buildDisplayList();
		TREE = loadModel("tree").invertV().buildDisplayList();
		HOUSE = loadModel("house").invertV().buildDisplayList();
		MOHAWK = loadModel("mohawk").invertV().buildDisplayList();
		HEAD = loadModel("head").invertV().buildDisplayList();
		TORSO = loadModel("torso").invertV().buildDisplayList();
		QUAD = loadModel("quadcopter").invertV().buildDisplayList();
	}
	
	public int id;
	public String name;
	public ArrayList<Vec3F> vertices;
	public ArrayList<Vec2F> texCoords;
	public ArrayList<Vec3F> normals;
	public ArrayList<Mesh> meshes;
	
	public Model(String name) {
		this.name = name;
		this.vertices = new ArrayList<Vec3F>();
		this.texCoords = new ArrayList<Vec2F>();
		this.normals = new ArrayList<Vec3F>();
		this.meshes = new ArrayList<Mesh>();
	}

	private Model buildDisplayList() {
		this.id = Model.buildDisplayList(this);
		return this;
	}
	
	private Model scale(float amount) {
		for (Vec3F vertex: vertices)
			vertex.scale(amount);
		return this;
	}
	
	private Model invertV() {
		for (Vec2F texCoord: texCoords)
			texCoord.y = 1 - texCoord.y;
		return this;
	}
	
	public class Mesh {
		public String name;
		public Material material;
		public ArrayList<TexturedPolygon> faces;

		public Mesh(String name) {
			this.name = name;
			this.faces = new ArrayList<TexturedPolygon>();
		}
	}
	
	private static int buildDisplayList(Model model) {
		int listID = glGenLists(1);
		glNewList(listID, GL_COMPILE);
		//TODO: Set to actual material lighting.
		//glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		for (Mesh mesh : model.meshes) {
			if (mesh.material != null)
				glBindTexture(GL_TEXTURE_2D, mesh.material.textureID);
			glBegin(GL_TRIANGLES);
			for (TexturedPolygon face : mesh.faces) {
				for (int i=0; i<face.vertexIDs.size(); i++) {
					if (!face.normalIDs.isEmpty())
						glNormal3f(model.normals.get(face.normalIDs.get(i)-1).x, model.normals.get(face.normalIDs.get(i)-1).y, model.normals.get(face.normalIDs.get(i)-1).z);
					if (!face.texCoordIDs.isEmpty())
						glTexCoord2f(model.texCoords.get(face.texCoordIDs.get(i)-1).x, model.texCoords.get(face.texCoordIDs.get(i)-1).y);
					glVertex3f(model.vertices.get(face.vertexIDs.get(i)-1).x, model.vertices.get(face.vertexIDs.get(i)-1).y, model.vertices.get(face.vertexIDs.get(i)-1).z);
				}
			}
			glEnd();
		}
		glEndList();
		return listID;
	}
	
	private static Model loadModel(String name) {
		Model model =  new Model(name);
		try {
			InputStream in = Model.class.getResource("models/"+name+".obj").openStream();
			InputStreamReader ir = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(ir);
			String line;
			Mesh mesh = null;
			MaterialLibrary matLib = null;
			Material material = null;
			while ((line = br.readLine()) != null) {
				if (line.length()<2 || line.startsWith("#"))
    				continue;
    			String[] sline = line.split("\\s+");
    			if (sline[0].equals("v")) {
    				model.vertices.add(new Vec3F(Float.parseFloat(sline[1]), Float.parseFloat(sline[2]), Float.parseFloat(sline[3])));
    			} else if (sline[0].equals("vt")) {
    				model.texCoords.add(new Vec2F(Float.parseFloat(sline[1]), Float.parseFloat(sline[2])));
    			} else if (sline[0].equals("vn")) {
    				model.normals.add(new Vec3F(Float.parseFloat(sline[1]), Float.parseFloat(sline[2]), Float.parseFloat(sline[3])));
    			} else if (sline[0].equals("f")) {
    				TexturedPolygon face = new TexturedPolygon();
    				for (int i = 1; i < sline.length; i++) {
    					String[] nums = sline[i].split("/");
    					face.vertexIDs.add(Integer.parseInt(nums[0]));
    					if (nums.length > 1 && nums[1].length()>0)
    						face.texCoordIDs.add(Integer.parseInt(nums[1]));
    					if (nums.length > 2 && nums[2].length()>0)
    						face.normalIDs.add(Integer.parseInt(nums[2]));
    				}
    				mesh.faces.add(face);
    			} else if (sline[0].equals("usemtl")) {
    				material = matLib.materials.get(sline[1]);
    				if (material == null)
    					material = matLib.defaultMaterial;
    				mesh.material = material;
    			} else if (sline[0].equals("o")) {
    				model.meshes.add(mesh = model.new Mesh(sline[1]));
    				mesh.material = material;
    			} else if (sline[0].equals("g")) {
    			} else if (sline[0].equals("s")) {
    			} else if (sline[0].equals("mtllib")) {
    				matLib = new MaterialLibrary(sline[1]);
    				matLib.loadMaterials();
    				material = matLib.defaultMaterial;
    			}
    		}
			br.close();
			ir.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//modelMap.put(name, model);
		return model;
	}

	public static void walkDir(File dir) {
        File[] files = dir.listFiles();
        if (files != null)
            for (File file : files) {
                if (file.isDirectory())
					walkDir(file);
                else if (file.getName().endsWith(".obj")) {
                	try{
                		//Load the file.
                	}catch (Exception e){
                		e.printStackTrace();
                	}
                }
            }
	}
}
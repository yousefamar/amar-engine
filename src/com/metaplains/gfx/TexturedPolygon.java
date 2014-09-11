package com.metaplains.gfx;

import java.util.ArrayList;

public class TexturedPolygon {

	public ArrayList<Integer> vertexIDs = new ArrayList<Integer>();
	public ArrayList<Integer> texCoordIDs = new ArrayList<Integer>();
	public ArrayList<Integer> normalIDs = new ArrayList<Integer>();

	public String toString() {
		String info = "";
		for (int i = 0; i < vertexIDs.size(); i++)
			info += vertexIDs.get(i).toString()+"/"+texCoordIDs.get(i).toString()+"/"+normalIDs.get(i).toString()+" ";
		return info;
	}
}

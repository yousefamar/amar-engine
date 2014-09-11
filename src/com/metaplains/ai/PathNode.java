package com.metaplains.ai;

public class PathNode {

	public int x;
	public int y;
	public PathNode parent;
	public int g, h, f;

	public PathNode(PathFinder pathFinder, int x, int y, PathNode parent) {
		this.x = x;
		this.y = y;
		this.parent = parent;
		
		this.g = pathFinder.calcCostToNode(this);
		this.h = pathFinder.calcHeuristic(x, y);
		this.f = g + h;
	}
	
	public boolean equals(Object object) {
		if (object == null || !(object instanceof PathNode) || this.x != ((PathNode)object).x || this.y != ((PathNode)object).y)
			return false;
		return true;
	}
}
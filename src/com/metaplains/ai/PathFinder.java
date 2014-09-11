package com.metaplains.ai;

import java.util.*;

import com.metaplains.entities.EntityTangible;
import com.metaplains.utils.Vec2I;


public class PathFinder {

	//TODO: Think about vehicles?
	private EntityTangible entity;
	private int goalX, goalY;
	private LinkedList<PathNode> open = new LinkedList<PathNode>();
	private LinkedList<PathNode> closed = new LinkedList<PathNode>();
	private Queue<PathNode> path = new LinkedList<PathNode>();
	
	//TODO: Make pathfinder specific to entities that can decide themselves is something is traversable (fly, walk, check terrain).
	public PathFinder(EntityTangible entity) {
		this.entity = entity;
	}
	
	public boolean hasNextNode() {
		return !path.isEmpty();
	}
	
	public Vec2I nextNode() {
		//hasNext is to be used for safety or an exception will be thrown. 
		PathNode nextNode = path.remove();
		return new Vec2I((nextNode.x<<5)+16, (nextNode.y<<5)+16);
	}

	public void generatePathAStar(int startX, int startY, int goalX, int goalY) {
		//TODO: Put threshold on pathfinder as to not scan the entire map for unreachable tiles.
		startX >>= 5;
		startY >>= 5;
		this.goalX = (goalX >>= 5);
		this.goalY = (goalY >>= 5);
		
		if (startX == goalX && startY == goalY) //TODO: Do quick initial checks to see if goal non-traversable and think about this.
			return;
			
		open.clear();
		closed.clear();
		
		open.add(new PathNode(this, startX, startY, null));
		
		while (!open.isEmpty()) {
			PathNode current = open.getFirst();
			
			if(current.x == goalX && current.y == goalY) {
				path = reconstructPathFromNode(current);
				return;
			}

			open.remove(current);
			closed.add(current);
			
			for (PathNode neighbor : getNewTraversableNeighbors(current)) {
				boolean isTentativeBetter = true;
				PathNode temp = neighbor.parent;
				neighbor.parent = current;
				int tentativeG = calcCostToNode(neighbor);
				neighbor.parent = temp;
				if(!open.contains(neighbor)) {
					int i = 0;
					for (PathNode nextNode : open) {
						if (neighbor.f <= nextNode.f)
							break;
						i++;
					}
					open.add(i, neighbor);
				} else {
					//TODO: Block diagonals that are adjacent to two tenatives.
					isTentativeBetter = tentativeG < neighbor.g;
				}
				if (isTentativeBetter) {
					neighbor.parent = current;
					neighbor.g = tentativeG;
					neighbor.f = neighbor.g + neighbor.h;
				}
			}
		}
	}

	private ArrayList<PathNode> getNewTraversableNeighbors(PathNode node) {
		//TODO: Optimise.
		ArrayList<PathNode> env = new ArrayList<PathNode>();
		boolean left = addToListIfNew(env, new PathNode(this, node.x-1, node.y, node));
		boolean right = addToListIfNew(env, new PathNode(this, node.x+1, node.y, node));
		boolean front = addToListIfNew(env, new PathNode(this, node.x, node.y-1, node));
		boolean back = addToListIfNew(env, new PathNode(this, node.x, node.y+1, node));
		if (left || back)
			addToListIfNew(env, new PathNode(this, node.x-1, node.y+1, node));
		if (left || front)
			addToListIfNew(env, new PathNode(this, node.x-1, node.y-1, node));
		if (right || front)
			addToListIfNew(env, new PathNode(this, node.x+1, node.y-1, node));
		if (right || back)
			addToListIfNew(env, new PathNode(this, node.x+1, node.y+1, node));
		return env;
	}
	
	private boolean addToListIfNew(ArrayList<PathNode> list, PathNode node) {
		//TODO: Assume unseen areas empty.
		if (closed.contains(node) /*|| !entity.canTraverse(node.x<<5, node.y<<5)*/)
			return false;
		if (open.contains(node))
			node = open.get(open.indexOf(node));
		list.add(node);
		return true;
	}
	
	private Queue<PathNode> reconstructPathFromNode(PathNode node) {
		LinkedList<PathNode> pathNodes = new LinkedList<PathNode>();
		do {
			pathNodes.add(node);
			node = node.parent;
		} while(node != null);
		Collections.reverse(pathNodes);
		pathNodes.remove();
		return pathNodes;
	}

	protected int calcCostToNode(PathNode node) {
		if(node.parent != null && !node.equals(node.parent))
			return ((node.x != node.parent.x) && (node.y != node.parent.y))?(node.parent.g + 14):(node.parent.g + 10);
		return 0;
	}
	
	protected int calcHeuristic(int x, int y) {
		//TODO: Use a different algorithm (not Manhattan distance).
		return (x<=goalX?goalX-x:x-goalX)+(y<=goalY?goalY-y:y-goalY);
	}
}
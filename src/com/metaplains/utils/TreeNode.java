package com.metaplains.utils;

import java.util.ArrayList;

public class TreeNode<T> {

	public T object;
	public ArrayList<TreeNode<T>> children;
	
	public TreeNode() {
	}
	
	public TreeNode(T object) {
		this.object = object;
	}
	
	public boolean addChild(T child) {
		if (children == null)
			children = new ArrayList<TreeNode<T>>();
		return children.add(new TreeNode<T>(child));
	}
	
	public boolean hasChildren() {
		return !children.isEmpty();
	}
}
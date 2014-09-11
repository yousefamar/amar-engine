package com.metaplains.utils;

public class QuadtreeNode<T> {

	public T object;
	public QuadtreeNode<T> c00;
	public QuadtreeNode<T> c10;
	public QuadtreeNode<T> c01;
	public QuadtreeNode<T> c11;
	public boolean hasChildren;
	
	public QuadtreeNode() {
	}
	
	public QuadtreeNode(T object) {
		this.object = object;
	}

	public void subdivide() {
		c00 = new QuadtreeNode<T>();
		c10 = new QuadtreeNode<T>();
		c01 = new QuadtreeNode<T>();
		c11 = new QuadtreeNode<T>();
		hasChildren = true;
	}
}

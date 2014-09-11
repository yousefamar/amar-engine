package com.metaplains.utils;

import java.nio.FloatBuffer;

import javax.vecmath.Matrix4f;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;

/* NB: Matrix and all operations assume a column-major configuration! */
public class Mat4F implements Cloneable {

	private static float[] temp = new float[16];

	public float[] mat = new float[16];
	/* 0  4  8  12 */
	/* 1  5  9  13 */
	/* 2  6  10 14 */
	/* 3  7  11 15 */
	
	public Mat4F() {
		setIdentity();
	}

	public Mat4F(Mat4F matrix) {
		set(matrix);
	}
	
	public Mat4F(float... mat) {
		set(mat);
	}
	
	public Mat4F setElement(int index, float value) {
		mat[index] = value;
		return this;
	}
	
	public Mat4F set(Mat4F mat) {
		return set(mat.mat);
	}
	
	public Mat4F set(float... mat) {
		//this.mat = mat.clone();
		for (int i = 0; i < this.mat.length; i++)
			setElement(i, mat[i]);
		return this;
	}

	public Mat4F setIdentity() {
		for (int row = 0; row < 4; row++)
			for (int col = 0; col < 4; col++)
				setElement(row*4+col, row==col?1:0);
		return this;
	}
	
	public Mat4F multiply(float... mat) { //TODO: Error check?
		temp[0] = this.mat[0] * mat[0] + this.mat[4] * mat[1] + this.mat[8] * mat[2] + this.mat[12] * mat[3];
		temp[1] = this.mat[1] * mat[0] + this.mat[5] * mat[1] + this.mat[9] * mat[2] + this.mat[13] * mat[3];
		temp[2] = this.mat[2] * mat[0] + this.mat[6] * mat[1] + this.mat[10] * mat[2] + this.mat[14] * mat[3];
		temp[3] = this.mat[3] * mat[0] + this.mat[7] * mat[1] + this.mat[11] * mat[2] + this.mat[15] * mat[3];
		temp[4] = this.mat[0] * mat[4] + this.mat[4] * mat[5] + this.mat[8] * mat[6] + this.mat[12] * mat[7];
		temp[5] = this.mat[1] * mat[4] + this.mat[5] * mat[5] + this.mat[9] * mat[6] + this.mat[13] * mat[7];
		temp[6] = this.mat[2] * mat[4] + this.mat[6] * mat[5] + this.mat[10] * mat[6] + this.mat[14] * mat[7];
		temp[7] = this.mat[3] * mat[4] + this.mat[7] * mat[5] + this.mat[11] * mat[6] + this.mat[15] * mat[7];
		temp[8] = this.mat[0] * mat[8] + this.mat[4] * mat[9] + this.mat[8] * mat[10] + this.mat[12] * mat[11];
		temp[9] = this.mat[1] * mat[8] + this.mat[5] * mat[9] + this.mat[9] * mat[10] + this.mat[13] * mat[11];
		temp[10] = this.mat[2] * mat[8] + this.mat[6] * mat[9] + this.mat[10] * mat[10] + this.mat[14] * mat[11];
		temp[11] = this.mat[3] * mat[8] + this.mat[7] * mat[9] + this.mat[11] * mat[10] + this.mat[15] * mat[11];
		temp[12] = this.mat[0] * mat[12] + this.mat[4] * mat[13] + this.mat[8] * mat[14] + this.mat[12] * mat[15];
		temp[13] = this.mat[1] * mat[12] + this.mat[5] * mat[13] + this.mat[9] * mat[14] + this.mat[13] * mat[15];
		temp[14] = this.mat[2] * mat[12] + this.mat[6] * mat[13] + this.mat[10] * mat[14] + this.mat[14] * mat[15];
		temp[15] = this.mat[3] * mat[12] + this.mat[7] * mat[13] + this.mat[11] * mat[14] + this.mat[15] * mat[15];
		return this.set(temp);
	}
	
	public Mat4F multiply(Mat4F mat4F) {
		return this.multiply(mat4F.mat);
	}
	
	public Mat4F transpose() {
		swap(1, 4);
		swap(2, 8);
		swap(3, 12);
		swap(6, 9);
		swap(7, 13);
		swap(11, 14);
		return this;
	}
	
	private void swap(int index1, int index2) {
		float temp = mat[index1];
		mat[index1] = mat[index2];
		mat[index2] = temp;
	}
	
	public Mat4F invert() {
		float determinant = determinant();
		temp[0] = determinant3x3(mat[5], mat[6], mat[7], mat[9], mat[10], mat[11], mat[13], mat[14], mat[15])/determinant;
		temp[1] = -determinant3x3(mat[4], mat[6], mat[7], mat[8], mat[10], mat[11], mat[12], mat[14], mat[15])/determinant;
		temp[2] = determinant3x3(mat[4], mat[5], mat[7], mat[8], mat[9], mat[11], mat[12], mat[13], mat[15])/determinant;
		temp[3] = -determinant3x3(mat[4], mat[5], mat[6], mat[8], mat[9], mat[10], mat[12], mat[13], mat[14])/determinant;
		temp[4] = -determinant3x3(mat[1], mat[2], mat[3], mat[9], mat[10], mat[11], mat[13], mat[14], mat[15])/determinant;
		temp[5] = determinant3x3(mat[0], mat[2], mat[3], mat[8], mat[10], mat[11], mat[12], mat[14], mat[15])/determinant;
		temp[6] = -determinant3x3(mat[0], mat[1], mat[3], mat[8], mat[9], mat[11], mat[12], mat[13], mat[15])/determinant;
		temp[7] = determinant3x3(mat[0], mat[1], mat[2], mat[8], mat[9], mat[10], mat[12], mat[13], mat[14])/determinant;
		temp[8] = determinant3x3(mat[1], mat[2], mat[3], mat[5], mat[6], mat[7], mat[13], mat[14], mat[15])/determinant;
		temp[9] = -determinant3x3(mat[0], mat[2], mat[3], mat[4], mat[6], mat[7], mat[12], mat[14], mat[15])/determinant;
		temp[10] = determinant3x3(mat[0], mat[1], mat[3], mat[4], mat[5], mat[7], mat[12], mat[13], mat[15])/determinant;
		temp[11] = -determinant3x3(mat[0], mat[1], mat[2], mat[4], mat[5], mat[6], mat[12], mat[13], mat[14])/determinant;
		temp[12] = -determinant3x3(mat[1], mat[2], mat[3], mat[5], mat[6], mat[7], mat[9], mat[10], mat[11])/determinant;
		temp[13] = determinant3x3(mat[0], mat[2], mat[3], mat[4], mat[6], mat[7], mat[8], mat[10], mat[11])/determinant;
		temp[14] = -determinant3x3(mat[0], mat[1], mat[3], mat[4], mat[5], mat[7], mat[8], mat[9], mat[11])/determinant;
		temp[15] = determinant3x3(mat[0], mat[1], mat[2], mat[4], mat[5], mat[6], mat[8], mat[9], mat[10])/determinant;
		return this.set(temp);
	}
	
	public float determinant() {
		float sum = mat[0] * (mat[5] * mat[10] * mat[15] + mat[6] * mat[11] * mat[13] + mat[7] * mat[9] * mat[14] - (mat[7] * mat[10] * mat[13]) - (mat[5] * mat[11] * mat[14]) - (mat[6] * mat[9] * mat[15]));
		sum -= mat[1] * (mat[4] * mat[10] * mat[15] + mat[6] * mat[11] * mat[12] + mat[7] * mat[8] * mat[14] - (mat[7] * mat[10] * mat[12]) - (mat[4] * mat[11] * mat[14]) - (mat[6] * mat[8] * mat[15]));
		sum += mat[2] * (mat[4] * mat[9] * mat[15] + mat[5] * mat[11] * mat[12] + mat[7] * mat[8] * mat[13] - (mat[7] * mat[9] * mat[12]) - (mat[4] * mat[11] * mat[13]) - (mat[5] * mat[8] * mat[15]));
		sum -= mat[3] * (mat[4] * mat[9] * mat[14] + mat[5] * mat[10] * mat[12] + mat[6] * mat[8] * mat[13] - (mat[6] * mat[9] * mat[12]) - (mat[4] * mat[10] * mat[13]) - (mat[5] * mat[8] * mat[14]));
		return sum;
	}
	
	 private static float determinant3x3(float t00, float t01, float t02, float t10, float t11, float t12, float t20, float t21, float t22) {
		 return (t00 * (t11 * t22 - (t12 * t21)) + t01 * (t12 * t20 - (t10 * t22)) + t02 * (t10 * t21 - (t11 * t20)));
	 }

	public Mat4F translate(float x, float y, float z) {
		multiply(	1, 0, 0, 0,
					0, 1, 0, 0,
					0, 0, 1, 0,
					x, y, z, 1 );
		return this;
	}
	
	public Mat4F translate(Vec3F vec) {
		return translate(vec.x, vec.y, vec.z);
	}
	
	public void scale(float x, float y, float z) {
		multiply(	x, 0, 0, 0,
					0, y, 0, 0,
					0, 0, z, 0,
					0, 0, 0, 1 );
	}
	
	public Mat4F setTranslation(float x, float y, float z) {
		setElement(12, x);
		setElement(13, y);
		setElement(14, z);
		return this;
	}
	
	public Mat4F setTranslation(Vec3F vec) {
		return setTranslation(vec.x, vec.y, vec.z);
	}
	
	public Mat4F setRotation(Quat4F quat) {
		//this.rotation = quat.clone();
		float sqw = quat.w*quat.w;
		float sqx = quat.x*quat.x;
		float sqy = quat.y*quat.y;
		float sqz = quat.z*quat.z;

		// invs (inverse square length) is only required if quaternion is not already normalised
		//float invs = 1.0F; / (sqx + sqy + sqz + sqw);
		setElement(0, ( sqx - sqy - sqz + sqw));//*invs;
		setElement(5, (-sqx + sqy - sqz + sqw));//*invs;
		setElement(10, (-sqx - sqy + sqz + sqw));//*invs;

		float tmp1 = quat.x*quat.y;
		float tmp2 = quat.z*quat.w;
		setElement(4, 2.0F * (tmp1 + tmp2));//*invs;
		setElement(1, 2.0F * (tmp1 - tmp2));//*invs;

		tmp1 = quat.x*quat.z;
		tmp2 = quat.y*quat.w;
		setElement(8, 2.0F * (tmp1 - tmp2));//*invs;
		setElement(2, 2.0F * (tmp1 + tmp2));//*invs;
		tmp1 = quat.y*quat.z;
		tmp2 = quat.x*quat.w;
		setElement(9, 2.0F * (tmp1 + tmp2));//*invs;
		setElement(6, 2.0F * (tmp1 - tmp2));//*invs;
		return this;
	}

	@Override
	public Mat4F clone() {
		try {
			Mat4F newMat = (Mat4F)super.clone(); 
			newMat.mat = mat.clone();
			return newMat;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public String toString() {
		return "["+mat[0]+" "+mat[4]+" "+mat[8]+" "+mat[12]+"]\n" +
				"["+mat[1]+" "+mat[5]+" "+mat[9]+" "+mat[13]+"]\n" +
				"["+mat[2]+" "+mat[6]+" "+mat[10]+" "+mat[14]+"]\n" +
				"["+mat[3]+" "+mat[7]+" "+mat[11]+" "+mat[15]+"]";
	}
	
	public Mat4D toMat4D() {
		double[] temp = new double[mat.length];
		for (int i = 0; i < mat.length; i++)
			temp[i] = (float) mat[i];
		return new Mat4D(temp);
	}
	
	public FloatBuffer toFloatBuffer() {
		//TODO: Pass the FloatBuffer as a parameter.
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		buffer.clear();
		buffer.put(mat);
		buffer.flip();
		return buffer;
	}
	
	public Matrix4f toMatrix4f() {
		//TODO: DON'T DO THIS! No shortcuts!
		Matrix4f matrix = new Matrix4f(mat);
		matrix.transpose();
		return matrix;
	}
}
package com.metaplains.utils;

import java.nio.FloatBuffer;
import javax.vecmath.Matrix4d;
import org.lwjgl.BufferUtils;

/* NB: Matrix and all operations assume a column-major configuration! */
public class Mat4D implements Cloneable {

	private static double[] temp = new double[16];

	public double[] mat = new double[16];
	/* 0  4  8  12 */
	/* 1  5  9  13 */
	/* 2  6  10 14 */
	/* 3  7  11 15 */
	
	public Mat4D() {
		setIdentity();
	}

	public Mat4D(Mat4D matrix) {
		set(matrix);
	}
	
	public Mat4D(double[] mat) {
		set(mat);
	}
	
	public Mat4D setElement(int index, double value) {
		mat[index] = value;
		return this;
	}
	
	public Mat4D set(Mat4D mat) {
		return set(mat.mat);
	}
	
	public Mat4D set(double... mat) {
		//this.mat = mat.clone();
		for (int i = 0; i < this.mat.length; i++)
			setElement(i, mat[i]);
		return this;
	}

	public Mat4D setIdentity() {
		for (int row = 0; row < 4; row++)
			for (int col = 0; col < 4; col++)
				setElement(row*4+col, row==col?1:0);
		return this;
	}
	
	public Mat4D multiply(double... mat) { //TODO: Error check?
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
	
	public Mat4D multiply(Mat4D mat4F) {
		return this.multiply(mat4F.mat);
	}
	
	public Mat4D transpose() {
		swap(1, 4);
		swap(2, 8);
		swap(3, 12);
		swap(6, 9);
		swap(7, 13);
		swap(11, 14);
		return this;
	}
	
	private void swap(int index1, int index2) {
		double temp = mat[index1];
		mat[index1] = mat[index2];
		mat[index2] = temp;
	}

	public Mat4D translate(double x, double y, double z) {
		multiply(	1, 0, 0, 0,
					0, 1, 0, 0,
					0, 0, 1, 0,
					x, y, z, 1 );
		return this;
	}
	
	public Mat4D translate(Vec3F vec) {
		return translate(vec.x, vec.y, vec.z);
	}
	
	public void scale(double x, double y, double z) {
		multiply(	x, 0, 0, 0,
					0, y, 0, 0,
					0, 0, z, 0,
					0, 0, 0, 1 );
	}
	
	public Mat4D setTranslation(double x, double y, double z) {
		setElement(12, x);
		setElement(13, y);
		setElement(14, z);
		return this;
	}
	
	public Mat4D setTranslation(Vec3F vec) {
		return setTranslation(vec.x, vec.y, vec.z);
	}
	
	/*public Mat4D setRotation(Quat4F quat) {
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
	}*/

	@Override
	public Mat4D clone() {
		try {
			Mat4D newMat = (Mat4D)super.clone(); 
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
	
	public FloatBuffer toFloatBuffer() {
		//TODO: Pass the FloatBuffer as a parameter.
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		buffer.clear();
		for (double element : mat)
			buffer.put((float)element);
		buffer.flip();
		return buffer;
	}
	
	public Matrix4d toMatrix4d() {
		//TODO: DON'T DO THIS! No shortcuts!
		Matrix4d matrix = new Matrix4d(mat);
		matrix.transpose();
		return matrix;
	}
}
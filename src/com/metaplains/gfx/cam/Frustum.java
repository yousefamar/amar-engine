package com.metaplains.gfx.cam;

import static com.metaplains.gfx.GL.*;
import com.metaplains.utils.AABB;
import com.metaplains.utils.FastMath;
import com.metaplains.utils.Mat4F;
import com.metaplains.utils.Vec3F;

public class Frustum { //TODO: Next time: VBOs for terrain and have every quad surrounded by an AABB. AND THINK ABOUT QUADTREES!

	private float[][] frustum = new float[6][4];
	private Mat4F proj;
	private Mat4F view;
	private Mat4F modl;
	private Mat4F clip;

	/*private Camera camera;
	
	public Frustum(Camera camera) {
		this.camera = camera;
	}*/
	
	public void updateFrustum() {
		proj = getMatrix(PROJECTION);
		view = getMatrix(VIEW);
		modl = getMatrix(MODEL);

		//TODO: Write an article about these techniques.
		/* Combine the two matrices (multiply projection by modelview) */
		/*clip.put(modl.get(0) * proj.get(0) + modl.get(1) * proj.get(4) + modl.get(2) * proj.get(8) + modl.get(3) * proj.get(12))
			.put(modl.get(0) * proj.get(1) + modl.get(1) * proj.get(5) + modl.get(2) * proj.get(9) + modl.get(3) * proj.get(13))
			.put(modl.get(0) * proj.get(2) + modl.get(1) * proj.get(6) + modl.get(2) * proj.get(10) + modl.get(3) * proj.get(14))
			.put(modl.get(0) * proj.get(3) + modl.get(1) * proj.get(7) + modl.get(2) * proj.get(11) + modl.get(3) * proj.get(15))

			.put(modl.get(4) * proj.get(0) + modl.get(5) * proj.get(4) + modl.get(6) * proj.get(8) + modl.get(7) * proj.get(12))
			.put(modl.get(4) * proj.get(1) + modl.get(5) * proj.get(5) + modl.get(6) * proj.get(9) + modl.get(7) * proj.get(13))
			.put(modl.get(4) * proj.get(2) + modl.get(5) * proj.get(6) + modl.get(6) * proj.get(10) + modl.get(7) * proj.get(14))
			.put(modl.get(4) * proj.get(3) + modl.get(5) * proj.get(7) + modl.get(6) * proj.get(11) + modl.get(7) * proj.get(15))

			.put(modl.get(8) * proj.get(0) + modl.get(9) * proj.get(4) + modl.get(10) * proj.get(8) + modl.get(11) * proj.get(12))
			.put(modl.get(8) * proj.get(1) + modl.get(9) * proj.get(5) + modl.get(10) * proj.get(9) + modl.get(11) * proj.get(13))
			.put(modl.get(8) * proj.get(2) + modl.get(9) * proj.get(6) + modl.get(10) * proj.get(10) + modl.get(11) * proj.get(14))
			.put(modl.get(8) * proj.get(3) + modl.get(9) * proj.get(7) + modl.get(10) * proj.get(11) + modl.get(11) * proj.get(15))

			.put(modl.get(12) * proj.get(0) + modl.get(13) * proj.get(4) + modl.get(14) * proj.get(8) + modl.get(15) * proj.get(12))
			.put(modl.get(12) * proj.get(1) + modl.get(13) * proj.get(5) + modl.get(14) * proj.get(9) + modl.get(15) * proj.get(13))
			.put(modl.get(12) * proj.get(2) + modl.get(13) * proj.get(6) + modl.get(14) * proj.get(10) + modl.get(15) * proj.get(14))
			.put(modl.get(12) * proj.get(3) + modl.get(13) * proj.get(7) + modl.get(14) * proj.get(11) + modl.get(15) * proj.get(15));
		*/

		//Matrix4f clipMat = new Matrix4f();
		//Matrix4f.mul((Matrix4f)new Matrix4f().load(modl), (Matrix4f)new Matrix4f().load(proj), clipMat);

		/*pushMatrix();
		setMatrix(proj);
		multMatrix(view);
		multMatrix(modl);
		getFloat(MODEL, clip);
		popMatrix();
		*/
		
		clip = proj.multiply(view).multiply(modl);
		
		//Order: Right, left, bottom, top, far, near.
		for (int planeID = 0; planeID < 6; planeID++)
			extractPlane(clip.mat, planeID);
		
		/*frustEye.setVec(camera.eyeVec);
		topLeft = Geom.cross(new Vec3F(frustum[3][0], frustum[3][1], frustum[3][2]), new Vec3F(frustum[1][0], frustum[1][1], frustum[1][2])).normalise().scale(100.0F);
		System.out.println(topLeft);
		topRight = Geom.cross(new Vec3F(frustum[3][0], frustum[3][1], frustum[3][2]), new Vec3F(frustum[0][0], frustum[0][1], frustum[0][2])).normalise().scale(100.0F);
		System.out.println(topRight);
		bottomLeft = Geom.cross(new Vec3F(frustum[2][0], frustum[2][1], frustum[2][2]), new Vec3F(frustum[1][0], frustum[1][1], frustum[1][2])).normalise().scale(100.0F);
		System.out.println(bottomLeft);
		bottomRight = Geom.cross(new Vec3F(frustum[2][0], frustum[2][1], frustum[2][2]), new Vec3F(frustum[0][0], frustum[0][1], frustum[0][2])).normalise().scale(100.0F);
		System.out.println(bottomRight);*/
	}

	private void extractPlane(float[] clipMat, int planeID) {
		int offset = planeID/2;
		float scale = planeID%2==0?1.0F:-1.0F;
		frustum[planeID][0] = clipMat[3] + scale*clipMat[offset + 0];
		frustum[planeID][1] = clipMat[7] + scale*clipMat[offset + 4];
		frustum[planeID][2] = clipMat[11] + scale*clipMat[offset + 8];
		frustum[planeID][3] = clipMat[15] + scale*clipMat[offset + 12];

		float mag = (float) FastMath.sqrt(frustum[planeID][0] * frustum[planeID][0] + frustum[planeID][1] * frustum[planeID][1] + frustum[planeID][2] * frustum[0][2]);
		frustum[planeID][0] /= mag;
		frustum[planeID][1] /= mag;
		frustum[planeID][2] /= mag;
		frustum[planeID][3] /= mag;
	}

	public boolean isPointInFrustum(Vec3F vec) {
		return isPointInFrustum(vec.x, vec.y, vec.z);
	}
	
	public boolean isPointInFrustum(float x, float y, float z) {
		for (int planeID = 0; planeID < 6; planeID++)
			if (isPointOverPlane(planeID, x, y, z))
				return false;
		return true;
	}
	
	public boolean isPointOverPlane(int planeID, float x, float y, float z) {
		//There's no point in using Geom and Plane3D as they use a different equation kind
		//and converting between them would take unnecessarily longer.
		return (frustum[planeID][0] * x + frustum[planeID][1] * y + frustum[planeID][2] * z + frustum[planeID][3] < 0);
	}
	
	/**
	 * @return Returns 0 if the sphere is not contained in or intersecting with the frustum or else the distance from the near plane.
	 */
	public float isSphereInFrustum(ClippingSphere sphere) {
	   float d = 0;
	   for(int planeID = 0; planeID < 6; planeID++)
	      if((d = frustum[planeID][0] * sphere.x + frustum[planeID][1] * sphere.y + frustum[planeID][2] * sphere.z + frustum[planeID][3]) < -sphere.radius)
	         return 0;
	   return d + sphere.radius;
	}
	
	public boolean isAABBInFrustum(AABB aabb) {
		for (int planeID = 0; planeID < 6; planeID++) {
			if (isPointOverPlane(planeID, aabb.minX, aabb.minY, aabb.minZ)
					&& isPointOverPlane(planeID, aabb.maxX, aabb.minY, aabb.minZ)
					&& isPointOverPlane(planeID, aabb.minX, aabb.maxY, aabb.minZ)
					&& isPointOverPlane(planeID, aabb.maxX, aabb.maxY, aabb.minZ)
					&& isPointOverPlane(planeID, aabb.minX, aabb.minY, aabb.maxZ)
					&& isPointOverPlane(planeID, aabb.maxX, aabb.minY, aabb.maxZ)
					&& isPointOverPlane(planeID, aabb.minX, aabb.maxY, aabb.maxZ)
					&& isPointOverPlane(planeID, aabb.maxX, aabb.maxY, aabb.maxZ))
				return false;
		}
		return true;
	}
	
	/*private Vec3F frustEye = new Vec3F(0, 0, 0);
	private Vec3F topLeft = Geom.cross(new Vec3F(frustum[3][0], frustum[3][1], frustum[3][2]), new Vec3F(frustum[1][0], frustum[1][1], frustum[1][2])).normalise().scale(10.0F);
	private Vec3F topRight = Geom.cross(new Vec3F(frustum[3][0], frustum[3][1], frustum[3][2]), new Vec3F(frustum[0][0], frustum[0][1], frustum[0][2])).normalise().scale(10.0F);
	private Vec3F bottomLeft = Geom.cross(new Vec3F(frustum[2][0], frustum[2][1], frustum[2][2]), new Vec3F(frustum[1][0], frustum[1][1], frustum[1][2])).normalise().scale(10.0F);
	private Vec3F bottomRight = Geom.cross(new Vec3F(frustum[2][0], frustum[2][1], frustum[2][2]), new Vec3F(frustum[0][0], frustum[0][1], frustum[0][2])).normalise().scale(10.0F);*/
	public void render() {
		/*glPushMatrix();
		glTranslatef(frustEye.x, frustEye.y, frustEye.z);
		glColor3f(0, 1, 1);
		glBegin(GL_LINES);
		glVertex3f(0, 0, 0);
		glVertex3f(topLeft.x, topLeft.y, topLeft.z);
		glVertex3f(0, 0, 0);
		glVertex3f(topRight.x, topRight.y, topRight.z);
		glVertex3f(0, 0, 0);
		glVertex3f(bottomLeft.x, bottomLeft.y, bottomLeft.z);
		glVertex3f(0, 0, 0);
		glVertex3f(bottomRight.x, bottomRight.y, bottomRight.z);
		glEnd();
		glPopMatrix();*/
	}
}
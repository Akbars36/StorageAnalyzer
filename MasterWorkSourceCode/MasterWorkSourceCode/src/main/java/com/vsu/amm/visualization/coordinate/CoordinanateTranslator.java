package com.vsu.amm.visualization.coordinate;

import java.awt.geom.Point2D;

import com.vsu.amm.visualization.utils.MatrixUtils;

public class CoordinanateTranslator {
	private static final double ANGLE_ROTATE_BY_Z = -45;
	private static final double ANGLE_ROTATE_BY_Y = 35.26438968275464;
	double[][] tranlationMatrix;

	public CoordinanateTranslator(double size) {
		double[][] rotateOnY = MatrixUtils.rotateOnAngleByY(ANGLE_ROTATE_BY_Y);
		double[][] rotateOnZ = MatrixUtils.rotateOnAngleByZ(ANGLE_ROTATE_BY_Z);
		double[][] move = MatrixUtils.moveOnXYZ(size*Math.sqrt(2.0)/2, 0, 0);
		double[][] tmp = MatrixUtils.multiplyByMatrix(rotateOnY, rotateOnZ);
		tranlationMatrix = MatrixUtils.multiplyByMatrix(tmp, move);
	}

	public Point3DInIRSCoords translate(Point2D point) {
		double[][] param = { { 0, point.getX(), point.getY(), 1 } };
		double[][] result = MatrixUtils.multiplyByMatrix(param,
				tranlationMatrix);
		int insertCount=(int)  Math.round(result[0][0]);
		int selectCount=(int)  Math.round(result[0][1]);
		int removeCount=(int)  Math.round(result[0][2]);
		if(insertCount<0){
			insertCount=0;
			System.out.print("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		}
		
		Point3DInIRSCoords res = new Point3DInIRSCoords(insertCount,selectCount,removeCount);
		return res;

	}
}

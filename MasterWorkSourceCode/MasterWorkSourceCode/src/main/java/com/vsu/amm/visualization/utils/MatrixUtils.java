package com.vsu.amm.visualization.utils;

import java.awt.geom.Point2D;

import com.vsu.amm.visualization.coordinate.CoordinanateTranslator;
import com.vsu.amm.visualization.coordinate.Point3DInIRSCoords;

public class MatrixUtils {

	/**
	 * Matrix multiplication method.
	 * 
	 * @param m1
	 *            Multiplicand
	 * @param m2
	 *            Multiplier
	 * @return Product
	 */
	public static double[][] multiplyByMatrix(double[][] m1, double[][] m2) {
		int m1ColLength = m1[0].length; // m1 columns length
		int m2RowLength = m2.length; // m2 rows length
		if (m1ColLength != m2RowLength)
			return null; // matrix multiplication is not possible
		int mRRowLength = m1.length; // m result rows length
		int mRColLength = m2[0].length; // m result columns length
		double[][] mResult = new double[mRRowLength][mRColLength];
		for (int i = 0; i < mRRowLength; i++) { // rows from m1
			for (int j = 0; j < mRColLength; j++) { // columns from m2
				for (int k = 0; k < m1ColLength; k++) { // columns from m1
					mResult[i][j] += m1[i][k] * m2[k][j];
				}
			}
		}
		return mResult;
	}

	public static double[][] rotateOnAngleByZ(double alpha) // radians
	{
		double cosA = (double) Math.cos(Math.toRadians(alpha));
		double sinA = (double) Math.sin(Math.toRadians(alpha));

		double[][] matr = { 
				{ cosA, -sinA, 0.0f, 0.0f }, 
				{ sinA, cosA, 0.0f, 0.0f },
				{ 0.0f, 0.0f, 1.0f, 0.0f },
				{ 0.0f, 0.0f, 0.0f, 1.0f } };
		return matr;
	}

	public static double[][] rotateOnAngleByY(double alpha) // radians
	{
		double cosA = (double) Math.cos(Math.toRadians(alpha));
		double sinA = (double) Math.sin(Math.toRadians(alpha));
		double[][] matr = { 
				{ cosA, 0.0f, sinA, 0.0f }, 
				{ 0.0f, 1.0f, 0.0f , 0.0f},
				{ -sinA, 0.0f, cosA , 0.0f},
				{ 0.0f, 0.0f, 0.0f, 1.0f } };
		return matr;
	}
	
	public static double[][] moveOnXYZ(double x,double y,double z) // radians
	{
		double[][] matr = new double[][] { 
				{ 1, 0, 0,0 }, 
				{ 0, 1, 0,0 }, 
				{ 0, 0, 1,0 },
				{ x, y, z, 1 } };
		return matr;
	}

	public static String toString(double[][] m) {
		String result = "";
		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m[i].length; j++) {
				result += String.format("%11.3f", m[i][j]);
			}
			result += "\n";
		}
		return result;
	}
	
	

	public static void main(String[] args) {
		// #1
		CoordinanateTranslator tr=new CoordinanateTranslator(10);
		Point2D p=new Point2D.Double(7,0);
		Point3DInIRSCoords r=tr.translate(p);
		 System.out.println(r.getInsertCoord()+"    "+r.getRemoveCoord()+"    "+r.getSelectCoord());
	}
}

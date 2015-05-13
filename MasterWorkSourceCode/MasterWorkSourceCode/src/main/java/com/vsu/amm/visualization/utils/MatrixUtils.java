package com.vsu.amm.visualization.utils;

/**
 * Вспомогательный класс для работы с матрицами
 * 
 * @author Potapov Danila
 *
 */
public class MatrixUtils {

	/**
	 * Метод для перемножения матриц
	 * 
	 * @param firstMatrix
	 *            матрица, которую умножают
	 * 
	 * @param secondMatrix
	 *            матрица, на которую умножают
	 * 
	 * @return результат умножения матриц или null, если размерности матриц не
	 *         совпадают
	 */
	public static double[][] multiplyByMatrix(double[][] firstMatrix,
			double[][] secondMatrix) {
		int m1ColLength = firstMatrix[0].length; // m1 columns length
		int m2RowLength = secondMatrix.length; // m2 rows length
		if (m1ColLength != m2RowLength)
			return null; // matrix multiplication is not possible
		int mRRowLength = firstMatrix.length; // m result rows length
		int mRColLength = secondMatrix[0].length; // m result columns length
		double[][] mResult = new double[mRRowLength][mRColLength];
		for (int i = 0; i < mRRowLength; i++) { // rows from m1
			for (int j = 0; j < mRColLength; j++) { // columns from m2
				for (int k = 0; k < m1ColLength; k++) { // columns from m1
					mResult[i][j] += firstMatrix[i][k] * secondMatrix[k][j];
				}
			}
		}
		return mResult;
	}

	/**
	 * Метод для получения матрицы поворота на угол alpha по оси Z
	 * 
	 * @param alpha
	 *            угол, на который производится поворот
	 * @return матрицу поворота
	 */
	public static double[][] rotateOnAngleByZ(double alpha) {
		double cosA = (double) Math.cos(Math.toRadians(alpha));
		double sinA = (double) Math.sin(Math.toRadians(alpha));

		double[][] matr = { { cosA, -sinA, 0.0f, 0.0f },
				{ sinA, cosA, 0.0f, 0.0f }, { 0.0f, 0.0f, 1.0f, 0.0f },
				{ 0.0f, 0.0f, 0.0f, 1.0f } };
		return matr;
	}

	/**
	 * Метод для получения матрицы поворота на угол alpha по оси Y
	 * 
	 * @param alpha
	 *            угол, на который производится поворот
	 * @return матрицу поворота
	 */
	public static double[][] rotateOnAngleByY(double alpha) // radians
	{
		double cosA = (double) Math.cos(Math.toRadians(alpha));
		double sinA = (double) Math.sin(Math.toRadians(alpha));
		double[][] matr = { { cosA, 0.0f, sinA, 0.0f },
				{ 0.0f, 1.0f, 0.0f, 0.0f }, { -sinA, 0.0f, cosA, 0.0f },
				{ 0.0f, 0.0f, 0.0f, 1.0f } };
		return matr;
	}

	/**
	 * Метод для получения матрицы сдвига системы координат на (x,y,z)
	 * 
	 * @param x
	 *            сдвиг по оси Х
	 * @param y
	 *            сдвиг по оси У
	 * @param z
	 *            сдвиг по оси Z
	 * @return матрицу сдвига
	 */
	public static double[][] moveOnXYZ(double x, double y, double z) // radians
	{
		double[][] matr = new double[][] { { 1, 0, 0, 0 }, { 0, 1, 0, 0 },
				{ 0, 0, 1, 0 }, { x, y, z, 1 } };
		return matr;
	}
}

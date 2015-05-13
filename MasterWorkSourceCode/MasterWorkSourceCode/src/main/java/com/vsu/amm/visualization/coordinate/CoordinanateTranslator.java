package com.vsu.amm.visualization.coordinate;

import java.awt.geom.Point2D;

import com.vsu.amm.visualization.utils.MatrixUtils;

/**
 * Класс для перевода координат из 2х мерных координат изображения(x,y) в
 * целочисленные координаты 3х мерные (insert,select,remove)
 * 
 * @author Potapov Danila
 *
 */
public class CoordinanateTranslator {
	/**
	 * Угол поворота по оси Z
	 */
	private static final double ANGLE_ROTATE_BY_Z = -45;
	/**
	 * Угол поворота по оси Y
	 */
	private static final double ANGLE_ROTATE_BY_Y = 35.26438968275464;

	private static final double COEFFICENT_FOR_MOVE = Math.sqrt(2.0) / 2;
	/**
	 * Матрица перевода координат
	 */
	double[][] tranlationMatrix;

	/**
	 * Конструктор для создания транслятора
	 * 
	 * @param size
	 *            -
	 */
	public CoordinanateTranslator(double size) {
		// создаем матрицу поворота по оси Y
		double[][] rotateOnY = MatrixUtils.rotateOnAngleByY(ANGLE_ROTATE_BY_Y);
		// создаем матрицу поворота по оси Z
		double[][] rotateOnZ = MatrixUtils.rotateOnAngleByZ(ANGLE_ROTATE_BY_Z);
		// создаем матрицу сдвига по оси Х(умножаем размер на синус 45 для
		// получения длины отрезка (0,maxInsertCoordinat))
		double[][] move = MatrixUtils.moveOnXYZ(size * COEFFICENT_FOR_MOVE, 0,
				0);
		double[][] tmp = MatrixUtils.multiplyByMatrix(rotateOnY, rotateOnZ);
		// Получаем матрицу перевода путем умножения
		tranlationMatrix = MatrixUtils.multiplyByMatrix(tmp, move);
	}

	/**
	 * Функция перевода координат из 2х мерных координат изображения(x,y) в
	 * целочисленные координаты 3х мерные (insert,select,remove)
	 * 
	 * @param point
	 *            точка (х,у)
	 * @return точку в 3х мерных координатах
	 */
	public Point3DInIRSCoords translate(Point2D point) {
		// Создаем вектор в старых координатах
		double[][] param = { { 0, point.getX(), point.getY(), 1 } };
		// Получаем координаты в 3х мерном прострасранстве
		double[][] result = MatrixUtils.multiplyByMatrix(param,
				tranlationMatrix);
		int insertCount = (int) Math.round(result[0][0]);
		int selectCount = (int) Math.round(result[0][1]);
		int removeCount = (int) Math.round(result[0][2]);
		Point3DInIRSCoords res = new Point3DInIRSCoords(insertCount,
				selectCount, removeCount);
		return res;

	}
}

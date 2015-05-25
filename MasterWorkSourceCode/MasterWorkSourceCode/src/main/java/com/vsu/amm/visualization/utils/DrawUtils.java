package com.vsu.amm.visualization.utils;

import java.awt.Color;
import java.awt.geom.Point2D;

import com.vsu.amm.visualization.coordinate.CoordinanateTranslator;
import com.vsu.amm.visualization.coordinate.Point3DInIRSCoords;

/**
 * Класс, который содержит вспомогательные функции для построения изображения
 * 
 * @author Potapov Danila
 *
 */
public class DrawUtils {
	/**
	 * Функция, для определения по какую сторону от прямой лежит точка
	 * 
	 * @param startX
	 *            х координата начала прямой
	 * @param startY
	 *            у координата начала прямой
	 * @param finishX
	 *            х координата конца прямой
	 * @param finishY
	 *            у координата конца прямой
	 * @param x
	 *            х координата тестируемой точки
	 * @param y
	 *            у координата тестируемой точки
	 * @return число, по которому можно определить с какой стороны от прямой
	 *         расположена точка
	 */
	public static float side(float startX, float startY, float finishX,
			float finishY, float x, float y) {
		return (finishY - startY) * (x - startX) + (-finishX + startX)
				* (y - startY);
	}

	/**
	 * Функция для проверки принадлежности точки треугольнику ABC
	 * 
	 * @param xA
	 *            x координата точки A
	 * @param yA
	 *            у координата точки A
	 * @param xB
	 *            x координата точки B
	 * @param yB
	 *            у координата точки B
	 * @param xC
	 *            x координата точки C
	 * @param yC
	 *            у координата точки C
	 * @param x
	 *            х координата тестируемой точки
	 * @param y
	 *            у координата тестируемой точки
	 * @return true если точка лежит в треугольнике
	 */
	public static Boolean pointInTriangle(float xA, float yA, float xB,
			float yB, float xC, float yC, float x, float y) {
		// Точка должна быть по одну сторону от всех прямых
		Boolean checkSide1 = side(xA, yA, xB, yB, x, y) >= 0;
		Boolean checkSide2 = side(xB, yB, xC, yC, x, y) >= 0;
		Boolean checkSide3 = side(xC, yC, xA, yA, x, y) >= 0;
		return checkSide1 && checkSide2 && checkSide3;
	}

	/**
	 * Функция для проверки принадлежности точки сторонам треугольника ABC
	 * 
	 * @param xA
	 *            x координата точки A
	 * @param yA
	 *            у координата точки A
	 * @param xB
	 *            x координата точки B
	 * @param yB
	 *            у координата точки B
	 * @param xC
	 *            x координата точки C
	 * @param yC
	 *            у координата точки C
	 * @param x
	 *            х координата тестируемой точки
	 * @param y
	 *            у координата тестируемой точки
	 * @return true если точка лежит на стороне треугольника
	 */
	public static Boolean pointInTriangleSide(float xA, float yA, float xB,
			float yB, float xC, float yC, float x, float y) {
		// Точка должна быть на стороне
		Boolean checkSide1 = side(xA, yA, xB, yB, x, y) <= 50
				&& side(xA, yA, xB, yB, x, y) >= 0;
		Boolean checkSide2 = side(xB, yB, xC, yC, x, y) <= 50
				&& side(xB, yB, xC, yC, x, y) >= 0;
		return checkSide1 || checkSide2;
	}

	/**
	 * Функция получения цвета c использованием линейного градиента
	 * 
	 * @param ratio
	 *            коэффицент для которого получаем цвет(должен быть из диапазона
	 *            [0,1]))
	 * @param colorMin
	 *            цвет от которого идет заливка
	 * @param colorMax
	 *            цвет к которому идет заливка
	 * @return новый цвет полученный c использованием линейного градиента
	 */
	public static Color getColorByLinearGradient(double ratio, Color colorMin,
			Color colorMax) {
		if (ratio > 1)
			ratio = 1;
		if (ratio < 0)
			ratio = 0;
		// Получаем каждую компоненту нового цвета по формуле
		int red = (int) (colorMax.getRed() * ratio + colorMin.getRed()
				* (1 - ratio));
		int green = (int) (colorMax.getGreen() * ratio + colorMin.getGreen()
				* (1 - ratio));
		int blue = (int) (colorMax.getBlue() * ratio + colorMin.getBlue()
				* (1 - ratio));
		Color c = new Color(red, green, blue);
		return c;
	}

	/**
	 * Метод вычисления значения функции f(x,y)=ax+by+c
	 * 
	 * @param w
	 *            коэффиценты а,b,c
	 * @param x
	 *            значение х
	 * @param y
	 *            значение у
	 * @return значение функции в точке (х,у)
	 */
	public static double evalLineFunction(double[] w, double x, double y) {
		double res = w[0] * (x) + w[1] * (y) + w[2];
		return res;
	}

	/**
	 * Метод вычисления значения функция f(x)=ax+b
	 * 
	 * @param w
	 *            коэффиценты a,b
	 * @param x
	 *            значение х
	 * @return значение функции в точке х
	 */
	public static double evalLineFunctionYValue(double[] w, double x) {
		double res = w[1] != 0 ? (w[0] / w[1] * (x) + w[2] / w[1]) : Double.NaN;
		return res;
	}

	/**
	 * Метод вычисления коэффицентов плоскости Ax+By+Cz+D=0 по трем точкам
	 * 
	 * @param p1
	 *            первая точка
	 * @param p2
	 *            вторая точка
	 * @param p3
	 *            третья точка
	 * @param size
	 *            размер изображения
	 * @return массив коэффицентов
	 */
	public static double[] getPlaneCoeffsByThreePoints(Point3DInIRSCoords r1,
			Point3DInIRSCoords r2, Point3DInIRSCoords r3) {
		double[] result = new double[4];
		// коэффицент при Insert
		result[0] = r2.getRemoveCoord() * r3.getSelectCoord()
				+ r1.getRemoveCoord() * r2.getSelectCoord()
				+ r1.getSelectCoord() * r3.getRemoveCoord()
				- r1.getSelectCoord() * r2.getRemoveCoord()
				- r1.getRemoveCoord() * r3.getSelectCoord()
				- r2.getSelectCoord() * r3.getRemoveCoord();
		// коэффицент при Remove
		result[1] = r2.getSelectCoord() * r3.getInsertCoord()
				+ r1.getInsertCoord() * r3.getSelectCoord()
				+ r1.getSelectCoord() * r2.getInsertCoord()
				- r1.getSelectCoord() * r3.getInsertCoord()
				- r2.getInsertCoord() * r3.getSelectCoord()
				- r1.getInsertCoord() * r2.getSelectCoord();
		// коэффицент при Select
		result[2] = r1.getInsertCoord() * r2.getRemoveCoord()
				+ r1.getRemoveCoord() * r3.getInsertCoord()
				+ r2.getInsertCoord() * r3.getRemoveCoord()
				- r2.getRemoveCoord() * r3.getInsertCoord()
				- r1.getRemoveCoord() * r2.getInsertCoord()
				- r1.getInsertCoord() * r3.getRemoveCoord();
		// свободный коэффицент
		result[3] = -(r1.getInsertCoord() * r2.getRemoveCoord()
				* r3.getSelectCoord() + r1.getRemoveCoord()
				* r3.getInsertCoord() * r2.getSelectCoord()
				+ r2.getInsertCoord() * r3.getRemoveCoord()
				* r1.getSelectCoord() - r2.getRemoveCoord()
				* r3.getInsertCoord() * r1.getSelectCoord()
				- r1.getRemoveCoord() * r2.getInsertCoord()
				* r3.getSelectCoord() - r1.getInsertCoord()
				* r3.getRemoveCoord() * r2.getSelectCoord());
		return result;
	}
}

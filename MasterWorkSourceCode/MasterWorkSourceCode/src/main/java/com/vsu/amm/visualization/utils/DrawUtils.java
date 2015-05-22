package com.vsu.amm.visualization.utils;

import java.awt.Color;

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
	
	public static Boolean pointInTriangleSide(float xA, float yA, float xB,
			float yB, float xC, float yC, float x, float y) {
		System.out.println(side(xA, yA, xB, yB, x, y));
		System.out.println(side(xB, yB, xC, yC, x, y));
		System.out.println(side(xC, yC, xA, yA, x, y));
		// Точка должна быть по одну сторону от всех прямых
		Boolean checkSide1 = side(xA, yA, xB, yB, x, y) <= 50&&side(xA, yA, xB, yB, x, y)>=0;
		Boolean checkSide2 = side(xB, yB, xC, yC, x, y) <= 50&& side(xB, yB, xC, yC, x, y)>=0;
		Boolean checkSide3 = side(xC, yC, xA, yA, x, y) <=50&&side(xC, yC, xA, yA, x, y)>=0;
		return checkSide1 || checkSide2 || checkSide3;
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
}

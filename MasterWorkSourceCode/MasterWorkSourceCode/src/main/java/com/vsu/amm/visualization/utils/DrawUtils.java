package com.vsu.amm.visualization.utils;

import java.awt.Color;

public class DrawUtils {
	public static float side(float x1, float y1, float x2, float y2, float x,
			float y) {
		return (y2 - y1) * (x - x1) + (-x2 + x1) * (y - y1);
	}

	public static Boolean pointInTriangle(float x1, float y1, float x2,
			float y2, float x3, float y3, float x, float y) {
		Boolean checkSide1 = side(x1, y1, x2, y2, x, y) >= 0;
		Boolean checkSide2 = side(x2, y2, x3, y3, x, y) >= 0;
		Boolean checkSide3 = side(x3, y3, x1, y1, x, y) >= 0;
		return checkSide1 && checkSide2 && checkSide3;
	}

	public static Color getLinearGradient(double ratio, Color c1, Color c2) {
		if (ratio > 1)
			ratio = 1;
		int red = (int) (c2.getRed() * ratio + c1.getRed() * (1 - ratio));
		int green = (int) (c2.getGreen() * ratio + c1.getGreen() * (1 - ratio));
		int blue = (int) (c2.getBlue() * ratio + c1.getBlue() * (1 - ratio));
		Color c = new Color(red, green, blue);
		return c;
	}
}

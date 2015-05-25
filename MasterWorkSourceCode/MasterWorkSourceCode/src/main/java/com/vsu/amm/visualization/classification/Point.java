package com.vsu.amm.visualization.classification;

import java.awt.geom.Point2D;
import java.util.List;

/**
 * Класс для хранение координат и значения в точке
 * 
 * @author Potapov Danila
 *
 */
public class Point {
	// координата по оси х
	Double x;
	// координата по оси у
	Double y;
	// значение в точке
	Integer value;

	public Double getX() {
		return x;
	}

	public void setX(Double x) {
		this.x = x;
	}

	public Double getY() {
		return y;
	}

	public void setY(Double y) {
		this.y = y;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public Point(Double x, Double y, Integer value) {
		super();
		this.x = x;
		this.y = y;
		this.value = value;
	}

	@Override
	public String toString() {
		return "Point [x=" + x + ", y=" + y + ", value=" + value + "]";
	}

	/**
	 * Метод получения точки по координатам и набору возможных значений
	 * 
	 * @param point
	 *            координаты точки
	 * @param vals
	 *            возможные значения
	 * @return
	 */
	public Point(Point2D point, List<Integer> vals) {
		Integer value = vals.get(0);
		if (vals.size() != 1) {
			int minInd = 0;
			int min = vals.get(minInd);
			for (int i = 0; i < vals.size(); i++) {
				if (vals.get(i) < min) {
					minInd = i;
					min = vals.get(i);
				}
			}
			value = minInd;
		}
		this.x=point.getX();
		this.y=point.getY();
		this.value=value;
	}

}

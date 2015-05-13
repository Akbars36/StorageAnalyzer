package com.vsu.amm.visualization.data;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;

/**
 * Вспомогательный класс, на основе которого строится изображение
 * 
 * @author Potapov Danila
 *
 */
public class ImageData {
	/**
	 * Минимальное количество операций
	 */
	Integer min;
	/**
	 * Максимальное количество операций
	 */
	Integer max;
	/**
	 * Карта, которая содержит координаты точки для отрисовки и соответствующие
	 * ей значения количества операций
	 */
	Map<Point2D, List<Integer>> data;

	/**
	 * Конструктор
	 * 
	 * @param min
	 *            Минимальное количество операций
	 * @param max
	 *            Максимальное количество операций
	 * @param data
	 *            Карта, которая содержит координаты точки для отрисовки и
	 *            соответствующие ей значения количества операций
	 */
	public ImageData(Integer min, Integer max, Map<Point2D, List<Integer>> data) {
		super();
		this.min = min;
		this.max = max;
		this.data = data;
	}

	public Integer getMin() {
		return min;
	}

	public void setMin(Integer min) {
		this.min = min;
	}

	public Integer getMax() {
		return max;
	}

	public void setMax(Integer max) {
		this.max = max;
	}

	public Map<Point2D, List<Integer>> getData() {
		return data;
	}

	public void setData(Map<Point2D, List<Integer>> data) {
		this.data = data;
	}

}

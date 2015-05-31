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
	Long min;
	/**
	 * Максимальное количество операций
	 */
	Long max;
	/**
	 * Карта, которая содержит координаты точки для отрисовки и соответствующие
	 * ей значения количества операций
	 */
	Map<Point2D, List<Long>> data;

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
	public ImageData(Long min, Long max, Map<Point2D, List<Long>> data) {
		super();
		this.min = min;
		this.max = max;
		this.data = data;
	}

	public Long getMin() {
		return min;
	}

	public void setMin(Long min) {
		this.min = min;
	}

	public Long getMax() {
		return max;
	}

	public void setMax(Long max) {
		this.max = max;
	}

	public Map<Point2D, List<Long>> getData() {
		return data;
	}

	public void setData(Map<Point2D, List<Long>> data) {
		this.data = data;
	}

}

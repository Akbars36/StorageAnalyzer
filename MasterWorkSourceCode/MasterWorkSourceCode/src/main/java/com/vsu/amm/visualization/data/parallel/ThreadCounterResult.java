package com.vsu.amm.visualization.data.parallel;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;

/**
 * Результат работы потока по подсчету количества операций
 * 
 * @author Potapov Danila
 *
 */
public class ThreadCounterResult {
	/**
	 * Карта из точек и их значений посчитанная текущим потоком
	 */
	Map<Point2D, List<Long>> result;
	/**
	 * Минимум найденный текущим потоком
	 */
	Long curMin;
	/**
	 * Максимум найденный текущим потоком
	 */
	Long curMax;

	/**
	 * Конструктор
	 * 
	 * @param result
	 *            Карта из точек и их значений посчитанная текущим потоком
	 * @param curMin
	 *            Минимум найденный текущим потоком
	 * @param curMax
	 *            Максимум найденный текущим потоком
	 */
	public ThreadCounterResult(Map<Point2D, List<Long>> result,
			Long curMin, Long curMax) {
		super();
		this.result = result;
		this.curMin = curMin;
		this.curMax = curMax;
	}

	public Map<Point2D, List<Long>> getResult() {
		return result;
	}

	public void setResult(Map<Point2D, List<Long>> result) {
		this.result = result;
	}

	public Long getCurMin() {
		return curMin;
	}

	public void setCurMin(Long curMin) {
		this.curMin = curMin;
	}

	public Long getCurMax() {
		return curMax;
	}

	public void setCurMax(Long curMax) {
		this.curMax = curMax;
	}

}

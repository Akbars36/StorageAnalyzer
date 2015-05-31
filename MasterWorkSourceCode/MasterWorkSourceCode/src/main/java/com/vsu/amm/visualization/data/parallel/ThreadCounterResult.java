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
	Map<Point2D, List<Integer>> result;
	/**
	 * Минимум найденный текущим потоком
	 */
	Integer curMin;
	/**
	 * Максимум найденный текущим потоком
	 */
	Integer curMax;

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
	public ThreadCounterResult(Map<Point2D, List<Integer>> result,
			Integer curMin, Integer curMax) {
		super();
		this.result = result;
		this.curMin = curMin;
		this.curMax = curMax;
	}

	public Map<Point2D, List<Integer>> getResult() {
		return result;
	}

	public void setResult(Map<Point2D, List<Integer>> result) {
		this.result = result;
	}

	public Integer getCurMin() {
		return curMin;
	}

	public void setCurMin(Integer curMin) {
		this.curMin = curMin;
	}

	public Integer getCurMax() {
		return curMax;
	}

	public void setCurMax(Integer curMax) {
		this.curMax = curMax;
	}

}

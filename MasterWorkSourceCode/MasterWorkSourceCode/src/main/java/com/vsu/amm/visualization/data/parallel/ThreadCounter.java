package com.vsu.amm.visualization.data.parallel;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.vsu.amm.data.storage.IDataStorage;
import com.vsu.amm.stat.SimpleCounterSet;
import com.vsu.amm.visualization.Vizualizator;
import com.vsu.amm.visualization.coordinate.CoordinanateTranslator;
import com.vsu.amm.visualization.coordinate.Point3DInIRSCoords;
import com.vsu.amm.visualization.data.DataGenerator;

/**
 * Класс реализующий вызов в отдельном потоке
 * 
 * @author Potapov Danila
 *
 */
public class ThreadCounter implements Callable<ThreadCounterResult> {
	/**
	 * Точки, которые необходимо обработать данному потоку
	 */
	List<Point2D> points;
	/**
	 * Ссылка на транслятор координат
	 */
	CoordinanateTranslator transl;
	/**
	 * Логгер
	 */
	static Logger log = Logger.getLogger(Vizualizator.class.getName());
	/**
	 * Список тестируемых хранилищ
	 */
	List<IDataStorage> storages;

	/**
	 * Конструктор
	 * 
	 * @param points
	 *            список точек для тестирования
	 * @param transl
	 *            ссылка на транслятор координат
	 * @param storages
	 *            список хранилищ для тестирования
	 */
	public ThreadCounter(List<Point2D> points, CoordinanateTranslator transl,
			List<IDataStorage> storages) {
		this.points = points;
		this.transl = transl;
		this.storages = storages;
	}

	/**
	 * Метод вызываемый в отдельном потоке для обработки
	 */
	@Override
	public ThreadCounterResult call() throws Exception {
		Integer min = null;
		Integer max = null;
		Map<Point2D, List<Integer>> coeffs = new HashMap<Point2D, List<Integer>>();
		// Проходим по всем точкам потока
		for (int i = 0; i < points.size(); i++) {
			Point2D p = points.get(i);
			// Переводим в координаты IRS
			Point3DInIRSCoords transfRes = transl.translate(p);
			// Получаем значения количества операций для точки IRS
			List<Integer> result = DataGenerator.getContersForStorages(
					storages, transfRes.getInsertCoord(),
					transfRes.getSelectCoord(), transfRes.getRemoveCoord());
			log.debug("Была обработана точка ("
					+ p.getX()
					+ ","
					+ p.getY()
					+ "). Новые координаты ISR ("
					+ transfRes.getInsertCoord()
					+ ","
					+ transfRes.getSelectCoord()
					+ ","
					+ transfRes.getRemoveCoord()
					+ "). Количество операций: "
					+ result.stream().map(Object::toString)
							.collect(Collectors.joining(", ")));
			// Если одно хранилище, то ищем минимум и максимум по значениям
			// точек
			if (storages.size() == 1) {
				Integer cur = result.get(0);
				if (min == null || cur < min)
					min = cur;
				if (max == null || cur > max)
					max = cur;
			}
			// Очищаем хранилища
			for (int j = 0; j < storages.size(); j++) {
				storages.get(j).clear();
				storages.get(j).setCounterSet(new SimpleCounterSet());
			}
			coeffs.put(p, result);
		}

		ThreadCounterResult result = new ThreadCounterResult(coeffs, min, max);
		return result;
	}

}

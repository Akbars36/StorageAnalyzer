package com.vsu.amm.visualization.data;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.vsu.amm.command.DataSetPlayer;
import com.vsu.amm.command.xmlgen.SourceGenerator;
import com.vsu.amm.data.storage.IDataStorage;
import com.vsu.amm.stat.ICounterSet;
import com.vsu.amm.stat.ICounterSet.OperationType;
import com.vsu.amm.visualization.Vizualizator;
import com.vsu.amm.visualization.coordinate.CoordinanateTranslator;
import com.vsu.amm.visualization.data.parallel.ThreadCounter;
import com.vsu.amm.visualization.data.parallel.ThreadCounterResult;
import com.vsu.amm.visualization.utils.DrawUtils;

/**
 * Класс для получения количества операций
 * 
 * @author Potapov Danila
 *
 */
public class DataGenerator {

	/**
	 * Логгер
	 */
	static Logger log = Logger.getLogger(Vizualizator.class.getName());

	/**
	 * Метод для получения количества операций
	 * 
	 * @param storages
	 *            хранилища, на которых тестируются нагрузка
	 * @param insertCount
	 *            количество insert
	 * @param selectCount
	 *            количество select
	 * @param removeCount
	 *            количество remove
	 * @return список, который содержит сумму операций присваивания и сравнения
	 *         для каждого хранилища
	 */
	public static List<Integer> getContersForStorages(
			List<IDataStorage> storages, Integer insertCount,
			Integer selectCount, Integer removeCount) {
		// Создаем плеер для тестирования нагрузки
		DataSetPlayer dsp = new DataSetPlayer(storages, null);
		// Создаем нагрузку и тестируем ее
		dsp.play(SourceGenerator.createInsertSelectRemoveSource(insertCount,
				selectCount, removeCount));
		// Получаем список , который содержит сумму операций присваивания и
		// сравнения для каждого хранилища
		List<Integer> result = new ArrayList<>();
		for (int i = 0; i < storages.size(); i++) {
			ICounterSet set = storages.get(i).getCounterSet();
			Integer sumOfOperations = set.get(OperationType.ASSIGN)
					+ set.get(OperationType.COMPARE);
			result.add(sumOfOperations);
		}
		return result;

	}

	/**
	 * Метод который для каждой точки изображения получает количество операций
	 * 
	 * @param size
	 *            размер изображения
	 * @param storages
	 *            тестируемые хранилища
	 * @return карту из точек и их значений, а также минимум и максимум(если
	 *         одно хранилище)
	 */
	public static ImageData getCoeffs(int size, List<IDataStorage> storages) {
		// Получаем количество доступных процессоров
		int PROCESSORS_COUNT = Runtime.getRuntime().availableProcessors();
		ExecutorService executor = Executors
				.newFixedThreadPool(PROCESSORS_COUNT + 1);
		Map<Point2D, List<Integer>> coeffs = new HashMap<Point2D, List<Integer>>();
		Integer min = null;
		Integer max = null;
		// Генерируем задания по обработке точек
		List<Future<ThreadCounterResult>> list = genearateTasks(size, storages,
				executor, PROCESSORS_COUNT);
		// Вызываем задания
		for (Future<ThreadCounterResult> fut : list) {
			ThreadCounterResult r = null;
			try {
				r = fut.get();
				// Если одно хранилище, то ищем минимум и максимум по всем
				// заданиям
				if (storages.size() == 1) {
					if (min == null || r.getCurMin() < min)
						min = r.getCurMin();
					if (max == null || r.getCurMax() > max)
						max = r.getCurMax();
				}
				// Добавляем результаты в одну карту точек - значений
				coeffs.putAll(r.getResult());
			} catch (InterruptedException | ExecutionException e) {
				log.error("Во время выполнения программы возникла ошибка. Результат может быть неккоректным.");
			}
		}
		executor.shutdown();
		if (storages.size() == 1) {
			log.debug("Min=" + min + " ;max= " + max);
		}
		ImageData data = new ImageData(min, max, coeffs);
		return data;
	}

	/**
	 * Метод который формирует задания по обработке точек изображения и
	 * отправляет их на выполнение
	 * 
	 * @param size
	 *            размер изображения
	 * @param storages
	 *            хранилища для тестирования
	 * @param executor
	 *            класс для выполнения заданий
	 * @param PROCESSORS_COUNT
	 *            количество заданий
	 * @return
	 */
	private static List<Future<ThreadCounterResult>> genearateTasks(int size,
			List<IDataStorage> storages, ExecutorService executor,
			int PROCESSORS_COUNT) {
		// Количество точек на поток
		Integer countInTask = (size * size / 2) / PROCESSORS_COUNT;
		// номер текущей точки
		Integer curPointNumber = 0;
		// Номер текущего задания
		Integer curTaskNumber = 1;
		List<Point2D> curPoints = new ArrayList<Point2D>();
		List<Future<ThreadCounterResult>> list = new ArrayList<Future<ThreadCounterResult>>();
		// Создаем переводчик координат
		CoordinanateTranslator transl = new CoordinanateTranslator(size);
		// Проходим по всем точкам изображения
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				// Определяем лежит ли точка в треугольнике Insert(0,0)
				// Select(size/2,size*sqrt (3)/2) Remove(size,0)
				if (DrawUtils.pointInTriangle(0, 0, size / 2, (float) (size
						* Math.sqrt(3.0) / 2.0f), size, 0, x, size - y)) {
					Point2D p = new Point2D.Double(x, size - y);
					curPoints.add(p);
					// Если набралось необходимое количество точек для создания
					// задания
					if (curPointNumber > curTaskNumber * countInTask) {
						// Создаем копии хранилищ
						List<IDataStorage> storagesCopy = new ArrayList<IDataStorage>(
								storages.size());
						for (int k = 0; k < storages.size(); k++) {
							storagesCopy.add(storages.get(k).cloneDefault());
						}
						// Создаем задание и отправляем его на выполнение
						Callable<ThreadCounterResult> callable = new ThreadCounter(
								curPoints, transl, storagesCopy);
						Future<ThreadCounterResult> future = executor
								.submit(callable);
						// add Future to the list, we can get return value using
						// Future
						list.add(future);
						curTaskNumber++;
						curPoints = new ArrayList<Point2D>();
					}
					curPointNumber++;
				}
			}
		}
		// Если остались точки, то создаем еще одно задание
		if (curPoints != null) {
			List<IDataStorage> storagesCopy = new ArrayList<IDataStorage>(
					storages.size());
			for (int k = 0; k < storages.size(); k++) {
				storagesCopy.add(storages.get(k).cloneDefault());
			}
			Callable<ThreadCounterResult> callable = new ThreadCounter(
					curPoints, transl, storagesCopy);
			Future<ThreadCounterResult> future = executor.submit(callable);
			// add Future to the list, we can get return value using Future
			list.add(future);
		}
		return list;
	}
}

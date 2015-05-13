package com.vsu.amm.visualization.data;

import java.util.ArrayList;
import java.util.List;

import com.vsu.amm.command.DataSetPlayer;
import com.vsu.amm.command.xmlgen.SourceGenerator;
import com.vsu.amm.data.storage.IDataStorage;
import com.vsu.amm.stat.ICounterSet;
import com.vsu.amm.stat.ICounterSet.OperationType;

/**
 * Класс для получения количества операций
 * 
 * @author Potapov Danila
 *
 */
public class DataGenerator {
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
}

package com.vsu.amm.visualization;

import com.vsu.amm.data.storage.BTree;
import com.vsu.amm.data.storage.IDataStorage;
import com.vsu.amm.data.storage.SimpleArray;
import com.vsu.amm.data.storage.SimpleList;
import com.vsu.amm.data.storage.SortedArray;
import com.vsu.amm.data.storage.SortedList;
import com.vsu.amm.data.storage.StorageGenerator;
import com.vsu.amm.stat.SimpleCounterSet;
import com.vsu.amm.visualization.Vizualizator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Класс для запуска приложения визуализации
 * 
 * @author Potapov Danila
 *
 */
public class Main {
	/**
	 * Логгер
	 */
	static Logger log = Logger.getLogger(Main.class.getName());

	/**
	 * Основная функция программы
	 * 
	 * @param args
	 *            аргументы запуска
	 */
	public static void main(String[] args) throws IOException {
		// InputStreamReader isr = new InputStreamReader(System.in);
		// BufferedReader br = new BufferedReader(isr);
		// System.out
		// .println("***Программа для визуализации нагрузки на хранилища***");
		// Integer size = getSize(br);
		// String imageName = getImageName(br);
		// List<IDataStorage> storages = getStorages(br);
		for (int i = 0; i < 1; i++) {
			String imageName = "t300" + i;
			List<IDataStorage> storages = new ArrayList<>();
			IDataStorage s = new SortedList();
			s.setCounterSet(new SimpleCounterSet());

			storages.add(s);
			s = new BTree();
			s.setCounterSet(new SimpleCounterSet());

			storages.add(s);
			Integer size = 1000;
			Vizualizator.Draw(size, imageName, storages);
		}
	}

	/**
	 * Функция получения хранилищ для тестирования
	 * 
	 * @param br
	 *            поток с консоли
	 * @return список сформированных хранилищ
	 * @throws IOException
	 */
	private static List<IDataStorage> getStorages(BufferedReader br)
			throws IOException {
		String command = "y";
		String storageClass;
		String storageParamName;
		String storageParamValue;
		Integer storageParamIntValue = null;
		Map<String, Integer> params = null;
		List<IDataStorage> storages = new ArrayList<IDataStorage>();
		while ("y".equals(command.toLowerCase())) {
			// Считываем название класса хранилища
			do {
				System.out.print("Класс хранилища:");
				storageClass = br.readLine();
				if (storageClass == null || storageClass.equals("")) {
					System.out.println("Класс хранилища не должен быть пуст!");
				}
			} while (storageClass == null || storageClass.equals(""));

			// Спрашиваем нужны ли параметры для хранилища
			System.out.print("Добавить к хранилищу параметры? (y/n)");
			command = br.readLine();
			while ("y".equals(command)) {
				params = new HashMap<String, Integer>();
				// Считываем название параметра
				do {
					System.out.print("Название параметра хранилища:");
					storageParamName = br.readLine();
					if (storageParamName == null || storageParamName.equals("")) {
						System.out
								.println("Название параметра хранилища не должно быть пусто!");
					}
				} while (storageParamName == null
						|| storageParamName.equals(""));

				// Считываем значение параметра
				do {
					System.out
							.print("Значение параметра хранилища(целочисленное):");
					storageParamValue = br.readLine();
					if (storageParamValue == null
							|| storageParamValue.equals("")) {
						System.out
								.println("Значение параметра хранилища не должно быть пусто!");
					}
					try {
						storageParamIntValue = Integer
								.parseInt(storageParamValue);
					} catch (NumberFormatException ex) {

					}
				} while (storageParamValue == null
						|| storageParamValue.equals(""));
				if (storageParamIntValue != null)
					params.put(storageParamName, storageParamIntValue);
				System.out
						.print("Добавить к хранилищу еще один параметр? (y/n)");
				command = br.readLine();
			}
			IDataStorage storage = StorageGenerator.getDataStorage(
					storageClass, null, params);
			if (storage != null) {
				storage.setCounterSet(new SimpleCounterSet());
				storages.add(storage);
			} else
				System.out
						.println("Хранилище с таким классом не было найдено. Оно не будет протестировано.");
			System.out.print("Добавить еще одно хранилище? (y/n)");
			command = br.readLine();
		}
		return storages;
	}

	/**
	 * Функция получения с консоли размера выходного файла изображения в
	 * пикселях
	 * 
	 * @param br
	 *            поток с консоли
	 * @return размер выходного файла изображения в пикселях
	 * @throws IOException
	 */
	private static Integer getSize(BufferedReader br) throws IOException {
		Integer size = null;
		do {
			System.out
					.print("Размер выходного файла изображения в пикселях(от 10 до 2000):");
			String sizeStr = br.readLine();
			if (sizeStr == null || sizeStr.equals("")) {
				System.out
						.println("Размер выходного файла изображения не должен быть пуст!");
			}
			try {
				size = Integer.parseInt(sizeStr);
				if (size < 10 || size > 2000) {
					System.out
							.println("Размер выходного файла изображения должен быть в диапазоне от 10 до 2000!");
				} else
					return size;
			} catch (NumberFormatException ex) {

			}
		} while (size == null || size < 10 || size > 2000);
		return null;
	}

	/**
	 * Функция получения с консоли имени выходного файла изображения
	 * 
	 * @param br
	 *            поток с консоли
	 * @return Имя выходного файла изображения
	 * @throws IOException
	 */
	private static String getImageName(BufferedReader br) throws IOException {
		String imageName = null;
		do {
			System.out.print("Название выходного файла изображения:");
			imageName = br.readLine();

			if (imageName == null || imageName.equals("")) {
				System.out
						.println("Название выходного файла изображения не может быть пусто!");
			}
			return imageName;
		} while (imageName == null || imageName.equals(""));
	}
}

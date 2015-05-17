package com.vsu.amm.visualization;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import com.vsu.amm.data.storage.IDataStorage;
import com.vsu.amm.stat.ICounterSet.OperationType;
import com.vsu.amm.stat.SimpleCounterSet;
import com.vsu.amm.visualization.coordinate.CoordinanateTranslator;
import com.vsu.amm.visualization.coordinate.Point3DInIRSCoords;
import com.vsu.amm.visualization.data.DataGenerator;
import com.vsu.amm.visualization.data.ImageData;
import com.vsu.amm.visualization.utils.DrawConstants;
import com.vsu.amm.visualization.utils.DrawUtils;

/**
 * Класс для создания изображений, отображающих результаты сравнения хранилищ
 * 
 * @author Potapov Danila
 *
 */
public class Vizualizator {

	static Logger log = Logger.getLogger(Vizualizator.class.getName());

	/**
	 * Основной метод для создания изображения
	 * 
	 * @param size
	 *            размер выходного изображения
	 * @param filename
	 *            название выходного файла
	 * @param storages
	 *            список хранилищ для тестирования
	 */
	public static void Draw(int size, String filename,
			List<IDataStorage> storages) {
		if (size < 0) {
			log.fatal("Был выбран неккоректный размер файла.");
			return;
		}
		if (filename == null || filename.isEmpty()) {
			log.fatal("Было выбрано неккоректное имя файла.");
			return;
		}
		if (storages == null || storages.isEmpty()) {
			log.fatal("Не были выбраны хранилища для тестирования");
			return;
		}
		log.info("Создаем файл с именем " + filename + " и размером " + size
				+ "*" + size + " пикселей.");
		// Создаем файл изображения
		int type = BufferedImage.TYPE_INT_ARGB;
		BufferedImage image = new BufferedImage(
				size + DrawConstants.OFFSET * 2, size + DrawConstants.OFFSET
						* 2, type);
		File outputfile = new File(filename + "."
				+ DrawConstants.IMAGE_EXTENSION);
		// рисуем на изображении
		drawImage(image, size, storages);
		// Сохраняем изображение
		try {
			ImageIO.write(image, DrawConstants.IMAGE_EXTENSION, outputfile);
			log.info("Файл с именем " + filename + " и размером " + size + "*"
					+ size + " пикселей успешно создан.");
		} catch (IOException e) {
			log.fatal("Невозможно сохранить файл с именем " + filename
					+ " и размером " + size + "*" + size + " пикселей.");
		}

	}

	/**
	 * Метод который наносит рисунок на изображение
	 * 
	 * @param image
	 *            изображение на котором рисуем
	 * @param size
	 *            размер изображения
	 * @param storages
	 *            список тестируемых хранилищ
	 */
	private static void drawImage(BufferedImage image, int size,
			List<IDataStorage> storages) {
		// Получаем список точек и их значений
		ImageData data = getCoeffs(size, storages);
		Map<Point2D, List<Integer>> coeffs = data.getData();
		// Определяем тип изображения
		boolean isSingle = storages.size() == 1;
		Color curColor = null;
		double curRange = 0;
		// Определяем диапазон значений(если 1 хранилище)
		if (isSingle)
			curRange = data.getMax() - data.getMin();
		// Проходим по всем точкам, находим их цвета и отображаем на изображении
		for (Entry<Point2D, List<Integer>> entry : coeffs.entrySet()) {
			Point2D point = entry.getKey();
			List<Integer> vals = entry.getValue();
			curColor = getPointColor(isSingle, point, vals, data.getMin(),
					curRange);
			if (curColor != null)
				image.setRGB((int) point.getX() + DrawConstants.OFFSET, size
						- (int) point.getY() + DrawConstants.OFFSET,
						curColor.getRGB());
		}
		// рисуем оси
		drawAxis(image, size);
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
	private static ImageData getCoeffs(int size, List<IDataStorage> storages) {

		Map<Point2D, List<Integer>> coeffs = new HashMap<Point2D, List<Integer>>();
		CoordinanateTranslator transl = new CoordinanateTranslator(size);
		Integer min = null;
		Integer max = null;
		for (int x = 0; x < size; x++) {
			Long now = Calendar.getInstance().getTimeInMillis();
			for (int y = 0; y < size; y++) {
				if (DrawUtils.pointInTriangle(0, 0, size / 2, (float) (size
						* Math.sqrt(3.0) / 2.0f), size, 0, x, size - y)) {
					Point2D p = new Point2D.Double(x, size - y);
					Point3DInIRSCoords res = transl.translate(p);
					List<Integer> result = DataGenerator.getContersForStorages(
							storages, res.getInsertCoord(),
							res.getSelectCoord(), res.getRemoveCoord());
					log.debug("Была обработана точка ("
							+ x
							+ ","
							+ (size - y)
							+ "). Новые координаты ISR ("
							+ res.getInsertCoord()
							+ ","
							+ res.getSelectCoord()
							+ ","
							+ res.getRemoveCoord()
							+ "). Количество операций: "
							+ result.stream().map(Object::toString)
									.collect(Collectors.joining(", ")));

					if (storages.size() == 1) {
						Integer cur = storages.get(0).getCounterSet()
								.get(OperationType.ASSIGN)
								+ storages.get(0).getCounterSet()
										.get(OperationType.COMPARE);
						if (min == null || cur < min)
							min = cur;
						if (max == null || cur > max)
							max = cur;
					}
					for (int i = 0; i < storages.size(); i++) {
						storages.get(i).clear();
						storages.get(i).setCounterSet(new SimpleCounterSet());
					}
					coeffs.put(p, result);
				}
			}
			double delta = (Calendar.getInstance().getTimeInMillis() - now) / 1000.0;
			log.debug("x= " + x + "; delta= " + delta + "s");
		}
		if (storages.size() == 1) {
			log.debug("Min=" + min + " ;max= " + max);
		}
		ImageData data = new ImageData(min, max, coeffs);
		return data;
	}

	/**
	 * Метод для отрисовки осей координат
	 * 
	 * @param image
	 *            изображение на котором рисуем
	 * @param size
	 *            размер изображения
	 */
	private static void drawAxis(BufferedImage image, int size) {
		Graphics gr = image.getGraphics();
		gr.setColor(Color.RED);
		gr.drawString("Select", (size + DrawConstants.OFFSET) / 2,
				DrawConstants.OFFSET);
		gr.drawString("Insert", 0, size + DrawConstants.OFFSET);
		gr.drawString("Remove", size + DrawConstants.OFFSET, size
				+ DrawConstants.OFFSET);
	}

	/**
	 * Метод получения цвета для точки
	 * 
	 * @param isSingle
	 *            параметр для определения алгоритма определения
	 *            цвета(количество сравниваемых хранилищ)
	 * @param point
	 *            точка для которой ищем цвет
	 * @param vals
	 *            значения этой точки
	 * @param mi
	 *            минимальное значение среди всех точек(Null если кол-во
	 *            хранилищ>1)
	 * @param curRange
	 *            диапазон значений среди всех точек(0 если кол-во хранилищ>1)
	 * @return цвет для точки
	 */
	private static Color getPointColor(boolean isSingle, Point2D point,
			List<Integer> vals, Integer mi, double curRange) {
		Color curColor = null;
		// Если одно хранилище, получаем коэффицент точки из диапазона [0,1] и
		// цвет с использованием линейного градиента
		if (isSingle) {
			double ratio = (vals.get(0) - mi) / curRange;
			curColor = DrawUtils.getColorByLinearGradient(ratio,
					DrawConstants.COLORS[0], DrawConstants.COLORS[1]);
			// Иначе ищем минимальное значение среди всех хранилищ и выбираем
			// соответствующий данному хранилищу цвет
		} else {
			int minInd = 0;
			int min = vals.get(minInd);
			for (int i = 0; i < vals.size(); i++) {
				if (vals.get(i) < min) {
					minInd = i;
					min = vals.get(i);
				}
			}
			try {
				curColor = DrawConstants.COLORS[minInd];
			} catch (ArrayIndexOutOfBoundsException ex) {
				log.error("Для хранилища №" + minInd + "не задан цвет в точке("
						+ point.getX() + "," + point.getY() + ").");
			}
		}
		return curColor;
	}

}

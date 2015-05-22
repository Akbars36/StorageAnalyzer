package com.vsu.amm.visualization;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import com.vsu.amm.data.storage.IDataStorage;
import com.vsu.amm.visualization.classification.Classificator;
import com.vsu.amm.visualization.classification.Point;
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

	/**
	 * Логгер
	 */
	static Logger log = Logger.getLogger(Vizualizator.class.getName());

	private static int MODE;

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
		switch (storages.size()) {
		case 1:
			MODE = 1;
			break;
		case 2:
			MODE = 2;
			break;
		default:
			MODE = 3;
			break;
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
		ImageData data = DataGenerator.getCoeffs(size, storages);
		Map<Point2D, List<Integer>> coeffs = data.getData();
		// Определяем тип изображения
		Color curColor = null;
		double curRange = 0;
		// Определяем диапазон значений(если 1 хранилище)
		if (MODE == 1) {
			curRange = data.getMax() - data.getMin();
		}
		List<Point> classificationPoints = new ArrayList<>();
		int i = 0;
		// Проходим по всем точкам, находим их цвета и отображаем на изображении
		for (Entry<Point2D, List<Integer>> entry : coeffs.entrySet()) {
			Point2D point = entry.getKey();
			List<Integer> vals = entry.getValue();
			Point p = handlePoint(point, vals);
			curColor = getPointColor(p, data.getMin(), curRange);
			double x = p.getX();
			double y = p.getY();
			if (curColor != null)
				image.setRGB((int) point.getX() + DrawConstants.OFFSET, size
						- (int) point.getY() + DrawConstants.OFFSET,
						curColor.getRGB());
			if (MODE == 2
					&& ((point.getX() % (size / 10) == 0 && point.getY()
							% (size / 10) == 0) || DrawUtils
							.pointInTriangleSide(0, 0, size / 2,
									(float) (size * Math.sqrt(3.0) / 2.0f),
									size, 0, (float) x,
									(float) y))) {
				image.setRGB((int) point.getX() + DrawConstants.OFFSET, size
						- (int) point.getY() + DrawConstants.OFFSET,
						Color.black.getRGB());
				classificationPoints.add(p);
			}

		}
		System.out.println(classificationPoints.size());
		if (MODE == 2) {
			double[] w = Classificator.classification(classificationPoints);
			for (int x = 0; x < size; x++) {
				for (int y = 0; y < size; y++) {
					// System.out.println("x="+x+" y="+y+" res= "+Math.abs(w[0]
					// * (x ) + w[1]
					// * (y ) + w[2]));
					if (Math.abs(w[0] * (x) + w[1] * (y) + w[2]) < 0.2) {
						image.setRGB((int) x + DrawConstants.OFFSET, size
								- (int) y + DrawConstants.OFFSET,
								Color.BLACK.getRGB());
					}
				}
			}
		}
		// рисуем оси
		drawAxis(image, size);
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
	private static Color getPointColor(Point point, Integer mi, double curRange) {
		Color curColor = null;
		// Если одно хранилище, получаем коэффицент точки из диапазона [0,1] и
		// цвет с использованием линейного градиента
		if (MODE == 1) {
			double ratio = (point.getValue() - mi) / curRange;
			curColor = DrawUtils.getColorByLinearGradient(ratio,
					DrawConstants.COLORS[0], DrawConstants.COLORS[1]);
			// Иначе ищем минимальное значение среди всех хранилищ и выбираем
			// соответствующий данному хранилищу цвет
		} else {
			try {
				curColor = DrawConstants.COLORS[point.getValue()];
			} catch (ArrayIndexOutOfBoundsException ex) {
				log.error("Для хранилища №" + point.getValue()
						+ "не задан цвет в точке(" + point.getX() + ","
						+ point.getY() + ").");
			}
		}
		return curColor;
	}

	private static Point handlePoint(Point2D point, List<Integer> vals) {
		Integer value = vals.get(0);
		if (MODE != 1) {
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
		Point result = new Point(point.getX(), point.getY(), value);
		return result;
	}

}

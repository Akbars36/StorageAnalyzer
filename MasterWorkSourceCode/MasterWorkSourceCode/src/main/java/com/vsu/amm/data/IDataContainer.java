package com.vsu.amm.data;

public interface IDataContainer {

	/**
	 * Ищет заданное значение в хранилище
	 *
	 * @param value
	 *            значение, которое получают из хранилища
	 * @return был ли найден элемент в хранилище
	 */
	boolean get(int value);

	/**
	 * Устанавливает заданное значение в хранилище
	 *
	 * @param value значение, которое добавляют в хранилище
	 * @return было ли добавлено новое значение в хранилище
	 */
	boolean set(int value);

	/**
	 * Удаляет заданное значение из хранилища
	 *
	 * @param value значение, которое удаляют из хранилища
	 * @return было ли удалено значение из хранилища
	 */
	boolean remove(int value);
}

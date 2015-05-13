package com.vsu.amm.data;

public interface IDataContainer {

	/**
	 * Ищет заданное значение в хранилище
	 *
	 * @param value
	 *            значение, которое получают из хранилища
	 */
	void get(int value);

	/**
	 * Устанавливает заданное значение в хранилище
	 *
	 * @param value
	 *            значение, которое добавляют в хранилище
	 */
	void set(int value);

	/**
	 * Удаляет заданное значение из хранилища
	 *
	 * @param value
	 *            значение, которое удаляют из хранилища
	 */
	void remove(int value);
}

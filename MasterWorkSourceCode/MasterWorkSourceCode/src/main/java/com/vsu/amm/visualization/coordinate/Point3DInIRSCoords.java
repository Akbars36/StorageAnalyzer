package com.vsu.amm.visualization.coordinate;

/**
 * Класс, который содержит 3х мерные координаты в пространстве
 * (insert,select,remove)
 * 
 * @author Potapov Danila
 *
 */
public class Point3DInIRSCoords {
	/**
	 * Координата по оси insert
	 */
	Integer insertCoord;
	/**
	 * Координата по оси remove
	 */
	Integer removeCoord;
	/**
	 * Координата по оси select
	 */
	Integer selectCoord;

	/**
	 * Конструктор
	 * 
	 * @param insertCoord
	 *            Координата по оси insert
	 * @param removeCoord
	 *            Координата по оси remove
	 * @param selectCoord
	 *            Координата по оси select
	 */
	public Point3DInIRSCoords(Integer insertCoord, Integer removeCoord,
			Integer selectCoord) {
		super();
		this.insertCoord = insertCoord;
		this.removeCoord = removeCoord;
		this.selectCoord = selectCoord;
	}

	public Integer getInsertCoord() {
		return insertCoord;
	}

	public void setInsertCoord(Integer insertCoord) {
		this.insertCoord = insertCoord;
	}

	public Integer getRemoveCoord() {
		return removeCoord;
	}

	public void setRemoveCoord(Integer removeCoord) {
		this.removeCoord = removeCoord;
	}

	public Integer getSelectCoord() {
		return selectCoord;
	}

	public void setSelectCoord(Integer selectCoord) {
		this.selectCoord = selectCoord;
	}

}

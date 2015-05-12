package com.vsu.amm.visualization.coordinate;

public class Point3DInIRSCoords {
	Integer insertCoord;
	Integer removeCoord;
	Integer selectCoord;
	
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

package com.vsu.amm.visualization.classification;

public class Point {
	Double x;
	Double y;
	Integer value;
	public Double getX() {
		return x;
	}
	public void setX(Double x) {
		this.x = x;
	}
	public Double getY() {
		return y;
	}
	public void setY(Double y) {
		this.y = y;
	}
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	public Point(Double x, Double y, Integer value) {
		super();
		this.x = x;
		this.y = y;
		this.value = value;
	}
	
	
}

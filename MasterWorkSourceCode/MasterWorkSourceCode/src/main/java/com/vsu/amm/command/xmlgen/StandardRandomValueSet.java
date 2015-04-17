package com.vsu.amm.command.xmlgen;

public class StandardRandomValueSet extends BaseValueSet {

	private int count;
	private int min;
	private int max;
	
	public StandardRandomValueSet(int count,int min, int max) {
		super();
		this.count = count;
		this.min = min;
		this.max = max;
	}

	@Override
	public int generateRandom() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}

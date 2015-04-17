package com.vsu.amm.command.xmlgen;

public abstract class BaseValueSet {
	
	abstract public int generateRandom();
	
	public int reuseRandom()
	{
		return 0;
	}
	
	protected void storeForReuse(int value)
	{
		
	}
}

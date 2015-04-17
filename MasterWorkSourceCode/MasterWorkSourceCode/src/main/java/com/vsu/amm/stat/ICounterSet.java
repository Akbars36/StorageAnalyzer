package com.vsu.amm.stat;

/**
 * Created with IntelliJ IDEA.
 * User: Влад
 * Date: 13.11.13
 * Time: 20:22
 * To change this template use File | Settings | File Templates.
 */
public interface ICounterSet {
	enum OperationType
	{
		operationCompare,
		operationAssign
	};
	
	public void startLabel(String name);
	
    public void inc(OperationType operation);

    public void inc(OperationType operation, int delta);
}

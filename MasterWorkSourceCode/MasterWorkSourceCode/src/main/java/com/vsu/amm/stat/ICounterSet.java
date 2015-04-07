package com.vsu.amm.stat;

/**
 * Created with IntelliJ IDEA.
 * User: Влад
 * Date: 13.11.13
 * Time: 20:22
 * To change this template use File | Settings | File Templates.
 */
public interface ICounterSet {
    public static final String COMPARE="compare";

    public static final String ASSIGN="assign";

    public void inc(String counterName);

    public void inc(String counterName, int delta);

    public int get(String counterName);
}

package com.vsu.amm.stat;

/**
 * Created with IntelliJ IDEA.
 * User: Влад
 * Date: 13.11.13
 * Time: 20:22
 * To change this template use File | Settings | File Templates.
 */
public interface ICounterSet {

    void inc(OperationType operation);

    void inc(OperationType operation, int delta);

    int get(OperationType operation);

    enum OperationType {
        COMPARE,
        ASSIGN
    }
}

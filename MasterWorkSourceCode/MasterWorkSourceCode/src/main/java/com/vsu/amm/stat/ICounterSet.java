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

    void clear();

    enum OperationType {
        COMPARE,
        ASSIGN,
        DEFERRED_COMPARE,
        CALCULATION,
        DEFERRED_CALCULATION,
        DEFERRED_ASSIGN
    }


}

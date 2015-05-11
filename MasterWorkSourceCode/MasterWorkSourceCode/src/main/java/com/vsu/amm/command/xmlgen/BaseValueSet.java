package com.vsu.amm.command.xmlgen;

import java.util.Map;

public abstract class BaseValueSet {

    abstract public int generateRandom();

    abstract public int generateRandomAndStore();

    public Integer reuseRandom() {
        return null;
    }

    public boolean hasStoredValues() {
        return false;
    }

    abstract public void clearStoredValues();

    abstract public void setParams(Map<String, Integer> params);
}

package com.vsu.amm.command.xmlgen;

public abstract class BaseValueSet {

    abstract public int generateRandom();

    abstract public int generateRandomAndStore();

    public Integer reuseRandom() {
        return null;
    }

    public boolean hasStoredValues() {
        return false;
    }

}

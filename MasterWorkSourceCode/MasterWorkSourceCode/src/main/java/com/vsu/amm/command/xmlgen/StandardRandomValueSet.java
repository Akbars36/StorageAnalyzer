package com.vsu.amm.command.xmlgen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StandardRandomValueSet extends BaseValueSet {

    private int min;
    private int max;
    private List<Integer> storedValues;
    private Random random = null;

    public StandardRandomValueSet(int min, int max) {
        super();
        if (max >= min) {
            this.min = min;
            this.max = max;
        } else {
            this.min = max;
            this.max = min;
        }

        random = new Random();
        storedValues = new ArrayList<>();
    }

    @Override
    public int generateRandom() {

        return random.nextInt(max - min) + min;
    }

    @Override
    public int generateRandomAndStore() {
        int value = random.nextInt(max - min) + min;
        storedValues.add(value);
        return value;
    }

    @Override
    public Integer reuseRandom() {
        if (storedValues.isEmpty())
            return null;
        return storedValues.get(random.nextInt() % storedValues.size());
    }

    @Override
    public boolean hasStoredValues() {
        return storedValues != null && storedValues.size() > 0;
    }
}

package com.vsu.amm.command.xmlgen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class StandardRandomValueSet extends BaseValueSet {

    private final List<Integer> storedValues = new ArrayList<>();
    private final Random random = new Random();
    private Integer min = null;
    private String minParamName = null;
    private Integer max = null;
    private String maxParamName = null;

    public StandardRandomValueSet(int min, int max) {
        super();
        if (max >= min) {
            this.min = min;
            this.max = max;
        } else {
            this.min = max;
            this.max = min;
        }
    }

    public StandardRandomValueSet(String minParam, String maxParam, Map<String, Integer> params) {
        super();

        if (minParam != null) {
            if (minParam.startsWith("%") && minParam.endsWith("%")) {
                minParamName = minParam.substring(1, minParam.length() - 1);
                if (params != null)
                    if (params.get(minParamName) != null)
                        min = params.get(minParamName);
            } else
                try {
                    min = Integer.parseInt(minParam);
                } catch (Exception ignored) {
                }
        }
        if (min == null)
            min = 0;

        if (maxParam != null) {
            if (maxParam.startsWith("%") && maxParam.endsWith("%")) {
                maxParamName = maxParam.substring(1, maxParam.length() - 1);
                if (params != null)
                    if (params.get(maxParamName) != null)
                        max = params.get(maxParamName);
            } else
                try {
                    max = Integer.parseInt(maxParam);
                } catch (Exception ignored) {
                }
        }

        if (max == null)
            max = Integer.MAX_VALUE;

        if (min > max) {
            int tmp = min;
            min = max;
            max = tmp;
        }
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
        return storedValues.get(random.nextInt(storedValues.size()));
    }

    @Override
    public boolean hasStoredValues() {
        return storedValues != null && storedValues.size() > 0;
    }

    @Override
    public void clearStoredValues() {
        storedValues.clear();
    }

    @Override
    public void setParams(Map<String, Integer> params) {
        if (params == null)
            return;

        if (minParamName != null) {
            Integer param = params.get(minParamName);
            if (param != null)
                min = param;
        }

        if (maxParamName != null) {
            Integer param = params.get(maxParamName);
            if (param != null)
                max = param;
        }
    }
}

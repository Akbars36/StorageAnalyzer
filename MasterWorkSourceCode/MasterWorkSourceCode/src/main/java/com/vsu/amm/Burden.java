package com.vsu.amm;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: vlzo0513
 * Date: 11.11.13
 * Time: 12:40
 * To change this template use File | Settings | File Templates.
 */
public class Burden {


    public static enum TagNames{
        SEQUENCE("sequence"),
        BLOCK("block"),
        INSERT("insert"),
        SELECT("select"),
        REMOVE("remove")
        ;

        private TagNames(final String name){
            this.name = name;
        }
        private final String name;

        @Override
        public String toString() {
            return name;
        }

    }

    private int id;
    private int parentId;
    private String name;
    private String count;
    private String label;
    private String alias;
    private String from;
    private String min;
    private String max;
    private String tempCount;

    private List<Integer> childrenId;
    private List<Integer> genKeyValueToAlias;

    public Burden(int id, int parentId, String name, String count, String label, String alias, String from, String min, String max) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.count = count;
        this.label = label;
        this.alias = alias;
        this.from = from;
        this.min = min;
        this.max = max;
        this.childrenId = null;
        this.genKeyValueToAlias = null;
        this.tempCount = count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public List<Integer> getChildrenId() {
        return childrenId;
    }

    public void setChildrenId(List<Integer> childrenId) {
        this.childrenId = childrenId;
    }

    public List<Integer> getGenKeyValueToAlias() {
        return genKeyValueToAlias;
    }

    public void setGenKeyValueToAlias(List<Integer> genKeyValueToAlias) {
        this.genKeyValueToAlias = genKeyValueToAlias;
    }

    public String getTempCount() {
        return tempCount;
    }

    public void setTempCount(String tempCount) {
        this.tempCount = tempCount;
    }
}

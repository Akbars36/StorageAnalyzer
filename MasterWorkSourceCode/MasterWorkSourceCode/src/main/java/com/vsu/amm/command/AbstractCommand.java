package com.vsu.amm.command;

/**
 * Created by Nikita Skornyakov on 12.05.2015.
 */
public abstract class AbstractCommand implements ICommand{
    protected int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}

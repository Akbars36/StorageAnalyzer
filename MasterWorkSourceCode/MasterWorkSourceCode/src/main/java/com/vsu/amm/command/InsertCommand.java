package com.vsu.amm.command;

import com.vsu.amm.data.IDataContainer;
import com.vsu.amm.data.stream.IDataStream;

/**
 * Created by VLAD on 13.05.14.
 */
public class InsertCommand implements ICommand {

    int value;

    public InsertCommand(int value) {
        this.value = value;
    }

    @Override
    public void execute(IDataContainer dataSet) {
        if (dataSet == null)
            return;
        dataSet.set(value);
    }

    @Override
    public void printToStream(IDataStream stream) {
        if (stream == null) {
            return;
        }
        stream.set(value);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int newValue) {
        value = newValue;
    }
}

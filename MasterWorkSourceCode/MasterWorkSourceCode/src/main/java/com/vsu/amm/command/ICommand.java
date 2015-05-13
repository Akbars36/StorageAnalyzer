package com.vsu.amm.command;

import com.vsu.amm.data.IDataContainer;
import com.vsu.amm.data.stream.IDataStream;

public interface ICommand {

    void execute(IDataContainer dataSet);

    void printToStream(IDataStream stream);

    int getValue();
    void setValue(int value);
}

package com.vsu.amm.command;

import com.vsu.amm.data.stream.IDataStream;

public interface ICommandSource {

    void restart();

    ICommand next();

    void printToStream(IDataStream stream);
}

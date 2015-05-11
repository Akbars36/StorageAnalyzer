package com.vsu.amm.command;

import com.vsu.amm.data.stream.IDataStream;

import java.util.Map;

public interface ICommandSource {

    void restart();

    ICommand next();

    void printToStream(IDataStream stream);

    void setParameters(Map<String, Integer> parameters);
}

package com.vsu.amm.command;

import com.vsu.amm.data.storage.IDataStorage;
import com.vsu.amm.data.stream.IDataStream;

import java.util.List;

public class DataSetPlayer implements ICommandPlayer {

    private final List<IDataStorage> dataSets;
    private IDataStream stream;

    // protected ICounterSet counterSet;

    // public DataSetPlayer(IDataStorage dataSet) {
    // super();
    // this.dataSets = dataSets;
    // this.counterSet = null;
    // }

    public DataSetPlayer(List<IDataStorage> dataSets) {
        super();
        this.dataSets = dataSets;
        // this.counterSet = counterSet;
    }

    public DataSetPlayer(List<IDataStorage> dataSets, IDataStream stream) {
        super();
        this.dataSets = dataSets;
        this.stream = stream;
    }

    @Override
    public void play(ICommandSource commandSource) {
        if (commandSource == null)
            return;

        if (dataSets == null)
            return;

        ICommand cmd = commandSource.next();
        while (cmd != null) {
            for (IDataStorage dataSet : dataSets) cmd.execute(dataSet);
           
            cmd = commandSource.next();
        }
        if (stream != null) {
            commandSource.printToStream(stream);
            stream.close();
        }


        
    }

}

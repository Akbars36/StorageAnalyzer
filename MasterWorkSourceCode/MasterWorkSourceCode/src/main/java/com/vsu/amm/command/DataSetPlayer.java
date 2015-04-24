package com.vsu.amm.command;

import com.vsu.amm.data.IDataContainer;
import com.vsu.amm.stat.ICounterSet;

public class DataSetPlayer implements ICommandPlayer {

    protected IDataContainer dataSet;
    protected ICounterSet counterSet;

    public DataSetPlayer(IDataContainer dataSet) {
        super();
        this.dataSet = dataSet;
        this.counterSet = null;
    }


    public DataSetPlayer(IDataContainer dataSet, ICounterSet counterSet) {
        super();
        this.dataSet = dataSet;
        this.counterSet = counterSet;
    }


    @Override
    public void play(ICommandSource commandSource) {
        if (commandSource == null)
            return;

        if (dataSet == null)
            return;

        ICommand cmd = commandSource.next();
        while (cmd != null) {
            cmd.execute(dataSet);
            cmd = commandSource.next();
        }

    }

}

package com.vsu.amm.command;

import java.util.List;

import com.vsu.amm.data.storage.IDataStorage;
import com.vsu.amm.data.stream.IDataStream;
import com.vsu.amm.stat.ICounterSet;

public class DataSetPlayer implements ICommandPlayer {

	protected List<IDataStorage> dataSets;
	protected IDataStream stream;

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
			for (int i = 0; i < dataSets.size(); i++)
				cmd.execute(dataSets.get(i));
			cmd.printToStream(stream);
			cmd = commandSource.next();
		}
		if(stream!=null)
			stream.close();

	}

}

package com.vsu.amm.visualization.data;

import java.util.ArrayList;
import java.util.List;

import com.vsu.amm.Constants;
import com.vsu.amm.command.DataSetPlayer;
import com.vsu.amm.command.xmlgen.SourceGenerator;
import com.vsu.amm.data.storage.IDataStorage;
import com.vsu.amm.data.stream.LogFileWriter;
import com.vsu.amm.stat.ICounterSet;
import com.vsu.amm.stat.SimpleCounterSet;
import com.vsu.amm.stat.ICounterSet.OperationType;

public class DataGenerator {
	public static List<Integer> getContersForStorages(
			List<IDataStorage> storages, Integer insertCount,
			Integer selectCount, Integer removeCount) {
		// LogFileWriter logFileWriter = new LogFileWriter(
		// Constants.DEFAULT_LOG_FILE_NAME);
		DataSetPlayer dsp = new DataSetPlayer(storages, null);
		dsp.play(SourceGenerator.createInsertSelectRemoveSource(insertCount,
				selectCount, removeCount));
		// logFileWriter.close();
		List<Integer> result = new ArrayList<>();
		for (int i = 0; i < storages.size(); i++) {
			ICounterSet set = storages.get(i).getCounterSet();
			Integer sumOfOperations = set.get(OperationType.ASSIGN)
					+ set.get(OperationType.COMPARE);
			result.add(sumOfOperations);
		}
		return result;

	}
}

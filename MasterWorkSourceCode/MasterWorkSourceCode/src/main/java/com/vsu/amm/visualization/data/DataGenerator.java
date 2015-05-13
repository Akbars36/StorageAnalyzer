package com.vsu.amm.visualization.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.vsu.amm.Constants;
import com.vsu.amm.command.DataSetPlayer;
import com.vsu.amm.command.ICommandSource;
import com.vsu.amm.command.xmlgen.SourceGenerator;
import com.vsu.amm.data.storage.IDataStorage;
import com.vsu.amm.data.stream.LogFileWriter;
import com.vsu.amm.stat.ICounterSet;
import com.vsu.amm.stat.SimpleCounterSet;
import com.vsu.amm.stat.ICounterSet.OperationType;

public class DataGenerator {

	static int processorsCnt = Runtime.getRuntime().availableProcessors();

	public static List<Integer> getContersForStorages(
			List<IDataStorage> storages, Integer insertCount,
			Integer selectCount, Integer removeCount) {
		// LogFileWriter logFileWriter = new LogFileWriter(
		// Constants.DEFAULT_LOG_FILE_NAME);
		DataSetPlayer dsp = new DataSetPlayer(storages, null);
		ICommandSource source = SourceGenerator.createInsertSelectRemoveSource(insertCount,
				selectCount, removeCount);


		List<StorageThread> threads = new ArrayList<>(storages.size());
		int[] processorsBurden = new int[processorsCnt];
		int baseBurden = storages.size() / processorsCnt;
		int addBurden = storages.size() % processorsCnt;
		for (int i = 0; i<processorsCnt; i++) {
			processorsBurden[i] = baseBurden;
			if (addBurden > 0) {
				addBurden--;
				processorsBurden[i]++;
			}

		}
		for (int i = 0, j=0; i<storages.size(); i+=processorsBurden[j], j++)
				threads.add(new StorageThread(storages.subList(i, processorsBurden[j]),
						source.copy()));

		threads.forEach(DataGenerator.StorageThread::run);

		try {
			for (StorageThread thread : threads)
				thread.join();
		} catch (InterruptedException ignored) {
		}
		List<Integer> result = new ArrayList<>(storages.size());

		for (StorageThread thread : threads) {
			result.addAll(thread.getResult());
		}
		//List<Integer> result = threads.stream().map(StorageThread::getResult).collect(Collectors.toList());
		return result;

		/*
		// logFileWriter.close();
		dsp.play(source);
		List<Integer> result = new ArrayList<>();
		for (int i = 0; i < storages.size(); i++) {
			ICounterSet set = storages.get(i).getCounterSet();
			Integer sumOfOperations = set.get(OperationType.ASSIGN)
					+ set.get(OperationType.COMPARE);
			result.add(sumOfOperations);
		}
		return result;
		*/
	}

	static class StorageThread extends Thread {

		List<IDataStorage> storage = new ArrayList<>(1);
		ICommandSource source;
		public StorageThread (List<IDataStorage> storage, ICommandSource source) {
			super();
			this.storage.addAll(storage);
			this.source = source;
		}

		@Override
		public void run() {
			DataSetPlayer dsp = new DataSetPlayer(storage, null);
			dsp.play(source);
		}

		public List<Integer> getResult() {
			List<Integer> res = new ArrayList<>(storage.size());
			for (int i = 0; i < storage.size(); i++)
				res.add(storage.get(i).getCounterSet().get(OperationType.COMPARE) +
					storage.get(i).getCounterSet().get(OperationType.ASSIGN));

			return res;
		}
	}
}

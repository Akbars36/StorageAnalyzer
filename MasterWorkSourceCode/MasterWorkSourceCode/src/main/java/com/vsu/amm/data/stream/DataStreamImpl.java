package com.vsu.amm.data.stream;

import com.vsu.amm.MasterWorkException;
import com.vsu.amm.data.storage.IDataStorage;
import com.vsu.amm.stat.ICounterSet;
import com.vsu.amm.stat.SimpleCounterSet;
import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataStreamImpl implements IDataStream {
    private static final Logger log = Logger.getLogger(DataStreamImpl.class);
    private final IDataStream dataStream;
    private final List<IDataStorage> dataStorages;
    private boolean first = true;
    private PrintWriter out = null;

    public DataStreamImpl(String filename, IDataStream dataStream, List<IDataStorage> dataStorages) {
        this.dataStream = dataStream;
        this.dataStorages = dataStorages;
        //this.counterSet = counterSet;
        //dataStorage.setCounterSet(counterSet);
        try {
            out = new PrintWriter(filename);
        } catch (FileNotFoundException e) {
            log.error(new MasterWorkException(e.getMessage(), e));
        }
    }

    @Override
    public void label(String label) {
        if (dataStream != null) {
            dataStream.label(label);
        }

    }

    @Override
    public boolean get(int value) {
        if (dataStream != null) {
            dataStream.get(value);
        }
        dataStorages.stream().filter(dataStorage -> dataStorage != null).forEach(dataStorage -> dataStorage.get(value));
        return false;
    }

    @Override
    public boolean set(int value) {
        if (dataStream != null) {
            dataStream.set(value);
        }
        dataStorages.stream().filter(dataStorage -> dataStorage != null).forEach(dataStorage -> dataStorage.set(value));
        return false;
    }

    @Override
    public boolean remove(int value) {
        if (dataStream != null) {
            dataStream.remove(value);
        }
        dataStorages.stream().filter(dataStorage -> dataStorage != null).forEach(dataStorage -> dataStorage.remove(value));
        return false;
    }

    public void flush(String params) {
        Map<String, String> paramsMap = new HashMap<>();
        Map<String, String> storageParams;
        String[] paramsMas = params.split(";");
        String[] temp;
        for (String param : paramsMas) {
            temp = param.split("=");
            paramsMap.put(temp[0], temp[1]);
        }
        StringBuilder builder = new StringBuilder();
        if (first) {
            for (String key : paramsMap.keySet()) {
                builder.append("Param:").append(key).append(",");
            }
            builder.append("Storage").append(",");
            builder.append("Storage Params").append(",");
            builder.append(ICounterSet.OperationType.COMPARE)
                    .append(",")
                    .append(ICounterSet.OperationType.ASSIGN)
                    .append("\r\n");
            first = false;
        }
        IDataStorage bestDataStorage = null;
        long countersBest;
        long countersCurr;
        for (IDataStorage dataStorage : dataStorages) {
            if (bestDataStorage == null) {
                bestDataStorage = dataStorage;
            } else {
                countersBest = bestDataStorage.getCounterSet().get(ICounterSet.OperationType.COMPARE)
                        + bestDataStorage.getCounterSet().get(ICounterSet.OperationType.ASSIGN);
                countersCurr = dataStorage.getCounterSet().get(ICounterSet.OperationType.COMPARE)
                        + dataStorage.getCounterSet().get(ICounterSet.OperationType.ASSIGN);
                if (countersCurr < countersBest) {
                    bestDataStorage = dataStorage;
                }
            }
        }
        for (String key : paramsMap.keySet()) {
            builder.append(paramsMap.get(key)).append(",");
        }
        builder.append(bestDataStorage.getClass().getCanonicalName()).append(",");
        builder.append(",");
        builder.append(bestDataStorage.getCounterSet().get(ICounterSet.OperationType.COMPARE)).append(",")
                .append(bestDataStorage.getCounterSet().get(ICounterSet.OperationType.ASSIGN))
                .append("\r\n");

        out.println(builder.toString());
        for (IDataStorage dataStorage : dataStorages) {
            dataStorage.clear();
            dataStorage.setCounterSet(new SimpleCounterSet());
        }
    }

    public void close() {
        if (out != null) {
            out.flush();
            out.close();
        }
    }
}

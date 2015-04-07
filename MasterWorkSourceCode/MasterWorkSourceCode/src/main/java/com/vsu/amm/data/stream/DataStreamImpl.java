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
    private boolean first = true;
    private IDataStream dataStream;
    private List<IDataStorage> dataStorages;

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
        if (dataStream != null){
            dataStream.label(label);
        }

    }

    @Override
    public void get(int value) {
        if (dataStream != null){
            dataStream.get(value);
        }
        for (IDataStorage dataStorage : dataStorages)
            if (dataStorage != null){
                dataStorage.get(value);
            }
    }

    @Override
    public void set(int value) {
        if (dataStream != null){
            dataStream.set(value);
        }
        for (IDataStorage dataStorage : dataStorages)
            if (dataStorage != null){
                dataStorage.set(value);
            }
    }

    @Override
    public void remove(int value) {
        if (dataStream != null){
            dataStream.remove(value);
        }
        for (IDataStorage dataStorage : dataStorages)
            if (dataStorage != null){
                dataStorage.remove(value);
            }
    }

    public void flush(String params){
        Map<String, String> paramsMap = new HashMap<>();
        Map<String, String> storageParams;
        String[] paramsMas = params.split(";");
        String[] temp;
        for(String param : paramsMas){
            temp = param.split("=");
            paramsMap.put(temp[0], temp[1]);
        }
        StringBuilder builder = new StringBuilder();
        if (first){
            for(String key : paramsMap.keySet()){
                builder.append("Param:").append(key).append(",");
            }
            builder.append("Storage").append(",");
            builder.append("Storage Params").append(",");
            builder.append(ICounterSet.COMPARE)
                    .append(",")
                    .append(ICounterSet.ASSIGN)
                    .append("\r\n");
            first = false;
        }
        IDataStorage bestDataStorage = null;
        int countersBest = 0;
        int countersCurr = 0;
        for (IDataStorage dataStorage :dataStorages){
            if (bestDataStorage == null){
                bestDataStorage = dataStorage;
            } else {
                countersBest = bestDataStorage.getCounterSet().get(ICounterSet.COMPARE)
                        + bestDataStorage.getCounterSet().get(ICounterSet.ASSIGN);
                countersCurr = dataStorage.getCounterSet().get(ICounterSet.COMPARE)
                        + dataStorage.getCounterSet().get(ICounterSet.ASSIGN);
                if (countersCurr < countersBest){
                    bestDataStorage = dataStorage;
                }
            }
        }
        for(String key : paramsMap.keySet()){
            builder.append(paramsMap.get(key)).append(",");
        }
        builder.append(bestDataStorage.getClass().getCanonicalName()).append(",");
        storageParams = bestDataStorage.getStorageParams();
        if (storageParams != null){
            for (String sParam : storageParams.keySet()){
                builder.append(sParam).append(";").append(storageParams.get(sParam));
            }
        }
        builder.append(",");
        builder.append(bestDataStorage.getCounterSet().get(ICounterSet.COMPARE)).append(",")
                .append(bestDataStorage.getCounterSet().get(ICounterSet.ASSIGN))
                .append("\r\n");

        out.println(builder.toString());
        for (IDataStorage dataStorage : dataStorages){
            dataStorage.clear();
            dataStorage.setCounterSet(new SimpleCounterSet());
        }
    }

    public void close(){
        if (out != null){
            out.flush();
            out.close();
        }
    }
}

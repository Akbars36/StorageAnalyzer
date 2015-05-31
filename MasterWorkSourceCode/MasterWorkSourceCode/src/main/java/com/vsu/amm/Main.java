package com.vsu.amm;

import com.vsu.amm.command.DataSetPlayer;
import com.vsu.amm.command.xmlgen.SelectCommandSource;
import com.vsu.amm.command.xmlgen.SequenceCommandSource;
import com.vsu.amm.data.cache.CacheItem;
import com.vsu.amm.data.cache.SimpleCacheArray;
import com.vsu.amm.data.cache.SimpleCacheList;
import com.vsu.amm.data.storage.*;
import com.vsu.amm.data.stream.LogFileWriter;
import com.vsu.amm.load.XMLLoader;
import com.vsu.amm.stat.ICounterSet;
import com.vsu.amm.stat.ICounterSet.OperationType;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Влад
 * Date: 29.10.13
 * Time: 20:30
 * To change this template use File | Settings | File Templates.
 */
class Main {
    private static final Logger log = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        List<IDataStorage> storageList = new ArrayList<>();
        SequenceCommandSource scs = new SequenceCommandSource(10000, 1500, 12000, 3);

        IDataStorage sl = new SortedList();
        storageList.add(sl);

        /*
        SimpleCacheList scl = new SimpleCacheList();
        scl.setInsertionRate(75);
        scl.setSize(100);
        scl.setInnerStorage(new SimpleList());
        storageList.add(scl);
*/
        IDataStorage sa = new SortedArray(10000);
        storageList.add(sa);
/*
        SimpleCacheList sca = new SimpleCacheList();
        sca.setInsertionRate(75);
        sca.setSize(100);
        sca.setInnerStorage(new SimpleArray());
        storageList.add(sca);

        SortedList srtdl = new SortedList();
        storageList.add(srtdl);

        SimpleCacheList csrtdl = new SimpleCacheList();
        csrtdl.setInsertionRate(75);
        csrtdl.setSize(100);
        csrtdl.setInnerStorage(new SortedList());
        storageList.add(csrtdl);

        SortedArray srtda = new SortedArray(1000);
        storageList.add(srtda);

        SimpleCacheList csrtda = new SimpleCacheList();
        csrtda.setInsertionRate(75);
        csrtda.setSize(100);
        csrtda.setInnerStorage(new SortedArray(1000));
        storageList.add(csrtda);
*/
        LinearAdaptiveStorage las = new LinearAdaptiveStorage();
        las.setActiveStorage(new SortedArray());
        las.setReserveStorage(new SortedList());
        las.setIsrCoeffs(new double[]{10, 0.75, -11, 0});
        las.setCalculationsStep(1000);
        storageList.add(las);
/*
        Thread t = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
                    for (Thread t : threadSet)
                        System.out.println(t.toString() + " has state " + t.getState());
                    System.out.println();
                }
            }
        };
        t.setDaemon(true);
        t.start();
*/
        System.out.println(new Date() + " started");
        DataSetPlayer player = new DataSetPlayer(storageList);
        player.play(scs);
        System.out.println(new Date() + " finished");
        for (IDataStorage storage : storageList) {
            ICounterSet cs = storage.getCounterSet();
            System.out.println(storage.getStorageName() + ": " +
                    (cs.get(OperationType.ASSIGN) + cs.get(OperationType.CALCULATION) + cs.get(OperationType.COMPARE)) +
                    " (" + (cs.get(OperationType.DEFERRED_ASSIGN) + cs.get(OperationType.DEFERRED_CALCULATION) +
                    cs.get(OperationType.DEFERRED_COMPARE)) + ")");

            /*
            System.out.println("Assigns: " + cs.get(OperationType.ASSIGN) + "(" + cs.get(OperationType.DEFERRED_ASSIGN) + ")");
            System.out.println("Compares: " + cs.get(OperationType.COMPARE) + "(" + cs.get(OperationType.DEFERRED_COMPARE) + ")");
            */
            System.out.println();
        }

    }
}

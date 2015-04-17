package com.vsu.amm;

import com.vsu.amm.data.LogFilePlayer;
import com.vsu.amm.data.stream.LogFileWriter;
import com.vsu.amm.data.storage.IDataStorage;
import com.vsu.amm.data.storage.SimpleArray;
import com.vsu.amm.load.ProcessLoadXML;
import com.vsu.amm.stat.ICounterSet;
import com.vsu.amm.stat.SimpleCounterSet;
import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Влад
 * Date: 29.10.13
 * Time: 20:30
 * To change this template use File | Settings | File Templates.
 */
public class Main {
    private static final Logger log = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        ProcessGenerateXML processGenerateXML = new ProcessGenerateXML();
        String fileName = "input.xml";
       // processGenerateXML.GenerateXML(fileName);
        ProcessLoadXML processLoadXML = new ProcessLoadXML();

        LogFileWriter logFileWriter = new LogFileWriter(Constants.DEFAULT_OUTPUT_FILE_NAME);
        processLoadXML.processXMLNew(fileName, logFileWriter);
        logFileWriter.close();
    }
}

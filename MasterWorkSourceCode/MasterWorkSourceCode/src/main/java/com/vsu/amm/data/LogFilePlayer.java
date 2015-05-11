package com.vsu.amm.data;

import com.vsu.amm.Constants;
import com.vsu.amm.MasterWorkException;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Влад
 * Date: 13.11.13
 * Time: 20:19
 * To change this template use File | Settings | File Templates.
 */
class LogFilePlayer {

    private static final Logger log = Logger.getLogger(LogFilePlayer.class);

    public void playFile(String filename, IDataContainer storage) {
        String line, command, value;
        int command_si, command_ei;
        int value_si, value_ei;
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            while ((line = br.readLine()) != null) {
                if (line.startsWith("<")) {
                    command_si = line.indexOf("<") + 1;
                    command_ei = line.indexOf(">");
                    command = line.substring(command_si, command_ei);
                    value_si = line.lastIndexOf("<") + 1;
                    value_ei = line.lastIndexOf(">");
                    value = line.substring(value_si, value_ei);
                    switch (command) {
                        case Constants.INSERT_COMMAND_NAME:
                            storage.set(Integer.parseInt(value));
                            break;
                        case Constants.SELECT_COMMAND_NAME:
                            storage.get(Integer.parseInt(value));
                            break;
                        case Constants.REMOVE_COMMAND_NAME:
                            storage.remove(Integer.parseInt(value));
                            break;
                        default:
                            break;
                    }
                }
            }
            br.close();
        } catch (IOException e) {
            log.error(new MasterWorkException(e.getMessage(), e)); //To change body of catch statement use File | Settings | File Templates.
        }

    }
}

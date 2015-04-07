package com.vsu.amm.data.stream;

import com.vsu.amm.Constants;
import com.vsu.amm.MasterWorkException;
import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Created with IntelliJ IDEA.
 * User: Влад
 * Date: 13.11.13
 * Time: 20:17
 * To change this template use File | Settings | File Templates.
 */
public class LogFileWriter implements IDataStream {

    private static final Logger log = Logger.getLogger(LogFileWriter.class);

    private PrintWriter out = null;


    public LogFileWriter(String filename)
    {
        try {
            out = new PrintWriter(filename);
        } catch (FileNotFoundException e) {
            log.error(new MasterWorkException(e.getMessage(),e));
        }

    }

    public void close(){
        if (out != null){
            out.flush();
            out.close();
        }
    }

    @Override
    public void get(int value) {
        //To change body of implemented methods use File | Settings | File Templates.
        if (out != null){
            out.println("<" + Constants.SELECT_COMMAND_NAME + "> <"+ value +">");
        }
    }

    @Override
    public void set(int value) {
        //To change body of implemented methods use File | Settings | File Templates.
        if (out != null){
            out.println("<" + Constants.INSERT_COMMAND_NAME + "> <"+ value +">");
        }
    }

    @Override
    public void remove(int value) {
        //To change body of implemented methods use File | Settings | File Templates.
        if (out != null){
            out.println("<" + Constants.REMOVE_COMMAND_NAME + "> <"+ value +">");
        }
    }

    @Override
    public void label(String label) {
        //To change body of implemented methods use File | Settings | File Templates.
        if (out != null){
            out.println(label);
        }
    }

}

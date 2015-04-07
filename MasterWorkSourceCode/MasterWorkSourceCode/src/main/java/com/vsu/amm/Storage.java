package com.vsu.amm;

import java.lang.Class;
import com.vsu.amm.data.storage.IDataStorage;

import java.util.Map;

public class Storage {

    String storageClass;
    Map<String, String> params;

    public Storage(String storageClass, Map<String, String> params) {
        this.storageClass = storageClass;
        this.params = params;
    }

    public IDataStorage getStorage(){
        IDataStorage storage = null;
        try {
            Class c = Class.forName(storageClass);
            Object obj = c.newInstance();
            if (obj instanceof IDataStorage){
                storage = (IDataStorage)obj;
                storage.setStorageParams(params);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            //log.error(new MasterWorkException(e.getMessage(), e));
        } catch (InstantiationException e) {
            e.printStackTrace();
            //log.error(new MasterWorkException(e.getMessage(), e));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            //log.error(new MasterWorkException(e.getMessage(), e));
        }
        return storage;
    }
}

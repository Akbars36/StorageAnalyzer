package com.vsu.amm;

import java.lang.Class;


import java.util.HashMap;
import java.util.Map;

import org.jdom2.Element;

import com.vsu.amm.data.storage.IDataStorage;

public class Storage {

    String storageClass;
    Map<String, String> storageParams;

    public Storage(Element elem) {
        String storageParamName;
        String storageParamValue;
    	storageClass = elem.getAttributeValue("class");
        storageParams = new HashMap<>();
        for (Element storageParam : elem.getChildren()) {
            storageParamName = storageParam.getAttributeValue("name");
            storageParamValue = storageParam.getAttributeValue("value");
            storageParams.put(storageParamName, storageParamValue);
        }
    }
    
    public Storage(String storageClass, Map<String, String> params) {
        this.storageClass = storageClass;
        this.storageParams = params;
    }

    public IDataStorage getStorage(){
    	IDataStorage storage = null;
        try {
            Class c = Class.forName(storageClass);
            Object obj = c.newInstance();
            if (obj instanceof IDataStorage){
                storage = (IDataStorage)obj;
                storage.setStorageParams(storageParams);
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

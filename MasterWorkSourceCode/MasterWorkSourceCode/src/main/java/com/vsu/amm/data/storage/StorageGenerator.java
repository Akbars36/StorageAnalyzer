package com.vsu.amm.data.storage;

import com.vsu.amm.Constants;
import com.vsu.amm.Storage;
import com.vsu.amm.stat.SimpleCounterSet;

import org.apache.log4j.Logger;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.List;

import static com.vsu.amm.Utils.isNullOrEmpty;

/**
 * Created by Kzarw on 25.04.2015.
 */
public class StorageGenerator {

    public static final Logger log = Logger.getLogger(StorageGenerator.class);


    public static List<IDataStorage> generateStorages(Element element) {
        if (element == null)
            return null;

        if (!Constants.STORAGES_TAG_NAME.equals(element.getName()))
            return null;

        List<Element> children = element.getChildren();
        if (children == null) {
            return null;
        }

        List<IDataStorage> storages = new ArrayList<>(children.size());

        for (Element child : children) {
            if (!"storage".equals(child.getName())) {
                log.warn("Unknown element " + child.getName() + ". Skipped.");
                continue;
            }
            String c = child.getAttributeValue("class");
            if (isNullOrEmpty(c)) {
                log.warn("Class name not set. Skipped.");
                continue;
            }
            Storage storage = new Storage(child);
            if (storage != null){
            	IDataStorage stor=storage.getStorage();
            	stor.setCounterSet(new SimpleCounterSet());
                storages.add(stor);
            }
        }

        return storages;
    }

    public static IDataStorage getDataStorage(String storageName) {
    	IDataStorage result;
        if (storageName == null)
            return null;
        try {
            Object o = Class.forName(storageName).newInstance();
            result=(IDataStorage) o;
            
            return (IDataStorage) o;
        } catch (Exception e) {
            log.error("Cannot find or create class");
        }
        return null;
    }

    //not yet implemented
    public static IDataStorage getDataStorage(String storageTitle, String cacheTitle) {
        return null;
    }
}

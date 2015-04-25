package com.vsu.amm.load;

import com.vsu.amm.Burden;
import com.vsu.amm.Constants;
import com.vsu.amm.Storage;
import com.vsu.amm.data.storage.IDataStorage;
import com.vsu.amm.data.stream.DataStreamImpl;
import com.vsu.amm.data.stream.IDataStream;
import com.vsu.amm.stat.ICounterSet;
import com.vsu.amm.stat.SimpleCounterSet;
import javafx.util.Pair;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Влад
 * Date: 13.11.13
 * Time: 20:12
 * To change this template use File | Settings | File Templates.
 */
public class ProcessLoadXML {
    public static final String SEQUENCE_NAME = "sequence";
    public static final String BLOCK_NAME = "block";
    public static final String INSERT_NAME = "insert";
    public static final String SELECT_NAME = "select";
    public static final String REMOVE_NAME = "remove";
    private static final Logger log = Logger.getLogger(ProcessLoadXML.class);
    private static HashMap<Integer, Burden> tags;
    private static HashMap<String, Integer> aliases;
    private static HashMap<String, List<String>> paramsValues;

    private static List<Pair<String, String>> generate(Burden burden) throws Exception {
        Pair<String, String> resultValue;
        List<Pair<String, String>> result = new LinkedList<Pair<String, String>>();
        List<Integer> children;
        int count;
        String name = burden.getName();
        switch (Burden.TagNames.valueOf(name.toUpperCase())) {
            case SEQUENCE:
                count = Integer.parseInt(burden.getCount());
                if ((burden.getLabel() != null) && ((burden.getId() == burden.getParentId()) || (!BLOCK_NAME.equals(tags.get(burden.getParentId()).getName())))) {
                    resultValue = new Pair<String, String>(Constants.LABEL_COMMAND_NAME, burden.getLabel());
                    result.add(resultValue);
                }
                for (int index = 1; index <= count; index++) {
                    children = burden.getChildrenId();
                    if (children != null) {
                        for (int i : children) {
                            result.addAll(generate(tags.get(i)));
                        }
                    }
                }
                break;
            case BLOCK:
                HashMap<Integer, List<Pair<String, String>>> values = new HashMap<Integer, List<Pair<String, String>>>();
                count = Integer.parseInt(burden.getCount());
                if ((burden.getLabel() != null) && ((burden.getId() == burden.getParentId()) || (!BLOCK_NAME.equals(tags.get(burden.getParentId()).getName())))) {
                    resultValue = new Pair<String, String>(Constants.LABEL_COMMAND_NAME, burden.getLabel());
                    result.add(resultValue);
                }
                for (int index = 1; index <= count; index++) {
                    children = burden.getChildrenId();
                    if (children != null) {
                        for (int i : children) {
                            values.put(i, generate(tags.get(i)));
                        }
                        Random random = new Random();
                        int childrenCount = children.size();
                        int i, k, c, id, size, begin, end, elCount;
                        Burden temp;
                        Pair<String, String> el;

                        while (values.size() != 0) {
                            i = random.nextInt(childrenCount);
                            id = children.get(i);
                            temp = tags.get(id);
                            if (SEQUENCE_NAME.equals(temp.getName()) || BLOCK_NAME.equals(temp.getName())) {
                                c = Integer.parseInt(temp.getTempCount());
                                if (values.get(id) != null) {
                                    size = values.get(id).size();
                                    /*if (values.get(id).get(0).equals(temp.getLabel())){
                                        size -= 1;
                                        begin = 1;
                                    } else {
                                        begin = 0;
                                    }*/
                                    begin = 0;
                                    elCount = size / c;
                                    k = random.nextInt(c);
                                    begin = k * elCount + begin;
                                    end = begin + elCount - 1;
                                    result.addAll(values.get(id).subList(begin, end + 1));
                                    for (int j = end; j >= begin; j--) {
                                        values.get(id).remove(j);
                                    }
                                    temp.setTempCount(String.valueOf(c - 1));
                                    if (values.get(id).size() == 0) {
                                        values.remove(id);
                                        temp.setTempCount(temp.getCount());
                                    }
                                }
                            } else {
                                if (values.get(id) != null) {
                                    size = values.get(id).size();
                                    k = random.nextInt(size);
                                    el = values.get(id).get(k);
                                    values.get(id).remove(k);
                                    result.add(el);
                                    if (values.get(id).size() == 0) {
                                        values.remove(id);
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            case INSERT:
                result.addAll(generateInsert(burden));
                break;
            case SELECT:
                result.addAll(generateSelect(burden));
                break;
            case REMOVE:
                result.addAll(generateRemove(burden));
                break;
            default:
                break;
        }
        return result;
    }

    private static List<Pair<String, String>> generateInsert(Burden insert) throws Exception {
        Pair<String, String> resultValue;
        List<Pair<String, String>> result = new LinkedList<Pair<String, String>>();
        Random randomGenerator = new Random();
        String alias = insert.getAlias();
        List<Integer> keys = new LinkedList<Integer>();
        int key;
        int count = Integer.parseInt(insert.getCount());
        boolean range = false;
        int min = 0;
        int max = 0;
        if ((insert.getMin() != null) && (insert.getMax() != null)) {
            range = true;
            min = Integer.parseInt(insert.getMin());
            max = Integer.parseInt(insert.getMax());
        }
        if ((insert.getLabel() != null) && (!BLOCK_NAME.equals(tags.get(insert.getParentId()).getName()))) {
            resultValue = new Pair<String, String>(Constants.LABEL_COMMAND_NAME, insert.getLabel());
            result.add(resultValue);
        }
        for (int index = 1; index <= count; index++) {
            if (range) {
                key = randomGenerator.nextInt((max - min) + 1) + min;
            } else {
                key = randomGenerator.nextInt(Integer.MAX_VALUE);
            }
            keys.add(key);
            resultValue = new Pair<String, String>(Constants.INSERT_COMMAND_NAME, String.valueOf(key));
            result.add(resultValue);
        }
        if (alias != null) {
            insert.setGenKeyValueToAlias(keys);
        }
        return result;
    }

    private static List<Pair<String, String>> generateSelect(Burden select) throws Exception {
        Pair<String, String> resultValue;
        List<Pair<String, String>> result = new LinkedList<Pair<String, String>>();
        Random randomGenerator = new Random();
        int key;
        int count = Integer.parseInt(select.getCount());
        List<Integer> keys = null;
        String from = select.getFrom();
        if (from != null) {
            keys = tags.get(aliases.get(from)).getGenKeyValueToAlias();
            if (keys == null) {
                throw new Exception("Not found the key values");
            }
        }
        if ((select.getLabel() != null) && (!BLOCK_NAME.equals(tags.get(select.getParentId()).getName()))) {
            resultValue = new Pair<String, String>(Constants.LABEL_COMMAND_NAME, select.getLabel());
            result.add(resultValue);
        }
        for (int index = 1; index <= count; index++) {
            if (from != null) {
                key = keys.get(randomGenerator.nextInt(keys.size()));
            } else {
                key = randomGenerator.nextInt(Integer.MAX_VALUE);
            }
            resultValue = new Pair<String, String>(Constants.SELECT_COMMAND_NAME, String.valueOf(key));
            result.add(resultValue);
        }
        return result;
    }

    private static List<Pair<String, String>> generateRemove(Burden remove) throws Exception {
        Pair<String, String> resultValue;
        List<Pair<String, String>> result = new LinkedList<Pair<String, String>>();
        Random randomGenerator = new Random();
        int key;
        int count = Integer.parseInt(remove.getCount());
        List<Integer> keys = null;
        String from = remove.getFrom();
        if (from != null) {
            keys = tags.get(aliases.get(from)).getGenKeyValueToAlias();
            if (keys == null) {
                throw new Exception("Not found the key values");
            }

        }
        if ((remove.getLabel() != null) && (!BLOCK_NAME.equals(tags.get(remove.getParentId()).getName()))) {
            resultValue = new Pair<String, String>(Constants.LABEL_COMMAND_NAME, remove.getLabel());
            result.add(resultValue);
        }
        for (int index = 1; index <= count; index++) {
            if (from != null) {
                key = keys.get(randomGenerator.nextInt(keys.size()));
            } else {
                key = randomGenerator.nextInt(Integer.MAX_VALUE);
            }
            resultValue = new Pair<String, String>(Constants.REMOVE_COMMAND_NAME, String.valueOf(key));
            result.add(resultValue);
        }
        return result;
    }
     /*
    public void processXML(String filename, IDataStream storage){
        tags = new HashMap<Integer, Burden>();
        aliases = new HashMap<String, Integer>();

        Queue<Element> elementsQueue = new LinkedList();
        Queue<Integer> idsQueue = new LinkedList();

        int currentId = 0;
        int parentId = 0;
        int tempId = parentId;
        Burden burden;
        Document document;
        Element element, rootNode;

        String count = null;
        String min = null;
        String max = null;
        String label = null;
        String alias = null;
        String from = null;
        List<Element> children;
        List<Integer> childrenId;

        SAXBuilder builder = new SAXBuilder();
        File xmlFile = new File(filename);
        try {
            document = builder.build(xmlFile);
            rootNode = document.getRootElement();
            elementsQueue.add(rootNode);
            idsQueue.add(parentId);
            childrenId = new LinkedList<Integer>();
            while(!elementsQueue.isEmpty()){
                element = elementsQueue.poll();
                parentId = idsQueue.poll();

                count = element.getAttribute("count") != null ? element.getAttributeValue("count") : null;
                min = element.getAttribute("min") != null ? element.getAttributeValue("min") : null;
                max = element.getAttribute("max") != null ? element.getAttributeValue("max") : null;
                label = element.getAttribute("label") != null ? element.getAttributeValue("label") : null;
                if (element.getAttribute("alias") != null){
                    alias = element.getAttributeValue("alias");
                    if (aliases.containsKey(alias)){
                        throw  new Exception("This alias already exists");
                    } else {
                        aliases.put(alias, currentId);
                    }

                } else {
                    alias = null;
                }

                from = element.getAttribute("from") != null ? element.getAttributeValue("from") : null;

                burden = new Burden(currentId, parentId, element.getName(), count, label, alias, from, min, max);
                if ((currentId != 0)){
                    if (parentId == tempId){
                        childrenId.add(currentId);
                    } else {
                        tags.get(tempId).setChildrenId(childrenId);
                        childrenId = new LinkedList<Integer>();
                        childrenId.add(currentId);
                        tempId = parentId;
                    }
                }

                tags.put(currentId, burden);
                children = (List<Element>)element.getChildren();
                for (Element e : children){
                    elementsQueue.add(e);
                    idsQueue.add(currentId);
                }
                currentId++;
            }
            if (elementsQueue.isEmpty()){
                tags.get(tempId).setChildrenId(childrenId);
            }
            Burden rootBurden = tags.get(0);
            List<Pair<String, String>> result = generate(rootBurden);
            for(Pair<String, String> pair  : result){
                switch (pair.getKey()){
                    case Constants.INSERT_COMMAND_NAME:
                        storage.add(Integer.parseInt(pair.getValue()));
                        break;
                    case Constants.SELECT_COMMAND_NAME:
                        storage.get(Integer.parseInt(pair.getValue()));
                        break;
                    case Constants.REMOVE_COMMAND_NAME:
                        storage.remove(Integer.parseInt(pair.getValue()));
                        break;
                    case Constants.LABEL_COMMAND_NAME:
                        storage.label(pair.getValue());
                        break;
                    default:
                        break;
                }
            }
        } catch (JDOMException e) {
            e.printStackTrace();
            //log.error(new MasterWorkException(e.getMessage(), e));
        } catch (IOException e) {
            e.printStackTrace();
            //log.error(new MasterWorkException(e.getMessage(), e));
        } catch (Exception e) {
            e.printStackTrace();
            //log.error(new MasterWorkException(e.getMessage(), e));
        }
    }     */

    private void generateParams(List<String> params, List<String> result, String resultString) {
        if (params.size() == 0) {
            result.add(resultString);
        } else {
            String name = params.get(0);
            for (String value : paramsValues.get(name)) {
                if (params.size() != 1) {
                    generateParams(params.subList(1, params.size()), result, resultString + name + "=" + value + ";");
                } else {
                    generateParams(new ArrayList<String>(), result, resultString + name + "=" + value + ";");
                }
            }
        }

    }

    public void processXMLNew(String filename, IDataStream stream) {
        //IDataStorage dataStorage;
        List<IDataStorage> dataStorages = new LinkedList<>();
        ICounterSet counterSet;

        int index;
        Burden burden;
        Document document;
        Element element, rootNode, storagesNode, dataNode, paramsNode;


        Map<String, String> paramMap;

        String param_name = null;
        String count = null;
        String min = null;
        String max = null;
        String label = null;
        String alias = null;
        String from = null;
        List<Element> children;
        List<Integer> childrenId;

        paramsValues = new HashMap<>();

        Queue<Element> elementsQueue = new LinkedList();
        Queue<Integer> idsQueue = new LinkedList();
        List<String> param_names;
        List<String> param_values;
        List<String> params_values;

        SAXBuilder builder = new SAXBuilder();
        File xmlFile = new File(filename);
        try {
            document = builder.build(xmlFile);
            rootNode = document.getRootElement();

            //Get storages
            storagesNode = rootNode.getChild(Constants.STORAGES_TAG_NAME);
            for (Element storageNode : storagesNode.getChildren()) {
                Storage storage = new Storage(storageNode);
                dataStorages.add(storage.getStorage());
            }
            /*storageNode = rootNode.getChild(Constants.STORAGE_TAG_NAME);
            storageClass = storageNode.getAttributeValue("class");
            storageParams = new HashMap<>();
            for(Element storageParam : (List<Element>)storageNode.getChildren()){
                storageParamName = storageParam.getAttributeValue("name");
                storageParamValue = storageParam.getAttributeValue("value");
                storageParams.put(storageParamName, storageParamValue);
            }
            Storage storage = new Storage(storageClass, storageParams);
            dataStorage = storage.getStorage();          */

            counterSet = new SimpleCounterSet();
            for (IDataStorage dataStorage : dataStorages) {
                dataStorage.setCounterSet(new SimpleCounterSet());
            }
            DataStreamImpl dataStream = new DataStreamImpl(Constants.CSV_OUTPUT_FILE_NAME, stream, dataStorages);

            //Get parameters values
            paramsNode = rootNode.getChild(Constants.PARAMS_TAG_NAME);
            for (Element param : paramsNode.getChildren()) {
                param_name = param.getAttributeValue("name");
                param_values = new ArrayList<>();
                for (Element value : param.getChildren()) {
                    param_values.add(value.getText());
                }
                paramsValues.put(param_name, param_values);
            }
            param_names = new ArrayList<>(paramsValues.keySet());
            params_values = new ArrayList<>();
            generateParams(param_names, params_values, "");

            //Generate burden
            int currentId;
            int parentId;
            int tempId;
            for (String params : params_values) {
                currentId = 0;
                parentId = 0;
                tempId = parentId;
                tags = new HashMap<Integer, Burden>();
                aliases = new HashMap<String, Integer>();


                paramMap = new HashMap<>();
                for (String s : params.substring(0, params.length() - 1).split(";", -1)) {
                    index = s.indexOf("=");
                    if (index != -1) {
                        paramMap.put(s.substring(0, index), s.substring(index + 1));
                    }
                }

                dataNode = rootNode.getChild(Constants.DATA_TAG_NAME);
                elementsQueue.addAll(dataNode.getChildren());
                idsQueue.add(parentId);
                childrenId = new LinkedList<Integer>();
                while (!elementsQueue.isEmpty()) {
                    element = elementsQueue.poll();
                    parentId = idsQueue.poll();

                    count = element.getAttribute("count") != null ? element.getAttributeValue("count") : null;
                    count = paramFromMap(paramMap, count);

                    min = element.getAttribute("min") != null ? element.getAttributeValue("min") : null;
                    min = paramFromMap(paramMap, min);


                    max = element.getAttribute("max") != null ? element.getAttributeValue("max") : null;
                    max = paramFromMap(paramMap, max);


                    label = element.getAttribute("label") != null ? element.getAttributeValue("label") : null;
                    label = paramFromMap(paramMap, label);

                    if (element.getAttribute("alias") != null) {
                        alias = element.getAttributeValue("alias");
                        alias = paramFromMap(paramMap, alias);

                        if (aliases.containsKey(alias)) {
                            throw new Exception("This alias already exists");
                        } else {
                            aliases.put(alias, currentId);
                        }

                    } else {
                        alias = null;
                    }

                    from = element.getAttribute("from") != null ? element.getAttributeValue("from") : null;
                    from = paramFromMap(paramMap, from);


                    burden = new Burden(currentId, parentId, element.getName(), count, label, alias, from, min, max);
                    if ((currentId != 0)) {
                        if (parentId == tempId) {
                            childrenId.add(currentId);
                        } else {
                            tags.get(tempId).setChildrenId(childrenId);
                            childrenId = new LinkedList<Integer>();
                            childrenId.add(currentId);
                            tempId = parentId;
                        }
                    }

                    tags.put(currentId, burden);
                    children = element.getChildren();
                    for (Element e : children) {
                        elementsQueue.add(e);
                        idsQueue.add(currentId);
                    }
                    currentId++;
                }
                if (elementsQueue.isEmpty()) {
                    tags.get(tempId).setChildrenId(childrenId);
                }
                Burden rootBurden = tags.get(0);
                List<Pair<String, String>> result = generate(rootBurden);
                result.add(0, new Pair<String, String>(Constants.LABEL_COMMAND_NAME, "Params:" + params));
                for (Pair<String, String> pair : result) {
                    switch (pair.getKey()) {
                        case Constants.INSERT_COMMAND_NAME:
                            dataStream.set(Integer.parseInt(pair.getValue()));
                            break;
                        case Constants.SELECT_COMMAND_NAME:
                            dataStream.get(Integer.parseInt(pair.getValue()));
                            break;
                        case Constants.REMOVE_COMMAND_NAME:
                            dataStream.remove(Integer.parseInt(pair.getValue()));
                            break;
                        case Constants.LABEL_COMMAND_NAME:
                            dataStream.label(pair.getValue());
                            break;
                        default:
                            break;
                    }
                }
                dataStream.flush(params);
            }

            dataStream.close();


        } catch (JDOMException e) {
            e.printStackTrace();
            //log.error(new MasterWorkException(e.getMessage(), e));
        } catch (IOException e) {
            e.printStackTrace();
            //log.error(new MasterWorkException(e.getMessage(), e));
        } catch (Exception e) {
            e.printStackTrace();
            //log.error(new MasterWorkException(e.getMessage(), e));
        }
    }

    private String paramFromMap(Map<String, String> paramMap, String param) {
        if (param == null) {
            return null;
        }
        String result = param;
        String paramName;
        if (param.startsWith("%") && param.endsWith("%")) {
            paramName = param.substring(1, param.length() - 1);
            result = paramMap.get(paramName);
        }
        return result;
    }

}

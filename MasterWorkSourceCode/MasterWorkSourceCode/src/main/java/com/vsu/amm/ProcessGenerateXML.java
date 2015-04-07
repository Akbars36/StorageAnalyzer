package com.vsu.amm;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.lang.annotation.ElementType;

/**
 * Created by VLAD on 16.04.14.
 */
public class ProcessGenerateXML {
    private static final Logger log = Logger.getLogger(ProcessGenerateXML.class);

    public String GenerateXML(String fileName){
        try{
            Element dataElement = new Element("data");
            Element storageElement = new Element("storages");
            Element paramsElement = new Element("param_values");

            System.out.println("Input burden:");
            InputStreamReader isr = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(isr);
            Element burdenElement;
            Element rootBurden = new Element("sequence");
            String command = "y";
            String burdenName;
            String burdenCount;
            String burdenAlias;
            String burdenLabel;
            String burdenMin;
            String burdenMax;
            while ("y".equals(command.toLowerCase())){
                System.out.print("Burden Type (I - Insert, S - Select, R - Remove):");
                do {
                    burdenName = br.readLine();
                    if (burdenName == null || burdenName.equals("")){
                        System.out.println("This attribute cannot be empty!");
                    }
                } while (burdenName == null || burdenName.equals(""));

                System.out.print("Count:");
                burdenCount = br.readLine();
                System.out.print("Alias:");
                burdenAlias = br.readLine();
                System.out.print("Label:");
                burdenLabel = br.readLine();
                System.out.print("Min:");
                burdenMin = br.readLine();
                System.out.print("Max:");
                burdenMax = br.readLine();

                switch (burdenName){
                    case "I":
                        burdenElement = new Element("insert");
                        break;
                    case "S":
                        burdenElement = new Element("select");
                        break;
                    case "R":
                        burdenElement = new Element("remove");
                        break;
                    default:
                        burdenElement = null;
                        break;
                }

                if (burdenElement == null){
                    System.out.println("Wrong burden!");
                }

                if (!burdenCount.isEmpty()){
                    burdenElement.setAttribute("count", burdenCount);
                } else {
                    burdenElement.setAttribute("count", "1");
                }

                if (!burdenAlias.isEmpty()){
                    burdenElement.setAttribute("alias", burdenAlias);
                }

                if (!burdenLabel.isEmpty()){
                    burdenElement.setAttribute("label", burdenLabel);
                }

                if (!burdenMin.isEmpty()){
                    burdenElement.setAttribute("min", burdenMin);
                }

                if (!burdenMax.isEmpty()){
                    burdenElement.setAttribute("max", burdenMax);
                }
                rootBurden.addContent(burdenElement);

                System.out.println("Do you want to add new burden? (y/n)");
                command = br.readLine();
            }
            Element rootElement = new Element("burden");
            dataElement.addContent(rootBurden);
            rootElement.addContent(dataElement);

            Element storage;
            Element storageParam;
            String storageClass;
            String storageParamName;
            String storageParamValue;
            command = "y";
            while ("y".equals(command.toLowerCase())){
                storage = new Element("storage");
                do {
                    System.out.print("Storage class:");
                    storageClass = br.readLine();
                    if (storageClass == null || storageClass.equals("")){
                        System.out.println("This attribute cannot be empty!");
                    }
                } while (storageClass == null || storageClass.equals(""));
                storage.setAttribute("class", storageClass);

                System.out.println("Do you want to add storage parameters? (y/n)");
                command = br.readLine();
                while ("y".equals(command)){
                    do {
                        System.out.print("Storage parameter name:");
                        storageParamName = br.readLine();
                        if (storageParamName == null || storageParamName.equals("")){
                            System.out.println("This attribute cannot be empty!");
                        }
                    } while (storageParamName == null || storageParamName.equals(""));

                    do {
                        System.out.print("Storage parameter value:");
                        storageParamValue = br.readLine();
                        if (storageParamValue == null || storageParamValue.equals("")){
                            System.out.println("This attribute cannot be empty!");
                        }
                    } while (storageParamValue == null || storageParamValue.equals(""));

                    storage.addContent(new Element("param").setAttribute("name", storageParamName).setAttribute("value", storageParamValue));
                    System.out.println("Do you want to add any storage parameter? (y/n)");
                    command = br.readLine();
                }
                storageElement.addContent(storage);
                System.out.println("Do you want to add another storage? (y/n)");
                command = br.readLine();
            }
            rootElement.addContent(storageElement);


            String paramName;
            String paramValue;
            System.out.println("Do you want to add any parameters? (y/n)");
            command = br.readLine();
            Element param;
            while ("y".equals(command.toLowerCase())){
                param = new Element("param");
                do {
                    System.out.print("Parameter name:");
                    paramName = br.readLine();
                    if (paramName == null || paramName.equals("")){
                        System.out.println("This attribute cannot be empty!");
                    }
                } while (paramName == null || paramName.equals(""));
                param.setAttribute("name", paramName);

                do {
                    System.out.println("Parameter values (in a row with spaces):");
                    paramValue = br.readLine();
                    if (paramValue == null || paramValue.equals("")){
                        System.out.println("This attribute cannot be empty!");
                    }
                } while (paramValue == null || paramValue.equals(""));
                for (String value : paramValue.split(" ")){
                    param.addContent(new Element("value").addContent(value));
                }
                paramsElement.addContent(param);
                System.out.println("Do you want to add new parameter? (y/n)");
                command = br.readLine();
            }

            rootElement.addContent(paramsElement);
            Document document = new Document(rootElement);
            XMLOutputter xmlOutput = new XMLOutputter();

            xmlOutput.setFormat(Format.getPrettyFormat());
            xmlOutput.output(document, new FileWriter(fileName));
        }
        catch (Exception e){
            log.error(e);
        }
        return fileName;
    }
}

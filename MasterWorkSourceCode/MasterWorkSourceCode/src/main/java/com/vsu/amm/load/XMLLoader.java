package com.vsu.amm.load;

import com.vsu.amm.Constants;
import com.vsu.amm.Utils;
import com.vsu.amm.command.ICommandSource;
import com.vsu.amm.command.xmlgen.SourceGenerator;
import com.vsu.amm.data.storage.IDataStorage;
import com.vsu.amm.data.storage.StorageGenerator;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Kzarw on 25.04.2015.
 */
public class XMLLoader {

	Logger log = Logger.getLogger(XMLLoader.class);

	Parameters parameters;
	public List<IDataStorage> storages;

	public List<ICommandSource> commands;

	public void ParseXML(String filename) {
		if (Utils.isNullOrEmpty(filename))
			return;

		File xmlFile = new File(filename);

		SAXBuilder builder = new SAXBuilder();

		try {
			Document document = builder.build(xmlFile);
			Element root = document.getRootElement();

			// first get params of the xml file
			Element element = root.getChild(Constants.PARAMS_TAG_NAME);

			parameters = new Parameters(element);

			Map<String, Integer> defaultParams = new HashMap<>(parameters
					.getParameters().size());
			parameters.getParameters().forEach(
					(name, values) -> defaultParams.put(name, Integer.parseInt(values.get(0))));
//			for (String params : parameters.getGenerateParametrs()) {
//                Map<String,String> paramMap = new HashMap<>();
//                for (String s : params.substring(0, params.length() - 1).split(";", -1)) {
//                    int index = s.indexOf("=");
//                    if (index != -1) {
//                        paramMap.put(s.substring(0, index), s.substring(index + 1));
//                    }
//                }
				// get storages from xml
				element = root.getChild(Constants.STORAGES_TAG_NAME);

				storages = StorageGenerator.generateStorages(element);
				if (storages == null || storages.size() == 0) {
					log.error("Storages not found!");
					return;
				}

				// get data from storages
				element = root.getChild(Constants.DATA_TAG_NAME);
				if (element == null) {
					log.error("Nothing to test!");
					return;
				}

				List<Element> commandElems = element.getChildren();
				if (commandElems == null) {
					log.error("Nothing to test!");
					return;
				}
				commands = new ArrayList<>(commandElems.size());

				commandElems.forEach(cmd -> commands.add(SourceGenerator
						.create(cmd, defaultParams)));

//			}
		} catch (IOException io) {

		} catch (JDOMException e) {
			e.printStackTrace();
		}
	}
}

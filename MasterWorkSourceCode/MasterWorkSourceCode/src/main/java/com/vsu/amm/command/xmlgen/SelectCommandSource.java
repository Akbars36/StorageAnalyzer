package com.vsu.amm.command.xmlgen;

import com.vsu.amm.Constants;
import com.vsu.amm.Utils;
import com.vsu.amm.command.SelectCommand;
import com.vsu.amm.data.stream.IDataStream;
import com.vsu.amm.command.ICommand;
import com.vsu.amm.command.ICommandSource;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SelectCommandSource extends SimpleCommandSource {

    public SelectCommandSource(Element elem, AliasSet aliasSet, Map<String, Integer> params) {
        if (elem == null)
            return;

        String from = elem.getAttributeValue("from");
        if (aliasSet != null)
            valueSet = aliasSet.getAlias(from);

        boolean reuseValues = valueSet != null;

        Integer tmp;

        if (valueSet == null) {

            String minAttr = elem.getAttributeValue("min");
            String maxAttr = elem.getAttributeValue("max");

            valueSet = new StandardRandomValueSet(minAttr, maxAttr, params);
        }

        String alias = elem.getAttributeValue("alias");

        if (!Utils.isNullOrEmpty(alias) && aliasSet != null)
            aliasSet.putAlias(alias, valueSet);

        tmp = getAttributeValue(elem, "count", params);
        int count = tmp == null ? 1 : tmp;

        commands = new ArrayList<>(count);

        if (reuseValues && valueSet.hasStoredValues())
            for (int i = 0; i < count; i++)
                commands.add(new SelectCommand(valueSet.reuseRandom().intValue()));
        else if (!Utils.isNullOrEmpty(alias))
            for (int i = 0; i < count; i++)
                commands.add(new SelectCommand(valueSet.generateRandomAndStore()));
        else
            for (int i = 0; i < count; i++) {
                commands.add(new SelectCommand(valueSet.generateRandom()));
            }

        label = elem.getAttributeValue("label");
    }
	public SelectCommandSource(Integer selectCount, AliasSet aliasSet) {
		valueSet = new StandardRandomValueSet(0, Constants.DEFAULT_MAX_VALUE);
		valueSet = aliasSet.getAlias(Constants.DEFAULT_ALIAS_NAME);
		if (valueSet.hasStoredValues()) {
			commands = new ArrayList<>(selectCount);
			for (int i = 0; i < selectCount; i++)
				commands.add(new SelectCommand(valueSet.reuseRandom()));
		}
	}

	private Integer getAttributeValue(Element element, String attributeName,
			Map<String, Integer> params) {
		String strAttribute = element.getAttributeValue(attributeName);
		if (strAttribute == null)
			return null;

		if (strAttribute.startsWith("%") && strAttribute.endsWith("%")) {
			strAttribute = strAttribute.substring(1, strAttribute.length() - 1);
			return params != null ? params.get(strAttribute) : null;
		}

		Integer attrValue = null;

		try {
			attrValue = Integer.parseInt(strAttribute);
		} catch (Exception ex) {
			System.out.println("Cannot parse parameter: " + attributeName);
		}

		return attrValue;
	}

	@Override
	public void restart() {
		currentCommand = 0;
	}

	@Override
	public ICommand next() {
		if (commands == null)
			return null;

		if (commands.size() <= currentCommand)
			return null;

		return commands.get(currentCommand++);
	}

	@Override
	public void printToStream(IDataStream stream) {
		if (commands == null)
			return;

		if (stream == null)
			return;

		if (label != null)
			stream.label(label);

		commands.forEach(iCommand -> iCommand.printToStream(stream));
	}

	public List<ICommand> getCommands() {
		return commands;
	}
    
}

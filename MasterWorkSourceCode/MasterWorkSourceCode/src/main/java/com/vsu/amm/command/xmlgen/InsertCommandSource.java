package com.vsu.amm.command.xmlgen;

import com.vsu.amm.Constants;
import com.vsu.amm.Utils;
import com.vsu.amm.command.ICommand;
import com.vsu.amm.command.ICommandSource;
import com.vsu.amm.command.InsertCommand;
import com.vsu.amm.data.stream.IDataStream;

import org.jdom2.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InsertCommandSource implements ICommandSource {

    List<ICommand> commands;
    int currentCommand = 0;
    BaseValueSet valueSet;
    String label;

    public InsertCommandSource(Element elem, AliasSet aliasSet, Map<String, Integer> params) {
        if (elem == null) {
            return;
        }

        String from = elem.getAttributeValue("from");
        valueSet = aliasSet.getAlias(from);
        boolean reuseValues = valueSet != null;

        Integer tmp;

        if (valueSet == null) {
            tmp = getAttributeValue(elem, "min", params);
            int min = tmp == null ? 0 : tmp;
            tmp = getAttributeValue(elem, "max", params);
            int max = tmp == null ? Integer.MAX_VALUE : tmp;
            valueSet = new StandardRandomValueSet(min, max);
        }

        String alias = elem.getAttributeValue("alias");

        if (!Utils.isNullOrEmpty(alias)) {
            aliasSet.putAlias(alias, valueSet);
        }

        tmp = getAttributeValue(elem, "count", params);
        int count = tmp == null ? 1 : tmp;

        commands = new ArrayList<>(count);

        if (reuseValues && valueSet.hasStoredValues())
            for (int i = 0; i < count; i++)
                commands.add(new InsertCommand(valueSet.reuseRandom().intValue()));
        else if (!Utils.isNullOrEmpty(alias))
            for (int i = 0; i < count; i++)
                commands.add(new InsertCommand(valueSet.generateRandomAndStore()));
        else
            for (int i = 0; i < count; i++) {
                commands.add(new InsertCommand(valueSet.generateRandom()));
            }

        label = elem.getAttributeValue("label");
    }

    public InsertCommandSource(Integer insertCount, AliasSet aliasSet) {
    	 valueSet = new StandardRandomValueSet(0, Constants.DEFAULT_MAX_VALUE);
    	 aliasSet.putAlias(Constants.DEFAULT_ALIAS_NAME, valueSet);
    	 commands = new ArrayList<>(insertCount);
    	 for (int i = 0; i < insertCount; i++)
             commands.add(new InsertCommand(valueSet.generateRandomAndStore()));
	}

	private Integer getAttributeValue(Element element, String attributeName, Map<String, Integer> params) {
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
        if (commands == null) {
            return;
        }
        if (stream == null) {
            return;
        }

        if (label != null)
            stream.label(label);
        commands.forEach(iCommand -> iCommand.printToStream(stream));
    }

	public List<ICommand> getCommands() {
		return commands;
	}

	public void setCommands(List<ICommand> commands) {
		this.commands = commands;
	}

	public int getCurrentCommand() {
		return currentCommand;
	}

	public void setCurrentCommand(int currentCommand) {
		this.currentCommand = currentCommand;
	}

	public BaseValueSet getValueSet() {
		return valueSet;
	}

	public void setValueSet(BaseValueSet valueSet) {
		this.valueSet = valueSet;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

    
}

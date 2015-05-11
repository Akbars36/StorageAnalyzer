package com.vsu.amm.command.xmlgen;

import com.vsu.amm.command.ICommand;
import com.vsu.amm.command.ICommandSource;
import com.vsu.amm.data.stream.IDataStream;
import org.apache.log4j.Logger;
import org.jdom2.Element;

import java.util.List;
import java.util.Map;

/**
 * Created by Nikita Skornyakov on 11.05.2015.
 */
public abstract class SimpleCommandSource implements ICommandSource {

    protected static Logger logger = Logger.getLogger(ICommandSource.class);
    protected List<ICommand> commands = null;
    protected int currentCommand = 0;
    protected BaseValueSet valueSet;
    protected String label;

    protected Integer getAttributeValue(Element element, String attributeName, Map<String, Integer> params) {
        if (element == null)
            return null;

        String strAttribute = element.getAttributeValue(attributeName);
        if (strAttribute == null)
            return null;

        if (strAttribute.startsWith("%") && strAttribute.endsWith("%")) {
            if (params == null)
                return null;

            strAttribute = strAttribute.substring(1, strAttribute.length() - 1);

            return params.get(strAttribute);
        }

        Integer attrValue = null;

        try {
            attrValue = Integer.parseInt(strAttribute);
        } catch (Exception ex) {
            logger.error("Cannot parse parameter: " + attributeName);
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

    @Override
    public void setParameters(Map<String, Integer> parameters) {
        if (valueSet != null) {
            valueSet.clearStoredValues();
            valueSet.setParams(parameters);
        }
    }
}

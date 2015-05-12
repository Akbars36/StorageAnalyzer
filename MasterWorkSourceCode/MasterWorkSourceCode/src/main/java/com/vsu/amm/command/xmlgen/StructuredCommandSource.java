package com.vsu.amm.command.xmlgen;

import com.vsu.amm.Utils;
import com.vsu.amm.command.ICommand;
import com.vsu.amm.command.ICommandSource;
import com.vsu.amm.data.stream.IDataStream;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Nikita Skornyakov on 11.05.2015.
 */
public abstract class StructuredCommandSource implements ICommandSource {

    protected List<ICommandSource> commands = null;
    protected int currentCommandSourceIndex = 0;
    protected String label;

    protected void parseParameters(Element elem, AliasSet aliasSet, Map<String, Integer> params) {
        if (elem == null)
            return;

        label = elem.getAttributeValue("label");

        String countStr = elem.getAttributeValue("count");
        int count = 1;
        if (!Utils.isNullOrEmpty(countStr)) {
            if (countStr.startsWith("%") && countStr.endsWith("%")) {
                countStr = countStr.substring(1, countStr.length() - 1);
                Integer tmp = params.get(countStr);
                if (tmp == null)
                    System.out.println("Parameter " + countStr + " not found!");
                else
                    count = tmp;
            } else
                try {
                    count = Integer.parseInt(countStr);
                } catch (Exception ex) {
                    System.out.println("Cannot parse count parameter in block sequence");
                }
        }

        List<Element> children = elem.getChildren();
        commands = new ArrayList<>(children.size());
        if (aliasSet == null)
            aliasSet = new AliasSet();
        for (int i = 0; i < count; i++)
            for (Element c : children)
                switch (c.getName().toLowerCase()) {
                    case "block":
                        commands.add(new BlockCommandSource(c, aliasSet, params));
                        break;
                    case "sequence":
                        commands.add(new SequenceCommandSource(c, aliasSet, params));
                        break;
                    case "select":
                        commands.add(new SelectCommandSource(c, aliasSet, params));
                        break;
                    case "insert":
                        commands.add(new InsertCommandSource(c, aliasSet, params));
                        break;
                    case "remove":
                        commands.add(new RemoveCommandSource(c, aliasSet, params));
                        break;
                    default:
                        System.out.println("Unknown command: " + c.getName() + ". Skipped.");
                }
    }

    @Override
    public void restart() {
        if (commands == null) {
            return;
        }

        commands.forEach(ICommandSource::restart);
        currentCommandSourceIndex = 0;
    }

    @Override
    public abstract ICommand next();

    @Override
    public void printToStream(IDataStream stream) {
        if (commands == null)
            return;

        if (stream == null)
            return;

        if (label != null)
            stream.label(label);

        commands.forEach(iCommandSource -> iCommandSource.printToStream(stream));
    }

    @Override
    public void setParameters(Map<String, Integer> parameters) {
        if (parameters == null)
            return;
        if (commands == null)
            return;
        commands.forEach(iCommandSource -> iCommandSource.setParameters(parameters));
    }

    public List<ICommandSource> getCommands() {
        return commands;
    }

    public void setCommands(List<ICommandSource> commands) {
        this.commands = commands;
    }

    public int getCurrentCommandSourceIndex() {
        return currentCommandSourceIndex;
    }

    public void setCurrentCommandSourceIndex(int currentCommandSourceIndex) {
        this.currentCommandSourceIndex = currentCommandSourceIndex;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}

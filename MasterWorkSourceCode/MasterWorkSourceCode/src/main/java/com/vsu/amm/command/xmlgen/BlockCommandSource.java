package com.vsu.amm.command.xmlgen;

import com.vsu.amm.command.ICommand;
import com.vsu.amm.command.ICommandSource;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BlockCommandSource extends StructuredCommandSource {

    private Integer[] commandOrder;

    public BlockCommandSource(Element elem, AliasSet aliasSet, Map<String, Integer> params) {
        if (elem == null)
            return;

        if (!"block".equals(elem.getName()))
            return;

        if (aliasSet == null)
            aliasSet = new AliasSet();

        parseParameters(elem, aliasSet, params);

        //generate execution order
        commandOrder = generateCommandOrder();
    }

    private Integer[] generateCommandOrder() {
        if (commands == null)
            return null;

        List<Integer> r = new ArrayList<>(commands.size());
        Random rnd = new Random();
        for (int i = 0; i < commands.size(); i++) {
            int cmdIndex = rnd.nextInt(commands.size());
            while (r.contains(cmdIndex))
                cmdIndex = (cmdIndex + 1) % commands.size();
            r.add(cmdIndex);
        }

        Integer[] result = new Integer[commands.size()];
        r.toArray(result);
        return result;
    }

    @Override
    public void restart() {
        super.restart();
        commandOrder = generateCommandOrder();
    }

    @Override
    public ICommand next() {
        if (commands == null)
            return null;

        if (commands.isEmpty())
            return null;

        while (currentCommandSourceIndex != commands.size()) {
            ICommandSource currentSource = commands.get(commandOrder[currentCommandSourceIndex]);
            ICommand currentCommand = currentSource.next();
            if (currentCommand != null)
                return currentCommand;

            currentCommandSourceIndex++;
        }
        return null;
    }

}

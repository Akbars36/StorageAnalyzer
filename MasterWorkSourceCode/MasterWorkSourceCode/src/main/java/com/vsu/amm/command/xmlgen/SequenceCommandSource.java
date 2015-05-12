package com.vsu.amm.command.xmlgen;

import com.vsu.amm.Utils;
import com.vsu.amm.command.ICommand;
import com.vsu.amm.command.ICommandSource;
import com.vsu.amm.data.stream.IDataStream;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SequenceCommandSource implements ICommandSource {

    public SequenceCommandSource(Element elem, AliasSet aliasSet, Map<String, Integer> params) {
        if (elem == null)
            return;
        
        if (!"sequence".equals(elem.getName()))
            return;

        if (aliasSet == null)
            aliasSet = new AliasSet();

        parseParameters(elem, aliasSet, params);
    }

    @Override
    public ICommand next() {
        if (commands == null)
            return null;

        if (commands.isEmpty())
            return null;

        while (currentCommandSourceIndex != commands.size()) {
            ICommandSource currentSource = commands.get(currentCommandSourceIndex);
            ICommand currentCommand = currentSource.next();
            if (currentCommand != null)
                return currentCommand;

            currentCommandSourceIndex++;
        }

        return null;
    }
}

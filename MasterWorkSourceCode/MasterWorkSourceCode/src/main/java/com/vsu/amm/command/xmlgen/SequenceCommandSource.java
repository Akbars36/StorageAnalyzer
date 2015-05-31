package com.vsu.amm.command.xmlgen;

import com.vsu.amm.command.ICommand;
import com.vsu.amm.command.ICommandSource;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.Map;

public class SequenceCommandSource extends StructuredCommandSource {

    public SequenceCommandSource(Element elem, AliasSet aliasSet, Map<String, Integer> params) {
        if (elem == null)
            return;
        
        if (!"sequence".equals(elem.getName()))
            return;

        if (aliasSet == null)
            aliasSet = new AliasSet();

        parseParameters(elem, aliasSet, params);
    }

    public SequenceCommandSource(Integer insertCount, Integer selectCount,
                                 Integer removeCount) {
        commands = new ArrayList<>(3);
        AliasSet aliasSet = new AliasSet();
        commands.add(new InsertCommandSource(insertCount, aliasSet));
        commands.add(new SelectCommandSource(selectCount, aliasSet));
        commands.add(new RemoveCommandSource(removeCount, aliasSet));
    }

    public SequenceCommandSource(Integer insertCount, Integer selectCount,
                                 Integer removeCount, int  repeatCount) {
        if (repeatCount < 0)
            repeatCount = 1;
        commands = new ArrayList<>(3 * repeatCount);
        for (int i = 0; i < repeatCount; i++) {
            AliasSet aliasSet = new AliasSet();
            commands.add(new InsertCommandSource(insertCount, aliasSet));
            commands.add(new SelectCommandSource(selectCount, aliasSet));
            commands.add(new RemoveCommandSource(removeCount, aliasSet));
        }
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

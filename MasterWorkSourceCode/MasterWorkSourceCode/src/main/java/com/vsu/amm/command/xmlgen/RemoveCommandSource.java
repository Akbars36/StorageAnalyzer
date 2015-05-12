package com.vsu.amm.command.xmlgen;

import com.vsu.amm.Constants;
import com.vsu.amm.Utils;
import com.vsu.amm.command.RemoveCommand;
import com.vsu.amm.command.SelectCommand;
import com.vsu.amm.data.stream.IDataStream;

import org.jdom2.Element;

import java.util.ArrayList;
import java.util.Map;

public class RemoveCommandSource extends SimpleCommandSource {

    public RemoveCommandSource(Element elem, AliasSet aliasSet, Map<String, Integer> params) {
        if (elem == null)
            return;

        String from = elem.getAttributeValue("from");
        if (valueSet != null)
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

        if (!Utils.isNullOrEmpty(alias) && aliasSet != null)
            aliasSet.putAlias(alias, valueSet);

        tmp = getAttributeValue(elem, "count", params);
        int count = tmp == null ? 1 : tmp;

        commands = new ArrayList<>(count);

        if (reuseValues && valueSet.hasStoredValues())
            for (int i = 0; i < count; i++)
                commands.add(new RemoveCommand(valueSet.reuseRandom().intValue()));
        else if (!Utils.isNullOrEmpty(alias))
            for (int i = 0; i < count; i++)
                commands.add(new RemoveCommand(valueSet.generateRandomAndStore()));
        else
            for (int i = 0; i < count; i++) {
                commands.add(new RemoveCommand(valueSet.generateRandom()));
            }

        label = elem.getAttributeValue("label");
    }

    public RemoveCommandSource(Integer removeCount, AliasSet aliasSet) {
        valueSet = new StandardRandomValueSet(0, Constants.DEFAULT_MAX_VALUE);
        valueSet = aliasSet.getAlias(Constants.DEFAULT_ALIAS_NAME);
        if (valueSet.hasStoredValues()) {
            commands = new ArrayList<>(removeCount);
            for (int i = 0; i < removeCount; i++)
                commands.add(new RemoveCommand(valueSet.reuseRandom()
                        .intValue()));
        }
    }
}

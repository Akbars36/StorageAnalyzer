package com.vsu.amm.command.xmlgen;

import com.vsu.amm.Constants;
import com.vsu.amm.Utils;
import com.vsu.amm.command.InsertCommand;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.Map;

public class InsertCommandSource extends SimpleCommandSource {

    public InsertCommandSource(Element elem, AliasSet aliasSet, Map<String, Integer> params) {
        if (elem == null)
            return;

        String from = elem.getAttributeValue("from");
        if (aliasSet != null)
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
}

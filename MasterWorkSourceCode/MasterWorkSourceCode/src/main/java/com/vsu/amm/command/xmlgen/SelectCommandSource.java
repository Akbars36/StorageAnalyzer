package com.vsu.amm.command.xmlgen;

import com.vsu.amm.Utils;
import com.vsu.amm.command.SelectCommand;
import org.jdom2.Element;

import java.util.ArrayList;
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
}

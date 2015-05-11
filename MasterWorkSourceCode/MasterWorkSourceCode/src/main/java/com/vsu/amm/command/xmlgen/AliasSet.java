package com.vsu.amm.command.xmlgen;


import java.util.HashMap;
import java.util.Map;

class AliasSet {

    private final Map<String, BaseValueSet> aliases = new HashMap<>();

    public BaseValueSet getAlias(String name) {
        if (name == null)
            return null;

        return aliases.get(name);
    }

    public void putAlias(String name, BaseValueSet valueSet) {
        if (name == null) {
            return;
        }

        aliases.put(name, valueSet);
    }

}

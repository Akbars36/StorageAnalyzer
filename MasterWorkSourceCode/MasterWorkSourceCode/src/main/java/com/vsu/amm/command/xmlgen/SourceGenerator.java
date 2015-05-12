package com.vsu.amm.command.xmlgen;

import com.vsu.amm.command.ICommandSource;
import org.jdom2.Element;

import java.util.Map;

/**
 * фабрика
 */
public class SourceGenerator {

	public static ICommandSource create(Element elem,
			Map<String, Integer> params) {
		if (elem == null)
			return null;

		AliasSet aliases = new AliasSet();

		ICommandSource commandSource;

        switch (elem.getName().toLowerCase()) {
            case "insert":
                commandSource = new InsertCommandSource(elem, aliases, params);
                break;
            case "select":
                commandSource = new SelectCommandSource(elem, aliases, params);
                break;
            case "remove":
                commandSource = new RemoveCommandSource(elem, aliases, params);
                break;
            case "block":
                commandSource = new BlockCommandSource(elem, aliases, params);
                break;
            case "sequence":
                commandSource = new SequenceCommandSource(elem, aliases, params);
                break;
            default:
                commandSource = null;
        }

		return commandSource;
	}

	public static ICommandSource createInsertSelectRemoveSource(
			Integer insertCount, Integer selectCount, Integer removeCount) {
		SequenceCommandSource commandSource = new SequenceCommandSource(
				insertCount, selectCount, removeCount);

		return commandSource;
	}

}

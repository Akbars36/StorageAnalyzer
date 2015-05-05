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

	List<ICommandSource> commands;
	int currentCommandSourceIndex = 0;
	String label;

	public SequenceCommandSource(Element elem, Map<String, Integer> params) {
		if (elem == null)
			return;

		if (!"sequence".equals(elem.getName()))
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
					System.out
							.println("Cannot parse count parameter in block sequence");
				}
		}

		List children = elem.getChildren();
		commands = new ArrayList<>(children.size());
		AliasSet aliasSet = new AliasSet();
		for (int i = 0; i < count; i++)
			children.forEach(child -> {
				Element c = (Element) child;
				switch (c.getName().toLowerCase()) {
				case "block":
					commands.add(new BlockCommandSource(c, aliasSet, params));
					break;
				case "sequence":
					commands.add(new SequenceCommandSource(c, params));
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
					System.out.println("Unknown command: " + c.getName()
							+ ". Skipped.");
				}
			});
	}

	public SequenceCommandSource(Integer insertCount, Integer selectCount,
			Integer removeCount) {
		commands = new ArrayList<>(3);
		AliasSet aliasSet = new AliasSet();
		commands.add(new InsertCommandSource(insertCount, aliasSet));
		commands.add(new SelectCommandSource(selectCount, aliasSet));
		commands.add(new RemoveCommandSource(removeCount, aliasSet));
	}

	@Override
	public ICommand next() {
		if (commands == null)
			return null;

		if (commands.isEmpty())
			return null;

		while (currentCommandSourceIndex != commands.size()) {
			ICommandSource currentSource = commands
					.get(currentCommandSourceIndex);
			ICommand currentCommand = currentSource.next();
			if (currentCommand != null)
				return currentCommand;

			currentCommandSourceIndex++;
		}

		return null;
	}

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
	public void restart() {
		if (commands == null)
			return;

		commands.forEach(iCommandSource -> iCommandSource.restart());
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

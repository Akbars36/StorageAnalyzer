package com.vsu.amm.load;

import com.vsu.amm.Utils;
import org.jdom2.Element;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Nikita Skornyakov on 25.04.2015.
 */
public class Parameters {

	Map<String, List<String>> parameters;

	public Parameters(Element element) {
		if (element == null) {
			parameters = null;
			return;
		}

		parameters = new HashMap<>();
		element.getChildren()
				.forEach(
						child -> {
							String childName = child.getName();
							if (!"param".equals(childName)) {
								System.out.println("Unknown tag " + childName
										+ " in param_values. Skipped.");
								return;
							}

							childName = child.getAttributeValue("name");
							if (Utils.isNullOrEmpty(childName)) {
								System.out
										.println("Parameter name not found. Skipped.");
								return;
							}

							List<Element> paramValues = child.getChildren();
							if (paramValues == null || paramValues.size() == 0) {
								System.out.println("No values for parameter "
										+ childName);
								return;
							}

							List<String> intValues = new ArrayList<>(
									paramValues.size());

							paramValues.forEach(value -> {
								if (!"value".equals(value.getName()))
									return;
								String txtValue = value.getText();
								try {
									// int v = String.parseInt(txtValue);
									intValues.add(txtValue);
								} catch (Exception ex) {
									System.out.println("Wrong value!");
								}
							});

							if (intValues.size() != 0)
								parameters.put(childName, intValues);
						});
	}

	public List<String> getGenerateParametrs() {
		List<String> param_names = new ArrayList<>(parameters.keySet());
		List<String> params_values = new ArrayList<>();
		generateParams(param_names, params_values, "");
		return params_values;
	}

	private void generateParams(List<String> params, List<String> result,
			String resultString) {
		if (params.size() == 0) {
			result.add(resultString);
		} else {
			String name = params.get(0);
			for (String value : parameters.get(name)) {
				if (params.size() != 1) {
					generateParams(params.subList(1, params.size()), result,
							resultString + name + "=" + value + ";");
				} else {
					generateParams(new ArrayList<>(), result,
							resultString + name + "=" + value + ";");
				}
			}
		}

	}

	public Map<String, List<String>> getParameters() {
		return parameters;
	}

	class ParamHelper {
		public int currentIndex = 0;
		public int paramCount;
		public String paramName;

		public ParamHelper(String paramName, int paramCount) {
			this.paramCount = paramCount;
			this.paramName = paramName;
		}
	}

	public Iterator<Map<String, String>> getParamIterator() {

		return new Iterator<Map<String, String>>() {
			//store all parameters in paramHelper for future iterate
			List<ParamHelper> helpers = new ArrayList<>(parameters.keySet().stream().map(paramName ->
					new ParamHelper(paramName, parameters.get(paramName).size())).collect(Collectors.toList()));
			Map<String, List<String>> p = parameters;

			@Override
			public boolean hasNext() {
				return helpers.size() != 0 && helpers.get(0).paramCount > helpers.get(0).currentIndex;

			}

			@Override
			public Map<String, String> next() {
				if (!hasNext())
					return null;

				Map<String, String> result = new HashMap<>(p.size());
				for (ParamHelper helper : helpers)
					result.put(helper.paramName, p.get(helper.paramName).get(helper.currentIndex));

				for (int i = helpers.size() - 1; i > 0; i--) {
					ParamHelper helper = helpers.get(i);
					helper.currentIndex++;
					if (helper.currentIndex != helper.paramCount)
						return result;
					helper.currentIndex = 0;
				}
				helpers.get(0).currentIndex++;
				return result;
			}
		};
	}

	public Iterator<Map<String, String>> getParamIterator(List<String> paramNames) {

		return new Iterator<Map<String, String>>() {
			//store all parameters in paramHelper for future iterate
			List<ParamHelper> helpers = new ArrayList<>(paramNames.stream().map(paramName ->
					new ParamHelper(paramName, parameters.get(paramName) == null ? 0 :
							parameters.get(paramName).size())).collect(Collectors.toList()));

			Map<String, List<String>> p = parameters;

			@Override
			public boolean hasNext() {
				for (int i = 0; i < helpers.size(); i++) {
					ParamHelper helper = helpers.get(i);
					if (helper.paramCount != 0)
						return helper.paramCount > helper.currentIndex;
				}
				return false;
			}

			@Override
			public Map<String, String> next() {
				if (!hasNext())
					return null;

				Map<String, String> result = new HashMap<>(p.size());
				for (ParamHelper helper : helpers)
					result.put(helper.paramName, p.get(helper.paramName).get(helper.currentIndex));

				for (int i = helpers.size() - 1; i > 0; i--) {
					ParamHelper helper = helpers.get(i);
					helper.currentIndex++;
					if (helper.currentIndex != helper.paramCount)
						return result;
					helper.currentIndex = 0;
				}
				helpers.get(0).currentIndex++;
				return result;
			}
		};
	}
}
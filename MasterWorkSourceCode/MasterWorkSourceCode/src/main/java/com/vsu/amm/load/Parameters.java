package com.vsu.amm.load;

import com.vsu.amm.MasterWorkException;
import com.vsu.amm.Utils;

import org.jdom2.Element;

import java.util.*;

/**
 * Created by Kzarw on 25.04.2015.
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
					generateParams(new ArrayList<String>(), result,
							resultString + name + "=" + value + ";");
				}
			}
		}

	}

	public Map<String, List<String>> getParameters() {
		return parameters;
	}

	// public Iterator<Map<String, String>> getParametersIterator() throws
	// MasterWorkException {
	// if (parameters == null)
	// return null;
	//
	// return getParametersIterator(parameters.keySet());
	// }

	// public Iterator<Map<String, String>> getParametersIterator(Set<String>
	// paramNames) throws MasterWorkException {
	// if (paramNames == null)
	// return null;
	//
	// if (parameters == null)
	// return null;
	//
	// Map<String, String> paramLenghts = new HashMap<>();
	//
	// for (String paramName : paramNames) {
	// List<String> paramValues = parameters.get(paramName);
	// if (paramValues == null)
	// throw new MasterWorkException("Parameter " + paramName + " not found!");
	// if (paramValues.size() == 0)
	// throw new MasterWorkException("Parameter " + paramName +
	// " has no values!");
	// paramLenghts.put(paramName, paramValues.size());
	// }
	// return null;
	//
	// }
}

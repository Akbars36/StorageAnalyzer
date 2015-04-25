package com.vsu.amm.load;

import com.vsu.amm.MasterWorkException;
import com.vsu.amm.Utils;
import org.jdom2.Element;

import java.util.*;

/**
 * Created by Kzarw on 25.04.2015.
 */
public class Parameters {

    Map<String, List<Integer>> parameters;

    public Parameters(Element element) {
        if (element == null) {
            parameters = null;
            return;
        }

        parameters = new HashMap<>();
        element.getChildren().forEach(child -> {
            String childName = child.getName();
            if (!"param".equals(childName)) {
                System.out.println("Unknown tag " + childName + " in param_values. Skipped.");
                return;
            }

            childName = child.getAttributeValue("name");
            if (Utils.isNullOrEmpty(childName)) {
                System.out.println("Parameter name not found. Skipped.");
                return;
            }

            List<Element> paramValues = child.getChildren();
            if (paramValues == null || paramValues.size() == 0) {
                System.out.println("No values for parameter " + childName);
                return;
            }

            List<Integer> intValues = new ArrayList<>(paramValues.size());

            paramValues.forEach(value -> {
                if (!"value".equals(value.getName()))
                    return;
                String txtValue = value.getText();
                try {
                    int v = Integer.parseInt(txtValue);
                    intValues.add(v);
                } catch (Exception ex) {
                    System.out.println("Wrong value!");
                }
            });

            if (intValues.size() != 0)
                parameters.put(childName, intValues);
        });
    }

    public Map<String, List<Integer>> getParameters() {
        return parameters;
    }

    public Iterator<Map<String, Integer>> getParametersIterator() throws MasterWorkException {
        if (parameters == null)
            return null;

        return getParametersIterator(parameters.keySet());
    }

    public Iterator<Map<String, Integer>> getParametersIterator(Set<String> paramNames) throws MasterWorkException {
        if (paramNames == null)
            return null;

        if (parameters == null)
            return null;

        Map<String, Integer> paramLenghts = new HashMap<>();

        for (String paramName : paramNames) {
            List<Integer> paramValues = parameters.get(paramName);
            if (paramValues == null)
                throw new MasterWorkException("Parameter " + paramName + " not found!");
            if (paramValues.size() == 0)
                throw new MasterWorkException("Parameter " + paramName + " has no values!");
            paramLenghts.put(paramName, paramValues.size());
        }
        return null;

    }
}

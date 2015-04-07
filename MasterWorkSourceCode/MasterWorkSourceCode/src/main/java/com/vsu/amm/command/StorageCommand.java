package com.vsu.amm.command;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by VLAD on 13.05.14.
 */
public class StorageCommand {
    @Parameter(names = {"class"})
    private String className;

    @Parameter(description = "Other params", variableArity = true)
    private List<String> params = new ArrayList<String>();

}

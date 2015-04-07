package com.vsu.amm.command;

import com.beust.jcommander.Parameter;

/**
 * Created by VLAD on 13.05.14.
 */
public class InsertCommand {
    @Parameter(names = {"count"})
    private Integer count = 1;

    @Parameter(names = {"label"})
    private String label;

    @Parameter(names = {"alias"})
    private String alias;

    @Parameter(names = {"min"})
    private Integer min;

    @Parameter(names = {"max"})
    private Integer max;
}

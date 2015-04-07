package com.vsu.amm.command;

import com.beust.jcommander.Parameter;

/**
 * Created by VLAD on 13.05.14.
 */
public class UpdateCommand {
    @Parameter(names = {"count"})
    private Integer count = 1;

    @Parameter(names = {"label"})
    private String label;

    @Parameter(names = {"from"})
    private String from;
}

package com.vsu.amm.command;

/**
 * Последовательно выполняет команды источника
 */
public interface ICommandPlayer {
    void play(ICommandSource commandSource);
}

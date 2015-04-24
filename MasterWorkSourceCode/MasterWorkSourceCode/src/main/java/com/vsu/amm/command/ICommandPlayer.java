package com.vsu.amm.command;

/**
 * ѕоследовательно выполн€ет команды источника
 */
public interface ICommandPlayer {
    void play(ICommandSource commandSource);
}

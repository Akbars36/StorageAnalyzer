package com.vsu.amm.command;

/**
 * ѕоследовательно выполн€ет команды источника
 */
interface ICommandPlayer {
    void play(ICommandSource commandSource);
}

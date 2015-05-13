package com.vsu.amm.command;

/**
 * Последовательно выполняет команды источника
 */
interface ICommandPlayer {
	void play(ICommandSource commandSource);
}

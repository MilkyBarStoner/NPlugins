/***************************************************************************
 * Project file:    NPlugins - NTheEndAgain - RespawnTask.java             *
 * Full Class name: fr.ribesg.bukkit.ntheendagain.task.RespawnTask         *
 *                                                                         *
 *                Copyright (c) 2012-2014 Ribesg - www.ribesg.fr           *
 *   This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt   *
 *    Please contact me at ribesg[at]yahoo.fr if you improve this file!    *
 ***************************************************************************/

package fr.ribesg.bukkit.ntheendagain.task;

import fr.ribesg.bukkit.ntheendagain.handler.EndWorldHandler;

/**
 * @author Ribesg
 */
public class RespawnTask extends RandomRepeatingTask {

	public RespawnTask(final EndWorldHandler handler) {
		super(handler);
	}

	@Override
	public boolean exec() {
		worldHandler.getPlugin().entering(getClass(), "exec");

		final boolean res = worldHandler.getRespawnHandler().respawn();

		worldHandler.getPlugin().exiting(getClass(), "exec", Boolean.toString(res));
		return res;
	}

	@Override
	protected long getInitialDelay() {
		long nextRespawnTaskTime = worldHandler.getConfig().getNextRespawnTaskTime();
		if (worldHandler.getConfig().getRespawnType() == 4) {
			nextRespawnTaskTime = 0;
		}
		return buildInitialDelay(nextRespawnTaskTime);
	}

	@Override
	protected long getDelay() {
		return worldHandler.getConfig().getRandomRespawnTimer();
	}
}

/***************************************************************************
 * Project file:    NPlugins - NCuboid - DeleteSubcmdExecutor.java         *
 * Full Class name: fr.ribesg.bukkit.ncuboid.commands.subexecutors.DeleteSubcmdExecutor
 *                                                                         *
 *                Copyright (c) 2012-2014 Ribesg - www.ribesg.fr           *
 *   This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt   *
 *    Please contact me at ribesg[at]yahoo.fr if you improve this file!    *
 ***************************************************************************/

package fr.ribesg.bukkit.ncuboid.commands.subexecutors;

import fr.ribesg.bukkit.ncore.lang.MessageId;
import fr.ribesg.bukkit.ncuboid.NCuboid;
import fr.ribesg.bukkit.ncuboid.Perms;
import fr.ribesg.bukkit.ncuboid.beans.GeneralRegion;
import fr.ribesg.bukkit.ncuboid.beans.PlayerRegion;
import fr.ribesg.bukkit.ncuboid.commands.AbstractSubcmdExecutor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class DeleteSubcmdExecutor extends AbstractSubcmdExecutor {

	public DeleteSubcmdExecutor(final NCuboid instance) {
		super(instance);
		setUsage(ChatColor.RED + "Usage : /cuboid delete <regionName>");
	}

	@Override
	public boolean exec(final CommandSender sender, final String[] args) {
		if (args.length != 1) {
			return false;
		} else if (Perms.hasDelete(sender)) {
			final GeneralRegion region = getPlugin().getDb().getByName(args[0]);
			if (region == null || region.getType() == GeneralRegion.RegionType.WORLD) {
				getPlugin().sendMessage(sender, MessageId.cuboid_doesNotExist, args[0]);
				return true;
			} else {
				final PlayerRegion r = (PlayerRegion) region;
				if (Perms.isAdmin(sender) || r.isOwner(sender)) {
					getPlugin().getDb().remove(r);
					getPlugin().sendMessage(sender, MessageId.cuboid_cmdDeleteDeleted, region.getRegionName());
				} else {
					getPlugin().sendMessage(sender, MessageId.cuboid_cmdDeleteNoPermission, region.getRegionName());
				}
				return true;
			}
		} else {
			getPlugin().sendMessage(sender, MessageId.noPermissionForCommand);
			return true;
		}
	}
}

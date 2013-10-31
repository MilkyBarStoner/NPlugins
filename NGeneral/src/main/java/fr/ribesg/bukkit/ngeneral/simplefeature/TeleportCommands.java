package fr.ribesg.bukkit.ngeneral.simplefeature;
import fr.ribesg.bukkit.ncore.common.NLocation;
import fr.ribesg.bukkit.ncore.lang.MessageId;
import fr.ribesg.bukkit.ncore.utils.PlayerUtils;
import fr.ribesg.bukkit.ngeneral.NGeneral;
import fr.ribesg.bukkit.ngeneral.Perms;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/** @author Ribesg */
public class TeleportCommands implements CommandExecutor {

	private final NGeneral               plugin;
	private final Map<String, NLocation> backMap;

	public TeleportCommands(final NGeneral instance) {
		this.plugin = instance;
		this.backMap = new HashMap<>();
		plugin.getCommand("tp").setExecutor(this);
		plugin.getCommand("tphere").setExecutor(this);
		plugin.getCommand("tpthere").setExecutor(this);
		plugin.getCommand("tpback").setExecutor(this);
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
		switch (cmd.getName()) {
			case "tp":
				if (Perms.hasTp(sender)) {
					return execTpCommand(sender, args);
				} else {
					plugin.sendMessage(sender, MessageId.noPermissionForCommand);
					return true;
				}
			case "tphere":
				if (Perms.hasTpHere(sender)) {
					return execTpHereCommand(sender, args);
				} else {
					plugin.sendMessage(sender, MessageId.noPermissionForCommand);
					return true;
				}
			case "tpthere":
				if (Perms.hasTpThere(sender)) {
					return execTpThereCommand(sender, args);
				} else {
					plugin.sendMessage(sender, MessageId.noPermissionForCommand);
					return true;
				}
			case "tpback":
				if (Perms.hasTpBack(sender)) {
					return execTpBackCommand(sender, args);
				} else {
					plugin.sendMessage(sender, MessageId.noPermissionForCommand);
					return true;
				}
			default:
				return false;
		}
	}

	private boolean execTpCommand(final CommandSender sender, final String[] args) {
		if (args.length == 1) {
			if (!(sender instanceof Player)) {
				plugin.sendMessage(sender, MessageId.cmdOnlyAvailableForPlayers);
				return true;
			} else {
				final Player player = (Player) sender;
				final Player target = Bukkit.getPlayer(args[0]);
				if (target == null) {
					plugin.sendMessage(player, MessageId.noPlayerFoundForGivenName, args[0]);
					return true;
				} else {
					backMap.put(player.getName(), new NLocation(player.getLocation()));
					player.teleport(target);
					plugin.sendMessage(player, MessageId.general_tp_youToTarget, target.getName());
					return true;
				}
			}
		} else if (args.length == 2) {
			final Player target = Bukkit.getPlayer(args[1]);
			if (target == null) {
				plugin.sendMessage(sender, MessageId.noPlayerFoundForGivenName, args[0]);
				return true;
			} else {
				for (final String playerName : args[0].split(",")) {
					final Player player = Bukkit.getPlayer(playerName);
					if (player == null) {
						plugin.sendMessage(sender, MessageId.noPlayerFoundForGivenName, args[0]);
					} else {
						backMap.put(player.getName(), new NLocation(player.getLocation()));
						player.teleport(target);
						plugin.sendMessage(player, MessageId.general_tp_somebodyToTarget, sender.getName(), target.getName());
						plugin.sendMessage(sender, MessageId.general_tp_youSomebodyToTarget, player.getName(), target.getName());
					}
				}
				return true;
			}
		} else {
			return false;
		}
	}

	private boolean execTpHereCommand(final CommandSender sender, final String[] args) {
		if (args.length == 1) {
			if (!(sender instanceof Player)) {
				plugin.sendMessage(sender, MessageId.cmdOnlyAvailableForPlayers);
				return true;
			} else {
				final Player target = (Player) sender;
				for (final String playerName : args[0].split(",")) {
					final Player player = Bukkit.getPlayer(playerName);
					if (player == null) {
						plugin.sendMessage(sender, MessageId.noPlayerFoundForGivenName, args[0]);
					} else {
						backMap.put(player.getName(), new NLocation(player.getLocation()));
						player.teleport(target);
						plugin.sendMessage(player, MessageId.general_tp_somebodyToHim, sender.getName());
						plugin.sendMessage(sender, MessageId.general_tp_youSomebodyToYou, player.getName());
					}
				}
				return true;
			}
		} else {
			return false;
		}
	}

	private boolean execTpThereCommand(final CommandSender sender, final String[] args) {
		if (args.length == 0) {
			if (!(sender instanceof Player)) {
				plugin.sendMessage(sender, MessageId.cmdOnlyAvailableForPlayers);
				return true;
			} else {
				final Player player = (Player) sender;
				final Block targetBlock = PlayerUtils.getTargetBlock(player, null, Integer.MAX_VALUE);
				if (targetBlock == null) {
					plugin.sendMessage(player, MessageId.general_tp_noTarget);
					return true;
				} else {
					final Location loc = targetBlock.getLocation();
					loc.add(0.5, 0.05, 0.5);
					while (loc.getBlock().getType().isSolid() || loc.getBlock().getRelative(BlockFace.UP).getType().isSolid()) {
						loc.add(0, 1, 0);
					}
					loc.setPitch(player.getLocation().getPitch());
					loc.setYaw(player.getLocation().getYaw());
					backMap.put(player.getName(), new NLocation(player.getLocation()));
					player.teleport(loc);
					plugin.sendMessage(player, MessageId.general_tp_youToLocation);
					return true;
				}
			}
		} else if (args.length == 1) {
			if (!(sender instanceof Player)) {
				plugin.sendMessage(sender, MessageId.cmdOnlyAvailableForPlayers);
				return true;
			} else {
				final Player player = (Player) sender;
				final Block targetBlock = PlayerUtils.getTargetBlock(player, null, Integer.MAX_VALUE);
				if (targetBlock == null) {
					plugin.sendMessage(player, MessageId.general_tp_noTarget);
					return true;
				} else {
					final Location loc = targetBlock.getLocation();
					loc.add(0.5, 0.05, 0.5);
					while (loc.getBlock().getType().isSolid() || loc.getBlock().getRelative(BlockFace.UP).getType().isSolid()) {
						loc.add(0, 1, 0);
					}
					for (final String playerName : args[0].split(",")) {
						final Player toTeleport = Bukkit.getPlayer(playerName);
						if (toTeleport == null) {
							plugin.sendMessage(sender, MessageId.noPlayerFoundForGivenName, args[0]);
						} else {
							backMap.put(toTeleport.getName(), new NLocation(toTeleport.getLocation()));
							loc.setPitch(toTeleport.getLocation().getPitch());
							loc.setYaw(toTeleport.getLocation().getYaw());
							toTeleport.teleport(loc);
							plugin.sendMessage(toTeleport, MessageId.general_tp_somebodyToLocation, sender.getName());
							plugin.sendMessage(sender, MessageId.general_tp_youSomebodyToLocation, toTeleport.getName());
						}
					}
					return true;
				}
			}
		} else {
			return false;
		}
	}

	private boolean execTpBackCommand(final CommandSender sender, final String[] args) {
		if (args.length == 0) {
			if (!(sender instanceof Player)) {
				plugin.sendMessage(sender, MessageId.cmdOnlyAvailableForPlayers);
				return true;
			} else {
				final Player player = (Player) sender;
				final NLocation loc = backMap.remove(player.getName());
				if (loc == null) {
					plugin.sendMessage(player, MessageId.general_tp_youNoKnownBack);
					return true;
				}
				final Location bukkitLoc = loc.toBukkitLocation();
				if (bukkitLoc == null) {
					plugin.sendMessage(player, MessageId.general_tp_youBackWorldUnloaded, loc.getWorldName());
					return true;
				}
				player.teleport(bukkitLoc);
				plugin.sendMessage(player, MessageId.general_tp_youTeleportedBack);
				return true;
			}
		} else if (args.length == 1) {
			for (final String playerName : args[0].split(",")) {
				final Player player = Bukkit.getPlayer(playerName);
				if (player == null) {
					plugin.sendMessage(sender, MessageId.noPlayerFoundForGivenName, args[0]);
				} else {
					final NLocation loc = backMap.remove(player.getName());
					if (loc == null) {
						plugin.sendMessage(sender, MessageId.general_tp_somebodyNoKnownBack, player.getName());
						return true;
					}
					final Location bukkitLoc = loc.toBukkitLocation();
					if (bukkitLoc == null) {
						plugin.sendMessage(sender, MessageId.general_tp_somebodyBackWorldUnloaded, player.getName(), loc.getWorldName());
						return true;
					}
					player.teleport(bukkitLoc);
					plugin.sendMessage(player, MessageId.general_tp_somebodyTeleportedYouBack, sender.getName());
					plugin.sendMessage(sender, MessageId.general_tp_youTeleportedSomebodyBack, player.getName());
				}
			}
			return true;
		} else {
			return false;
		}
	}
}

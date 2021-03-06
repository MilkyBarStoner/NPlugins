/***************************************************************************
 * Project file:    NPlugins - NCore - NPlugin.java                        *
 * Full Class name: fr.ribesg.bukkit.ncore.node.NPlugin                    *
 *                                                                         *
 *                Copyright (c) 2012-2014 Ribesg - www.ribesg.fr           *
 *   This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt   *
 *    Please contact me at ribesg[at]yahoo.fr if you improve this file!    *
 ***************************************************************************/

package fr.ribesg.bukkit.ncore.node;

import fr.ribesg.bukkit.ncore.NCore;
import fr.ribesg.bukkit.ncore.lang.AbstractMessages;
import fr.ribesg.bukkit.ncore.lang.MessageId;
import fr.ribesg.bukkit.ncore.utils.FrameBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.mcstats.Metrics;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents a plugin node of the N plugin suite
 *
 * @author Ribesg
 */
public abstract class NPlugin extends JavaPlugin implements Node {

	private static final String CORE          = "NCore";
	private static final String NCORE_WEBSITE = "http://www.ribesg.fr/";

	private final Logger logger = this.getLogger();

	private NCore core;
	private boolean enabled      = false;
	private boolean debugEnabled = false;

	private Metrics metrics;

	@Override
	public void onEnable() {
		final FrameBuilder frame;
		core = (NCore) Bukkit.getPluginManager().getPlugin(CORE);
		if (badCoreVersion()) {

			frame = new FrameBuilder();
			frame.addLine("This plugin requires " + CORE + " v" + getMinCoreVersion(), FrameBuilder.Option.CENTER);
			frame.addLine(CORE + " plugin was found but the");
			frame.addLine("current version (v" + getCoreVersion() + ") is too low.");
			frame.addLine("See " + NCORE_WEBSITE);
			frame.addLine("Disabling plugin...");

			for (final String s : frame.build()) {
				getLogger().severe(s);
			}

			getPluginLoader().disablePlugin(this);
		} else /* Everything's ok */ {
			debugEnabled = core.getPluginConfig().isDebugEnabled(this.getName());
			if (debugEnabled) {
				getLogger().info("DEBUG MODE ENABLED!");
			}
			try {
				metrics = new Metrics(this);
				metrics.start();
			} catch (final IOException e) {
				e.printStackTrace();
			}
			final boolean activationResult = onNodeEnable();
			if (activationResult) {
				enabled = true;
				afterEnable();
			} else {
				// TODO Emergency mode
				getLogger().severe("Disabling plugin...");
				getPluginLoader().disablePlugin(this);
			}
		}
	}

	/**
	 * Replace the normal {@link org.bukkit.plugin.java.JavaPlugin#onEnable()} method in a normal plugin.
	 *
	 * @return If we should disable the plugin immediatly because we got a problem
	 */
	protected abstract boolean onNodeEnable();

	/**
	 * Call {@link #handleOtherNodes()} after plugin initialization tick.
	 */
	private void afterEnable() {
		new BukkitRunnable() {

			@Override
			public void run() {
				handleOtherNodes();
			}
		}.runTask(this);
	}

	/**
	 * Connect this Node to other Nodes if needed
	 * Called after every plugins has been loaded
	 */
	protected abstract void handleOtherNodes();

	@Override
	public void onDisable() {
		if (enabled) {
			onNodeDisable();
		}
	}

	/**
	 * Associate commands to their executors with a nullcheck.
	 *
	 * @param commandName the name of the command
	 * @param executor    the executor
	 *
	 * @return if the command was successfully registered
	 */
	public boolean setCommandExecutor(final String commandName, final CommandExecutor executor) {
		debug("- Registering command " + commandName);
		final PluginCommand cuboidCmd = getCommand(commandName);
		if (cuboidCmd != null) {
			cuboidCmd.setExecutor(executor);
			return true;
		} else {
			error("Command registered by another plugin: " + commandName);
			return false;
		}
	}

	/**
	 * Replace the normal {@link org.bukkit.plugin.java.JavaPlugin#onDisable()} method in a normal plugin.
	 * Only here for compliance
	 */
	protected abstract void onNodeDisable();

	private boolean badCoreVersion() {
		linkCore();
		return getCoreVersion().compareTo(getMinCoreVersion()) < 0;
	}

	public void sendMessage(final CommandSender to, final MessageId messageId, final String... args) {
		final String[] m = getMessages().get(messageId, args);
		to.sendMessage(m);
	}

	public void broadcastMessage(final MessageId messageId, final String... args) {
		final String[] m = getMessages().get(messageId, args);
		for (final String mes : m) {
			getServer().broadcastMessage(mes);
		}
	}

	public void broadcastExcluding(final Player player, final MessageId messageId, final String... args) {
		final String[] m = getMessages().get(messageId, args);
		for (final Player p : Bukkit.getOnlinePlayers()) {
			if (p != player) {
				for (final String mes : m) {
					getServer().broadcastMessage(mes);
				}
			}
		}
	}

	public abstract AbstractMessages getMessages();

	/**
	 * Call the Core's Setter for this Node type
	 * Basically: core.set[THIS]Node(this);
	 */
	private void linkCore() {
		getCore().set(getNodeName(), this);
	}

	protected abstract String getMinCoreVersion();

	private String getCoreVersion() {
		return getCore().getDescription().getVersion();
	}

	public NCore getCore() {
		return core;
	}

	protected Metrics getMetrics() {
		return metrics;
	}

	// ##################### //
	// ## Debugging stuff ## //
	// ##################### //

	public void setDebugEnabled(final boolean value) {
		this.debugEnabled = value;
	}

	public boolean isDebugEnabled() {
		return this.debugEnabled;
	}

	public void log(final Level level, final String message) {
		logger.log(level, message);
	}

	public void info(final String message) {
		log(Level.INFO, message);
	}

	public void entering(final Class clazz, final String methodName) {
		if (this.debugEnabled) {
			log(Level.INFO, "DEBUG >>> '" + methodName + "' in " + shortNPluginPackageName(clazz.getName()));
		}
	}

	public void entering(final Class clazz, final String methodName, final String comment) {
		if (this.debugEnabled) {
			log(Level.INFO, "DEBUG >>> '" + methodName + "' in " + shortNPluginPackageName(clazz.getName()) + " (" + comment + ')');
		}
	}

	public void exiting(final Class clazz, final String methodName) {
		if (this.debugEnabled) {
			log(Level.INFO, "DEBUG <<< '" + methodName + "' in " + shortNPluginPackageName(clazz.getName()));
		}
	}

	public void exiting(final Class clazz, final String methodName, final String comment) {
		if (this.debugEnabled) {
			log(Level.INFO, "DEBUG <<< '" + methodName + "' in " + shortNPluginPackageName(clazz.getName()) + " (" + comment + ')');
		}
	}

	private String shortNPluginPackageName(final String packageName) {
		return packageName.substring(17);
	}

	public void debug(final String message) {
		if (this.debugEnabled) {
			log(Level.INFO, "DEBUG         " + message);
		}
	}

	public void debug(final String message, final Throwable e) {
		if (this.debugEnabled) {
			logger.log(Level.SEVERE, "DEBUG         " + message, e);
		}
	}

	public void error(final String message) {
		error(Level.SEVERE, message);
	}

	public void error(final Level level, final String message) {
		log(level, message);
	}

	public void error(final String message, final Throwable e) {
		error(Level.SEVERE, message, e);
	}

	public void error(final Level level, final String message, final Throwable e) {
		logger.log(level, message, e);
	}
}

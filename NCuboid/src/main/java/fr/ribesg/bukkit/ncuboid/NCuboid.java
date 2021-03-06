/***************************************************************************
 * Project file:    NPlugins - NCuboid - NCuboid.java                      *
 * Full Class name: fr.ribesg.bukkit.ncuboid.NCuboid                       *
 *                                                                         *
 *                Copyright (c) 2012-2014 Ribesg - www.ribesg.fr           *
 *   This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt   *
 *    Please contact me at ribesg[at]yahoo.fr if you improve this file!    *
 ***************************************************************************/

package fr.ribesg.bukkit.ncuboid;

import fr.ribesg.bukkit.ncore.common.NLocation;
import fr.ribesg.bukkit.ncore.node.NPlugin;
import fr.ribesg.bukkit.ncore.node.cuboid.CuboidNode;
import fr.ribesg.bukkit.ncuboid.beans.Jail;
import fr.ribesg.bukkit.ncuboid.beans.Jails;
import fr.ribesg.bukkit.ncuboid.beans.RegionDb;
import fr.ribesg.bukkit.ncuboid.beans.RegionDbPersistenceHandler;
import fr.ribesg.bukkit.ncuboid.beans.WorldRegion;
import fr.ribesg.bukkit.ncuboid.commands.MainCommandExecutor;
import fr.ribesg.bukkit.ncuboid.config.Config;
import fr.ribesg.bukkit.ncuboid.dynmap.DynmapBridge;
import fr.ribesg.bukkit.ncuboid.lang.Messages;
import fr.ribesg.bukkit.ncuboid.listeners.EventExtensionListener;
import fr.ribesg.bukkit.ncuboid.listeners.PlayerStickListener;
import fr.ribesg.bukkit.ncuboid.listeners.WorldLoadingListener;
import fr.ribesg.bukkit.ncuboid.listeners.flag.*;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.PluginManager;
import org.mcstats.Metrics;

import java.io.IOException;
import java.util.Set;

/**
 * TODO
 *
 * @author Ribesg
 */
public class NCuboid extends NPlugin implements CuboidNode {

	// Configs
	private Messages messages;
	private Config   pluginConfig;

	// Useful Nodes
	// // None

	// Cuboids base
	private RegionDb db;

	// Jails
	private Jails jails;

	// Dynmap!
	private DynmapBridge dynmapBridge;

	@Override
	protected String getMinCoreVersion() {
		return "0.6.1";
	}

	/**
	 * @see fr.ribesg.bukkit.ncore.node.NPlugin#onNodeEnable()
	 */
	@Override
	protected boolean onNodeEnable() {
		entering(getClass(), "onNodeEnable");

		debug("Loading plugin messages...");
		try {
			if (!getDataFolder().isDirectory()) {
				getDataFolder().mkdir();
			}
			messages = new Messages();
			messages.loadMessages(this);
		} catch (final IOException e) {
			error("An error occured when NCuboid tried to load messages.yml", e);
			return false;
		}

		debug("Loading plugin configuration...");
		try {
			pluginConfig = new Config(this);
			pluginConfig.loadConfig();
		} catch (final IOException | InvalidConfigurationException e) {
			error("An error occured when NCuboid tried to load config.yml", e);
			return false;
		}

		debug("Creating Dynmap Bridge...");
		this.dynmapBridge = new DynmapBridge();

		debug("Creating Jail system...");
		jails = new Jails(this);

		debug("Loading Regions...");
		try {
			db = RegionDbPersistenceHandler.loadDb(this);
		} catch (final IOException | InvalidConfigurationException e) {
			error("An error occured when NCuboid tried to load cuboidDB.yml", e);
			return false;
		}

		debug("Creating and registering listeners...");
		final PluginManager pm = getServer().getPluginManager();

		pm.registerEvents(new EventExtensionListener(this), this);
		pm.registerEvents(new PlayerStickListener(this), this);
		pm.registerEvents(new WorldLoadingListener(this), this);

		// Flag Listeners
		pm.registerEvents(new BoosterFlagListener(this), this);
		pm.registerEvents(new BuildFlagListener(this), this);
		pm.registerEvents(new ChatFlagListener(this), this);
		pm.registerEvents(new ChestFlagListener(this), this);
		pm.registerEvents(new ClosedFlagListener(this), this);
		pm.registerEvents(new CreativeFlagListener(this), this);
		pm.registerEvents(new DropFlagListener(this), this);
		pm.registerEvents(new EndermanGriefFlagListener(this), this);
		pm.registerEvents(new ExplosionFlagListener(this), this);
		pm.registerEvents(new FarmFlagListener(this), this);
		pm.registerEvents(new FireFlagListener(this), this);
		pm.registerEvents(new GodFlagListener(this), this);
		pm.registerEvents(new InvisibleFlagListener(this), this);
		pm.registerEvents(new JailFlagListener(this), this);
		pm.registerEvents(new MobFlagListener(this), this);
		pm.registerEvents(new PassFlagListener(this), this);
		pm.registerEvents(new PvpFlagListener(this), this);
		pm.registerEvents(new SnowFlagListener(this), this);
		pm.registerEvents(new TeleportFlagListener(this), this);
		pm.registerEvents(new UseFlagListener(this), this);
		pm.registerEvents(new WarpgateFlagListener(this), this);

		debug("Registering command...");
		setCommandExecutor("cuboid", new MainCommandExecutor(this));

		debug("Initializing Dynmap bridge...");
		this.dynmapBridge.initialize(this.db);

		debug("Initializing Metrics...");
		final Metrics.Graph g = getMetrics().createGraph("Amount of Regions");
		g.addPlotter(new Metrics.Plotter() {

			@Override
			public int getValue() {
				return getDb().size();
			}
		});

		exiting(getClass(), "onNodeEnable");
		return true;
	}

	/**
	 * @see fr.ribesg.bukkit.ncore.node.NPlugin#handleOtherNodes()
	 */
	@Override
	protected void handleOtherNodes() {
		entering(getClass(), "handleOtherNodes");

		debug("Seeking new worlds...");
		for (final World world : getServer().getWorlds()) {
			if (db.getByWorld(world.getName()) == null) {
				debug("  New world found: " + world.getName());
				db.addByWorld(new WorldRegion(world.getName()));
			}
		}

		exiting(getClass(), "handleOtherNodes");
	}

	/**
	 * @see fr.ribesg.bukkit.ncore.node.NPlugin#onNodeDisable()
	 */
	@Override
	protected void onNodeDisable() {
		entering(getClass(), "onNodeDisable");

		debug("Saving Regions...");
		try {
			RegionDbPersistenceHandler.saveDb(this, getDb());
		} catch (final IOException e) {
			error("An error occured when NCuboid tried to save cuboidDB.yml", e);
		}

		exiting(getClass(), "onNodeDisable");
	}

	public RegionDb getDb() {
		return db;
	}

	public void setDb(final RegionDb db) {
		this.db = db;
	}

	@Override
	public Messages getMessages() {
		return messages;
	}

	public Config getPluginConfig() {
		return pluginConfig;
	}

	public DynmapBridge getDynmapBridge() {
		return dynmapBridge;
	}

	public Jails getJails() {
		return jails;
	}

	// API for other nodes

	@Override
	public String getNodeName() {
		return CUBOID;
	}

	@Override
	public boolean isJailed(final String playerName) {
		entering(getClass(), "isJailed", "playerName=" + playerName);
		final boolean result = jails.isJailed(playerName);
		exiting(getClass(), "isJailed", "result=" + result);
		return result;
	}

	@Override
	public boolean jail(final String playerName, final String jailName) {
		if (isDebugEnabled()) {
			entering(getClass(), "jail", "playerName=" + playerName + ";jaileName=" + jailName);
		}
		final boolean result = jails.jail(playerName, jailName);
		exiting(getClass(), "jail", "result=" + result);
		return result;
	}

	@Override
	public boolean unJail(final String playerName) {
		entering(getClass(), "unJail", "playerName=" + playerName);
		final boolean result = jails.unJail(playerName);
		exiting(getClass(), "unJail", "result=" + result);
		return result;
	}

	@Override
	public Set<String> getJailsSet() {
		return jails.getJailNames();
	}

	@Override
	public NLocation getJailLocation(final String jailName) {
		entering(getClass(), "getJailLocation", "jailName=" + jailName);

		final Jail jail = jails.getByName(jailName);
		if (jail != null) {
			exiting(getClass(), "getJailLocation");
			return jail.getLocation();
		} else {
			exiting(getClass(), "getJailLocation", "Failed: unknown jail");
			return null;
		}
	}
}

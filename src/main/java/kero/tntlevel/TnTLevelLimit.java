package kero.tntlevel;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class TnTLevelLimit extends JavaPlugin implements Listener {

	private int tntlevel;

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		reloadConfig();
		tntlevel = getConfig().getInt("TnT Level Limit", 30);

		getLogger().info("TnT Level Limit enabled");
	}

	@Override
	public void onDisable() {
		getConfig().set("TnT Level Limit", tntlevel);
		saveConfig();
		getLogger().info("TnT Level Limit disabled");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (cmd.getName().equalsIgnoreCase("tntlevel")) {
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("limit")) {
					if (!sender.hasPermission("tntlevel.setlimit")) {
						sender.sendMessage(ChatColor.RED
								+ "You do not have permission to use this command!");
						return true;
					}

					tntlevel = Integer.parseInt(args[1]);

					getConfig().set("TnT Level Limit", tntlevel);
					saveConfig();

					sender.sendMessage(ChatColor.GREEN
							+ "TnT Level Limit is now " + ChatColor.AQUA
							+ tntlevel);
					return true;
				} else if (args[0].equalsIgnoreCase("reload")) {
					if (!sender.hasPermission("tntlevel.reload")) {
						sender.sendMessage(ChatColor.RED
								+ "You do not have permission to use this command!");
						return true;
					}
					this.reloadConfig();
					tntlevel = getConfig().getInt("tntlevel", 30);

					getConfig().set("TnT Level Limit", tntlevel);
					saveConfig();

					sender.sendMessage(ChatColor.GREEN
							+ "TnTLevelLimit config has been reloaded.");
					return true;
				}
				sender.sendMessage("---------- TnT Level Limit ----------");
				sender.sendMessage("/tntlevel limit <blocklimit>");
				sender.sendMessage("/tntlevel reload");
				return true;
			}
		}

		if (!(sender.hasPermission("tntlevel.setlimit") || sender
				.hasPermission("tntlevel.reload"))) {
			sender.sendMessage(ChatColor.RED
					+ "You do not have permission to use this command!");
		} else {
			sender.sendMessage("---------- TnT Level Limit ----------");
			sender.sendMessage("/tntlevel limit <blocklimit>");
			sender.sendMessage("/tntlevel reload");
		}
		return false;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlace(BlockPlaceEvent event) {
		if (event.isCancelled())
			return;
		if (event.getBlockPlaced().getType() == Material.TNT) {
			if (!event.getPlayer().hasPermission("tntlevel.log")
					&& !canPlaceTnt(event.getBlockPlaced().getLocation(),
							event.getPlayer())) {

				event.setCancelled(true);

				System.out.println("[TnTLevelLimit] "
						+ event.getPlayer().getName()
						+ " tried to place tnt at ["
						+ event.getBlockPlaced().getX() + ", "
						+ event.getBlockPlaced().getY() + ", "
						+ event.getBlockPlaced().getZ() + "]");

				messageOPs(ChatColor.RED + "[TnTLevelLimit] "
						+ event.getPlayer().getName()
						+ " tried to place tnt at ["
						+ event.getBlockPlaced().getX() + ", "
						+ event.getBlockPlaced().getY() + ", "
						+ event.getBlockPlaced().getZ() + "]");

				event.getPlayer().sendMessage(
						ChatColor.RED + "You may only place TnT at level "
								+ ChatColor.GREEN + tntlevel + ChatColor.RED
								+ " or below.");
			}
		}

	}

	public void messageOPs(String message) {
		Player[] players = Bukkit.getServer().getOnlinePlayers();

		for (Player player : players) {
			if (player.isOp())
				player.sendMessage(message);
		}

	}

	public boolean canPlaceTnt(Location location, Player player) {

		if (location.getBlockY() < tntlevel)
			return true;

		return false;
	}
}

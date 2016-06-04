package com.keensta.omnipremium.transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.keensta.omnipremium.OmniPremium;
import com.keensta.omnipremium.transaction.PermissionAbstraction.PremiumRank;

public class BuyCraftHandler implements CommandExecutor, Listener {
	
	private Logger log = Logger.getLogger("Minecraft");
	private OmniPremium plugin;
	private Permission perms;
	private PermissionAbstraction permAbstraction;
	
	public BuyCraftHandler(OmniPremium plugin) {
		this.plugin = plugin;
		perms = plugin.getPermissions();
		permAbstraction = new PermissionAbstraction(plugin);
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		//Command: okbuypackage <packageID> <name>  (Stores UUID incase player changes name)
		if (command.getName().equalsIgnoreCase("okbuypackage")) {
			if (sender instanceof Player)
				return true;
			
			if (args.length < 2) {
				log.info("[OmniPremium] Invalid okbuypackage invocation: " + args);
				return true;
			}
			
			String pkg = args[0];
			String user = args[1];
			
			log.info("[OmniPremium] Handling package " + pkg + " for user " + user);
			
			Player ply = Bukkit.getPlayerExact(user);
			UUID uuid = UUID.fromString(checkForUserUUID(user, ply)); //Check for UUID as we would rather use the UUID then name
			
			if (uuid != null) 
				ply = Bukkit.getPlayer(uuid);
			
			if (ply == null) {
				log.severe("[OmniPremium] Received package " + pkg + " for user " + user + ", but user not online!");
				
				if ( pkg.equalsIgnoreCase("omnikeys") ) {
					if (args.length < 3) {
						log.info("[OmniPremium] Invalid okbuypackage(Omnikeys) invocation: " + args);
						return true;
					}
					
					int keyamount = Integer.parseInt(args[2]);
					pkg = pkg + ":" + Integer.toString(keyamount);
				}
				
				FileConfiguration cfg = plugin.getConfig("ranks");
				List<String> packages = new ArrayList<String>();
				String offlineID = checkForUserUUID(user, ply);
				
				if (offlineID == null)
					offlineID = user;
				
				if (cfg.contains(offlineID))
					packages = cfg.getStringList(offlineID + ".packages");
				
				if (!packages.contains(pkg))
					packages.add(pkg);
				
				cfg.set(offlineID + ".packages", packages);
				plugin.saveConfig(cfg, "ranks");
				
				return true;
			}
			
			if (pkg.equalsIgnoreCase("VIP")) {
				permAbstraction.setPremiumRank(ply, PremiumRank.VIP);
				ply.sendMessage(ChatColor.GREEN + "Thank you very much for your purchase!");
				ply.sendMessage(ChatColor.GREEN + "You are now a VIP member. Enjoy!");
			} else if (pkg.equalsIgnoreCase("Sponsor")) {
				permAbstraction.setPremiumRank(ply, PremiumRank.SPONSOR);
				ply.sendMessage(ChatColor.GREEN + "Thank you very much for your purchase!");
				ply.sendMessage(ChatColor.GREEN + "You are now a sponsor member. Enjoy!");
			} else if (pkg.equalsIgnoreCase("omnikeys")) {
				
				if (args.length < 3) {
					log.info("[OmniPremium] Invalid okbuypackage(Omnikeys) invocation: " + args);
					return true;
				}
				
				int keyamount = Integer.parseInt(args[2]);
				
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "omnikey command needs to go here");
				ply.sendMessage(ChatColor.GREEN + "Thank you very much for your purchase!");
				ply.sendMessage(ChatColor.GREEN + "Enjoy your keys!");
			} else {
				log.severe("[OmniPremium] Unknown package: " + pkg);
			}
			
		}
		//Removes any packages (For seasonal ranks)
		else if (command.getName().equalsIgnoreCase("okremovepackage")) {
			//TODO: Code in removal of packages that only last a short time. 
		}
		
		return false;
	}
	
	@EventHandler
	public void playerJoinEvent(PlayerJoinEvent ev) {
		FileConfiguration cfg = plugin.getConfig("ranks");
		String playerName = checkForUserUUID(ev.getPlayer().getName(), ev.getPlayer());
		
		if (playerName == null)
			playerName = ev.getPlayer().getName();
		
		if(cfg.contains(playerName)) {
			
			for(String pkg : cfg.getStringList(playerName + ".packages")) {
				
				if (pkg.contains(":")) {
					String[] keyPkg = pkg.split(":");
					
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "okbuypackage " + keyPkg[0] + " " + ev.getPlayer().getName() + " " + keyPkg[1]);
				}
				
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "okbuypackage " + pkg + " " + playerName);
			}
			
			cfg.set(playerName, null);
			plugin.saveConfig(cfg, "ranks");
			
		}
		
	}

	private String checkForUserUUID(String user, Player ply) {
		FileConfiguration data = plugin.getConfig("userdata");
		String uuid = null;
		//User is always the original username used to purchase package, we check our database for the UUID if it doesn't exist
		//we add it, if it does then we use that instead. Just incase of them changing there name to something other then purchase name :O
		
		if (data.contains(user) && data.contains(user + ".uuid")) {
			uuid = data.getString(user + ".uuid");
		} else {
			if (ply != null) {
				data.set(user + ".uuid", ply.getUniqueId().toString());
				uuid = ply.getUniqueId().toString();
			} else {
				OfflinePlayer op = Bukkit.getOfflinePlayer(user);
				
				if (op.hasPlayedBefore()) {
					uuid = op.getUniqueId().toString();
				}
			}
		}
		
		return uuid;
	}

}

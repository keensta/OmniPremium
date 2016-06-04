package com.keensta.omnipremium;

import java.io.File;
import java.util.logging.Logger;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.keensta.omnipremium.transaction.BuyCraftHandler;
import com.keensta.omnipremium.util.ConfigManager;

public class OmniPremium extends JavaPlugin {
	
	Logger log = Logger.getLogger("Minecraft");
	
	//Command Handler
	private BuyCraftHandler bcHandler = null;
	
	//Util
	private ConfigManager cfgManager = null;
	
	//Vault
	private Permission permission = null;
	
	@Override
	public void onDisable() {
		log.info("[OmniPremium] shutting down");
	}
	
	@Override
	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		File dataDirectory = this.getDataFolder();
		
		if (dataDirectory.exists() == false)
			dataDirectory.mkdirs();
		
		
		if (!setupPermissions()) {
			log.severe("[OmniPremium] Unable to find permissions plugin!!");
		}
		
		cfgManager = new ConfigManager(this);
		
		//Initialize command handler
		bcHandler = new BuyCraftHandler(this);
		
		getCommand("okbuypackage").setExecutor(bcHandler);
		getCommand("okremovepackage").setExecutor(bcHandler);
		
		log.info("[OmniPermium] Enabled");
	}

	private boolean setupPermissions() {
		final RegisteredServiceProvider<Permission> permissionProvider = this.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null) {
			permission = permissionProvider.getProvider();
		}
		return (permission != null);
	}
	
	public Permission getPermissions() {
		return permission;
	}
	
	public FileConfiguration getConfig(String name) {
		return cfgManager.getConfig(name);
	}
	
	public void saveConfig(FileConfiguration cfg, String name) {
		cfgManager.saveConfig(cfg, name);
	}
	
}
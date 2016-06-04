package com.keensta.omnipremium.util;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.keensta.omnipremium.OmniPremium;

public class ConfigManager {
	
	private OmniPremium plugin;
	
	public ConfigManager(OmniPremium plugin) {
		this.plugin = plugin;
	}
	
	public FileConfiguration getConfig(String name) {
		return YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), name+".yml"));
	}
	
	public void saveConfig(FileConfiguration cfg, String name) {
		try {
			cfg.save(new File(plugin.getDataFolder(), name+".yml"));
		} catch (final IOException e) {
			Logger.getLogger("Minecraft").severe("[OmniPremium] Failed to save" + name + ".yml!");
			e.printStackTrace();
		}
	}

}

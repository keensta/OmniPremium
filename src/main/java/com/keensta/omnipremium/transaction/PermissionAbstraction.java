package com.keensta.omnipremium.transaction;

import org.bukkit.entity.Player;

import net.milkbowl.vault.permission.Permission;

import com.keensta.omnipremium.OmniPremium;

public class PermissionAbstraction {
	
	@SuppressWarnings("unused")
	private OmniPremium plugin;
	private Permission perms;
	
	public enum PremiumRank {
		VIP("vip", 1), SPONSOR("sponsor", 2);
		
		private String groupName;
		private int tier;
		
		private PremiumRank(String groupName, int tier) {
			this.groupName = groupName;
			this.tier = tier;
		}
		
		public String getGroupName() {
			return groupName;
		}
		
		public int getTier() {
			return tier;
		}
	}
	
	public PermissionAbstraction(OmniPremium plugin) {
		this.plugin = plugin;
		this.perms = plugin.getPermissions();
	}
	
	//Build in tier system if any other ranks are added they can be tier to decide what overrules what
	public void setPremiumRank(Player player, PremiumRank rank) {
		for (PremiumRank i : PremiumRank.values()) {
			if (perms.playerInGroup(player, i.getGroupName())) {
				if (i.getTier() > rank.getTier()) {
					return;
				}
				
				perms.playerRemoveGroup(player, i.getGroupName());
			}
		}
		
		perms.playerAddGroup(player, rank.getGroupName());
	}

}

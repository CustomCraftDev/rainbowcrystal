package rainbowcrystal;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;


/**
 * @author DieFriiks / CustomCraftDev / undeaD_D
 * @category RainbowCrystal Chat plugin
 * @version 1.0
 */
public class RainbowCrystal extends JavaPlugin {
	
    FileConfiguration config;
    String nopermission_msg;
    EventListener events;
	  ArrayList<Block> blocks;
	  CustomEnchantment ench = new CustomEnchantment(69);
	
    boolean event;
    boolean debug;
    boolean isplayer;
    
    int speed;
    String itemname;
    String[] lore;
	  ItemStack crystal;
	  DyeColor[] itemcolor;
	  byte[] data;
	  Material material;
    
    
	/**
     * on Plugin enable
     */
	public void onEnable() {
		
		loadConfig();
    	say("Config loaded");
    	    	
    	events = new EventListener(this);
    	say("Eventlistener loaded");
    	
    	activate();
	}
	

	/**
     * on Plugin disable
     */
	public void onDisable() {
		unregister();
		Bukkit.getScheduler().cancelTasks(this);
		saveblocks();
				
	}

	
	private void unregister() {
		try {
			Field byIdField = Enchantment.class.getDeclaredField("byId");
			Field byNameField = Enchantment.class.getDeclaredField("byName");
			 
			byIdField.setAccessible(true);
			byNameField.setAccessible(true);
			 
			@SuppressWarnings("unchecked")
			HashMap<Integer, Enchantment> byId = (HashMap<Integer, Enchantment>) byIdField.get(null);
			@SuppressWarnings("unchecked")
			HashMap<String, Enchantment> byName = (HashMap<String, Enchantment>) byNameField.get(null);
			 
			if(byId.containsKey(69))
			byId.remove(69);
			 
			if(byName.containsKey(getName()))
			byName.remove(getName());
		}catch (Exception ignored) {}
	}


	/**
     * on Command
     * @param sender - command sender
     * @param cmd - command
     * @param alias
     * @return true or false
     */
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		isplayer = false;
		Player p = null;
		
		if ((sender instanceof Player)) {
			p = (Player)sender;
			isplayer = true;
		}
			if(cmd.getName().equalsIgnoreCase("crystal") && args.length != 0){
				
				// give
				if(args[0].equalsIgnoreCase("give")){
					if(isplayer){
						if(p.hasPermission("rc.give")){
							p.getInventory().addItem(crystal);
						return true;
					}
						else{
							p.sendMessage(nopermission_msg);
							return true;
						}
					}
					else{
						System.out.println("[RainbowCrystal] ingame only ...");
						return true;
					}
				}
				
				// disable
				if(args[0].equalsIgnoreCase("disable")){
					if(isplayer){
						if(p.hasPermission("rc.disable")){
							this.setEnabled(false);
							p.sendMessage(ChatColor.RED + "[RainbowCrystal] was disabled");
							say("disabled by " + p.getName());
						return true;
					}
						else{
							p.sendMessage(nopermission_msg);
							return true;
						}
					}
					else{
							this.setEnabled(false);
						System.out.println("[RainbowCrystal] was disabled");
						return true;
					}
				}
				
				// reset
				if(args[0].equalsIgnoreCase("reset")){
					if(isplayer){
						if(p.hasPermission("rc.reset")){
						    File configFile = new File(getDataFolder(), "config.yml");
						    configFile.delete();
						    saveDefaultConfig();
							p.sendMessage(ChatColor.RED + "[RainbowCrystal] config reset");
						    reload();
							p.sendMessage(ChatColor.RED + "[RainbowCrystal] was reloaded");
							say("reset by " + p.getName());
						return true;
						}
						else{
							p.sendMessage(nopermission_msg);
							return true;
						}
					}
					else{
					    File configFile = new File(getDataFolder(), "config.yml");
					    configFile.delete();
					    saveDefaultConfig();
					    System.out.println("[RainbowCrystal] config reset");
					    reload();
					    System.out.println("[RainbowCrystal] was reloaded");
					    return true;
					}
				}
				
				// reload
				if(args[0].equalsIgnoreCase("reload")){
					if(isplayer){
						if(p.hasPermission("rc.reload")){
							reload();
							p.sendMessage(ChatColor.RED + "[RainbowCrystal] was reloaded");
							say("reloaded by " + p.getName());
						return true;
					}
						else{
							p.sendMessage(nopermission_msg);
							return true;
						}
					}
					else{
						reload();
					    System.out.println("[RainbowCrystal] was reloaded");
						return true;
				    }
				}
			}
		
		// nothing to do here \o/
		return false;
	}
		
	
	/**
     * load config settings
     */
	@SuppressWarnings("deprecation")
	private void loadConfig() {
		config = getConfig();
		config.options().copyDefaults(true);
		saveConfig();
		
		itemname = ChatColor.translateAlternateColorCodes('&', config.getString("Crystal.Name"));
		
		List<String> itemlore = config.getStringList("Crystal.Lore");
		lore = new String[itemlore.size()];
		for(int i = 0; i < itemlore.size(); i++){
			lore[i] = ChatColor.translateAlternateColorCodes('&', itemlore.get(i));
		}

		List<String> color = config.getStringList("Crystal.Color");
		itemcolor = new DyeColor[color.size()];
		data = new byte[color.size()];
		for(int k = 0; k < color.size(); k++){
			itemcolor[k] = DyeColor.valueOf(color.get(k));
			data[k] = itemcolor[k].getData();
		}
		
		if(config.getBoolean("Crystal.Type")){
			material = Material.STAINED_GLASS;
		}
		else{
			material = Material.WOOL;
		}
		
		blocks = new ArrayList<Block>();
		int amount = config.getInt("Locations.amount");
		if(amount != 0){
			for(int i = 0; i < amount; i++){
			    String [] arg = config.getString("Locations." + i).split(",");
			    Double [] parsed = new Double[3];
			        for(int a = 0;a<3;a++){
			              parsed[a] = Double.parseDouble(arg[a+1]);
			        }
			  Location block = new Location(this.getServer().getWorld(arg[0]), parsed[0], parsed[1], parsed[2]);
			  blocks.add(block.getBlock());
			  config.set("Locations." + i, null);
			}
			config.set("Locations.amount", 0);
		}
		
		speed = config.getInt("Crystal.Speed");
		debug = config.getBoolean("debug");
		nopermission_msg = ChatColor.translateAlternateColorCodes('&', config.getString("nopermission-msg"));
		saveConfig();
		
    	setupitem();
	}
    
	
	private void activate() {
		if(blocks.size() != 0){
			for(int i = 0; i < blocks.size(); i++){
				events.changeblock(blocks.get(i));
			}
		}
	}
	
	
	/*
	 *  setup item
	 */
	public void setupitem(){
		crystal = new ItemStack(Material.DIAMOND, 1);
		crystal.addUnsafeEnchantment(this.ench, 1);
		ItemMeta m = crystal.getItemMeta();
		m.setDisplayName(itemname);
			List<String> liste = new ArrayList<String>();
			for(int i = 0; i < lore.length; i++){
				liste.add(lore[i]);
			}
			m.setLore(liste);
		crystal.setItemMeta(m);
	}
	
	
    /**
     * reload
     */
    private void reload(){
 	   	try {
			    config = null;
			    nopermission_msg = null;
			    
			    System.gc();
				reloadConfig();
				loadConfig();
			
 	   	} catch (Exception e) {
        	if(debug){
        		e.printStackTrace();
        	}
        }
    }
    
    
    /**
     * print to console
     * @param message to print
     */
	public void say(String out) {
		if(debug){
			System.out.println("[RainbowCrystal] [DEBUG] " + out);
		}
	}
	
	
	public void saveblocks(){
		if(blocks.size() != 0){
			for(int i = 0; i < blocks.size(); i++){
				Location loc = blocks.get(i).getLocation();
				String s = loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ();
				config.set("Locations." + i, s);
			}
			config.set("Locations.amount", blocks.size());
			saveConfig();
		}

	}
	
	
	public void Locations(boolean b, Block b2) {
		if(b){
			blocks.add(b2);
		}
		else{
			blocks.remove(b2);
		}
	}
}

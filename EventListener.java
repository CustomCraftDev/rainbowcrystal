package rainbowcrystal;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitScheduler;


/**
 * made by DieFriiks / CustomCraftDev
 */
public class EventListener implements Listener {
	
	RainbowCrystal plugin;
	BukkitScheduler scheduler;
	
	/**
     * Constructor
     * @param RainbowCrystal regioninv
     */
	public EventListener(RainbowCrystal plugin) {
		this.plugin = plugin;
    	plugin.getServer().getPluginManager().registerEvents(this, plugin);
		scheduler = Bukkit.getServer().getScheduler();
	}
	
	
	@EventHandler
	public void onBreak(BlockBreakEvent e){
		if(e.getBlock().hasMetadata("crystal")){
			if(e.getPlayer().hasPermission("rc.break")){
				List<MetadataValue> l = e.getBlock().getMetadata("crystal");
				scheduler.cancelTask((int) l.get(0).asInt());
				e.getBlock().setType(Material.AIR);
				e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), plugin.crystal);
				plugin.Locations(false, e.getBlock());
				e.setCancelled(true);
			}
			else{
				e.getPlayer().sendMessage(plugin.nopermission_msg);
				e.setCancelled(true);
			}
		}
	}
	
	
	public void changeblock(final Block b){
        int i = scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
            @SuppressWarnings("deprecation")
			@Override
            public void run() {
            	for(int i = 0; i < plugin.data.length; i++){
            		if(plugin.data[i] == b.getData()){
            			if(i < plugin.data.length-1){
            				b.setData(plugin.data[i+1]);
            				break;
            			}
            			else{
            				b.setData(plugin.data[0]);
            				break;
            			}
            		}
            	}
            }
        }, 0L, plugin.speed);
		b.setMetadata("crystal", new FixedMetadataValue(plugin, i));
	}
	

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInteract(PlayerInteractEvent e){
		if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
			Player p = e.getPlayer();
			ItemStack item = p.getItemInHand();
			if(item.getType().equals(Material.DIAMOND)){
				if(item.getItemMeta().getDisplayName().equals(plugin.itemname)){
					if(p.hasPermission("rc.place")){
						Block b = e.getClickedBlock().getRelative(e.getBlockFace());
						b.setType(plugin.material);
						b.setData(plugin.data[0]);
							changeblock(b);
							plugin.Locations(true, b);
							ItemStack hand = p.getItemInHand();
							if(hand.getAmount() > 0){
								hand.setAmount(hand.getAmount()-1);
								p.setItemInHand(hand);
							}
							else{
								p.getInventory().remove(hand);
							}
						e.setCancelled(true);
					}
					else{
						p.sendMessage(plugin.nopermission_msg);
						e.setCancelled(true);
					}
				}
			}
		}
	}
	
	
}

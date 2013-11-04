package me.stuntguy3000.scrollingsigns;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SSPlugin extends JavaPlugin implements Listener {

    public Logger log = Bukkit.getLogger();
    public List<ScrollingSign> signs = new ArrayList<ScrollingSign>();
    public HashMap<String, Integer> removeLineToBreak = new HashMap<String, Integer>();
    public SSUtil util;

    public void onEnable() {
        util = new SSUtil(this);
        
        util.log(Level.INFO, "&3ScrollingSigns v" + this.getDescription().getVersion() + " by stuntguy3000 enabled!");
        
        loadSigns();
        signTimer();
        
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
            util.log(Level.INFO, "&3Metrics started!");
        } catch (IOException e) {
        	util.log(Level.WARNING, "&cMetrics failed to start!");
        	e.printStackTrace();
        }
        
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    private void signTimer() {
    	this.getServer().getScheduler().cancelTasks(this);
    	
    	getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
    		@Override
			public void run() {
    			for (ScrollingSign scrollSign : signs) {
    				int x = scrollSign.getX();
    				int y = scrollSign.getY();
    				int z = scrollSign.getZ();
    				World world = Bukkit.getWorld(scrollSign.getWorld());
    				
    				// Valid world?
    				if (world != null) {
    					Block signBlock = world.getBlockAt(x, y, z);
    					
    					// Valid block?
    					if (signBlock != null) {
    						
    						// Is it a sign?
    						if (signBlock.getType() == Material.SIGN_POST || signBlock.getType() == Material.WALL_SIGN) {
    							Sign sign = (Sign) signBlock.getState();
    							
    							if (scrollSign.getLine1() != null) sign.setLine(0, util.colour(scrollSign.getLine1().next()));
    							if (scrollSign.getLine2() != null) sign.setLine(1, util.colour(scrollSign.getLine2().next()));
    							if (scrollSign.getLine3() != null) sign.setLine(2, util.colour(scrollSign.getLine3().next()));
    							if (scrollSign.getLine4() != null) sign.setLine(3, util.colour(scrollSign.getLine4().next()));	
    							
    							sign.update(true);
    						} else {
    							// Nope!
    							signs.remove(scrollSign);
    						}
    					} else {
    						// Nope!
    						signs.remove(scrollSign);
    					}
    				} else {
    					// Nope!
    					signs.remove(scrollSign);
    				}
    			}
			}
    	}, 20L, 20L);
	}

	private void loadSigns() {
		// For now we will use a test sign
    	
    	signs.add(new ScrollingSign(100, 75, 100, "plotmeh", 
    			new Line("Hey there! Welcome to my server."), 
    			null, null, null));
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String args[]) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
	    	
	    	if (args.length == 0) {
		    	p.sendMessage(util.colour("&3[ScrollingSigns] &bScrollingSigns v" + this.getDescription().getVersion() + " by stuntguy3000"));
		    	p.sendMessage(util.colour("&3[ScrollingSigns] &7For commands, type /ss help"));
		    	return true;
		    }
	    	
	    	if (args.length == 1) {
	    		if (args[0].equalsIgnoreCase("help")) {
	    			helpOption(p, "", "Display plugin information");
	    			helpOption(p, "help", "Display command help");
	    			helpOption(p, "set <line> <message>", "Set text to scroll");
	    			helpOption(p, "unset <line>", "Unset and clear a line on a sign");
	    			return true;
	    		}
	    	}
	    	
	    	if (args.length == 2) {
	    		if (args[0].equalsIgnoreCase("unset")) {
	    			
	    			String unsetInput = args[1];
	    			
	    			try {
	    				int num = Integer.parseInt(unsetInput);
	    				
	    				if (num < 1 || num > 4) {
	    					p.sendMessage(util.colour("&3[ScrollingSigns] &cPlease choose a number between 1 to 4!"));
	    					return true;
	    				}
	    				
	    				// All good to go
	    				p.sendMessage(util.colour("&3[ScrollingSigns] &7To remove line " + num + " please break the scrolling sign."));
	    				
	    				removeLineToBreak.remove(p.getName());
	    				removeLineToBreak.put(p.getName(), num);
	    			} catch (NumberFormatException ex) {
	    				p.sendMessage(util.colour("&3[ScrollingSigns] &cThat is not a valid number!"));
	    			}
	    			
	    			return true;
	    		}
	    	}
	    	
	    	p.sendMessage(util.colour("&3[ScrollingSigns] &7Invalid command or syntax. Type /ss help"));
	    } else {
	    	util.log(Level.WARNING, "&cThis command is not supported by console!");
	    }
	    
    	return false;
    }

	private void helpOption(Player p, String cmd, String description) {
		p.sendMessage(util.colour("&3[Help] " + "&f/ss " + cmd + " &8- &7" + description));
	}
	
	@EventHandler
	public void onBreakBlock(BlockBreakEvent event) {
		Player p = event.getPlayer();
		
		if (removeLineToBreak.containsKey(p.getName())) {
			event.setCancelled(true);
			
			removeLineToBreak.remove(p.getName());
			
			if (event.getBlock().getType() == Material.SIGN_POST || event.getBlock().getType() == Material.WALL_SIGN) {
				int line = removeLineToBreak.remove(p.getName());
				ScrollingSign sign = null;
				
				for (ScrollingSign s : signs) {
					if (s.getX() == event.getBlock().getX() && 
						s.getY() == event.getBlock().getY() &&  
						s.getZ() == event.getBlock().getZ())
						sign = s;
				}
				
				if (sign == null) {
					p.sendMessage(util.colour("&cThat wasn't a scrolling sign! To unset a line, please break a scrolling sign (you need to run the command again)"));
					return;
				}
				
				signs.remove(sign);
				
				if (line == 1)
					sign.setLine1(null);

				if (line == 2)
					sign.setLine2(null);

				if (line == 3)
					sign.setLine3(null);

				if (line == 4)
					sign.setLine4(null);
				
				signs.add(sign);
			} else {
				p.sendMessage(util.colour("&cThat wasn't a sign! To unset a line, please break a sign (you need to run the command again)"));
			}
		}
	}
}

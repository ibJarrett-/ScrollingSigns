package me.stuntguy3000.scrollingsigns;

import java.util.ArrayList;
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
import org.bukkit.plugin.java.JavaPlugin;

public class SSPlugin extends JavaPlugin {

    public Logger log = Bukkit.getLogger();
    public ArrayList<ScrollingSign> signs = new ArrayList<ScrollingSign>();
    public SSUtil util;

    public void onEnable() {
        util = new SSUtil(this);
        
        util.log(Level.INFO, "&3ScrollingSigns v" + this.getDescription().getVersion() + " by stuntguy3000 enabled!");
        
        loadSigns();
        signTimer();
    }

    private void signTimer() {
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
		    }
	    	
	    	if (args.length == 1) {
	    		if (args[0].equalsIgnoreCase("help")) {
	    			helpOption(p, "", "Display plugin information");
	    			helpOption(p, "help", "Display command help");
	    			helpOption(p, "setsign", "Enable a sign to have scrolling text");
	    			helpOption(p, "set <line> <message>", "Set text to scroll");
	    			helpOption(p, "unset <line>", "Unset and clear a line on a sign");
	    		}
	    	}
	    } else {
	    	util.log(Level.WARNING, "&cThis command is not supported by console!");
	    }
	    
    	return false;
    }

	private void helpOption(Player p, String cmd, String description) {
		// [Help] is probably going to change
		p.sendMessage(util.colour("&3[Help] " + "&f/ss " + cmd + " &8- &7" + description));
	}
}

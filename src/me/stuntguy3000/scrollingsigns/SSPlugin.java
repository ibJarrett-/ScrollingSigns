package me.stuntguy3000.scrollingsigns;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SSPlugin extends JavaPlugin {

    public Logger log = Bukkit.getLogger();
    public SSUtil util;

    public void onEnable() {
        util = new SSUtil(this);
        
        util.log(Level.INFO, "&3ScrollingSigns v" + this.getDescription().getVersion() + " by stuntguy3000 enabled!");
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
	    			helpOption(p, "", "Display plugin information");
	    			helpOption(p, "", "Display plugin information");
	    			helpOption(p, "", "Display plugin information");
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

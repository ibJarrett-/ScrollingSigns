package me.stuntguy3000.scrollingsigns;

import java.util.logging.Level;

import org.bukkit.ChatColor;

public class SSUtil {
    
    private SSPlugin plugin;
    
    public SSUtil(SSPlugin instance) {
        this.plugin = instance;
    }
    
    public void log(Level level, String message) {
    	plugin.log.log(level, colour(message));
    }
    
    public String colour(String message) {
    	return ChatColor.translateAlternateColorCodes('&', message);
    }
}

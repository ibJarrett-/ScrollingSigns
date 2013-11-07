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
    public HashMap<String, Integer> removeLineToSet = new HashMap<String, Integer>();
    public HashMap<String, String> removeLineToSetText = new HashMap<String, String>();
    public SSUtil util;

    public void onEnable() {
        saveDefaultConfig();
        
        util = new SSUtil(this);

        util.log(Level.INFO, "ScrollingSigns v" + this.getDescription().getVersion() + " by stuntguy3000 enabled!");

        loadSigns();
        
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
            util.log(Level.INFO, "Metrics started!");
        } catch (IOException e) {
            util.log(Level.WARNING, "Metrics failed to start!");
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
                            }
                        }
                    }
                }
            }
        }, 5L, 5L);
    }

    private void loadSigns() {
        this.getServer().getScheduler().cancelTasks(this);

        signs.clear();
        
        for (String key : getConfig().getKeys(false)) {
            if (key.contains("-")) {
                String[] word = key.split("-");
                
                if (word[3] != null) {
                    signs.add(new ScrollingSign(tryInt(word[0]), tryInt(word[1]), tryInt(word[2]), word[3], 
                            (this.getConfig().getString(key + ".line1") != null ? new Line(this.getConfig().getString(key + ".line1")) : null), 
                            (this.getConfig().getString(key + ".line2") != null ? new Line(this.getConfig().getString(key + ".line2")) : null), 
                            (this.getConfig().getString(key + ".line3") != null ? new Line(this.getConfig().getString(key + ".line3")) : null), 
                            (this.getConfig().getString(key + ".line4") != null ? new Line(this.getConfig().getString(key + ".line4")) : null)));
                }
            }
        }
        
        signTimer();
    }

    private int tryInt(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return -1;
        }
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
                    p.sendMessage(util.colour("&3[ScrollingSigns] " + "&f/ss " + cmd + "&8- &7" + "Display plugin information"));
                    helpOption(p, "help", "Display command help");
                    helpOption(p, "set <line> <message>", "Set text to scroll");
                    helpOption(p, "unset <line>", "Unset and clear a line on a sign");
                    return true;
                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("unset")) {
                    if (p.hasPermission("ss.unset")) {
                        String unsetInput = args[1];

                        try {
                            int num = Integer.parseInt(unsetInput);

                            if (num < 1 || num > 4) {
                                p.sendMessage(util.colour("&3[ScrollingSigns] &cPlease choose a number between 1 to 4!"));
                                return true;
                            }

                            // All good to go
                            p.sendMessage(util.colour("&3[ScrollingSigns] &7To remove line " + num + " please break the sign."));

                            removeLineToBreak.remove(p.getName());
                            removeLineToBreak.put(p.getName(), num);
                        } catch (NumberFormatException ex) {
                            p.sendMessage(util.colour("&3[ScrollingSigns] &cThat is not a valid number!"));
                        }

                    } else p.sendMessage(util.colour("&3[ScrollingSigns] &cYou cannot use this command!"));

                    return true;
                }
            } else {
                if (p.hasPermission("ss.unset")) {
                    if (args[0].equalsIgnoreCase("set")) {
                        String unsetInput = args[1];

                        try {
                            int num = Integer.parseInt(unsetInput);

                            if (num < 1 || num > 4) {
                                p.sendMessage(util.colour("&3[ScrollingSigns] &cPlease choose a number between 1 to 4!"));
                                return true;
                            }

                            // All good to go
                            p.sendMessage(util.colour("&3[ScrollingSigns] &7To set line " + num + " please break the sign."));

                            StringBuilder sb = new StringBuilder();
                            for (int i = 2; i < args.length; i++){
                            sb.append(args[i]).append(" ");
                            }
                             
                            String wholeMessage = sb.toString().trim();
                            
                            removeLineToSet.remove(p.getName());
                            removeLineToSet.put(p.getName(), num);
                            
                            removeLineToSetText.remove(p.getName());
                            removeLineToSetText.put(p.getName(), wholeMessage);
                        } catch (NumberFormatException ex) {
                            p.sendMessage(util.colour("&3[ScrollingSigns] &cThat is not a valid number!"));
                        }

                        return true;
                    }
                    
                } else p.sendMessage(util.colour("&3[ScrollingSigns] &7Invalid command or syntax. Type /ss help"));
            }

            p.sendMessage(util.colour("&3[ScrollingSigns] &7Invalid command or syntax. Type /ss help"));
        } else {
            util.log(Level.WARNING, "This command is not supported by console!");
        }

        return false;
    }

    private void helpOption(Player p, String cmd, String description) {
        p.sendMessage(util.colour("&3[ScrollingSigns] " + "&f/ss " + cmd + " &8- &7" + description));
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        Player p = event.getPlayer();

        if (removeLineToBreak.containsKey(p.getName())) {
            event.setCancelled(true);
            int line = removeLineToBreak.remove(p.getName());
            
            if (event.getBlock().getType() == Material.SIGN_POST || event.getBlock().getType() == Material.WALL_SIGN) {
                Sign signBlock = (Sign) event.getBlock().getState();
                ScrollingSign sign = null;

                for (ScrollingSign s : signs) {
                    if (s.getX() == event.getBlock().getX() && 
                            s.getY() == event.getBlock().getY() &&  
                            s.getZ() == event.getBlock().getZ())
                        sign = s;
                }

                if (sign == null) {
                    p.sendMessage(util.colour("&3[ScrollingSigns] &cThat wasn't a scrolling sign! To unset a line, please break a scrolling sign (you need to run the command again)"));
                    return;
                }

                signBlock.setLine(line - 1, "");
                
                String configName = signBlock.getX() + "-" + signBlock.getY() + "-" + signBlock.getZ() + "-" + signBlock.getWorld().getName();
                
                getConfig().set(configName + ".line" + line, null);
                saveConfig();
                
                loadSigns();
                
                signBlock.update(true);
                
                p.sendMessage(util.colour("&3[ScrollingSigns] &7Sign line Removed."));
            } else {
                p.sendMessage(util.colour("&3[ScrollingSigns] &cThat wasn't a sign! To unset a line, please break a sign (you need to run the command again)"));
                
            }
        }
        
        if (removeLineToSet.containsKey(p.getName())) {
            event.setCancelled(true);
            int line = removeLineToSet.remove(p.getName());
            
            if (event.getBlock().getType() == Material.SIGN_POST || event.getBlock().getType() == Material.WALL_SIGN) {
                Sign signBlock = (Sign) event.getBlock().getState();
                
                String configName = signBlock.getX() + "-" + signBlock.getY() + "-" + signBlock.getZ() + "-" + signBlock.getWorld().getName();
                
                getConfig().set(configName + ".line" + line, removeLineToSetText.remove(p.getName()));
                saveConfig();
                
                loadSigns();
                
                p.sendMessage(util.colour("&3[ScrollingSigns] &7Sign line added."));
            } else {
                p.sendMessage(util.colour("&3[ScrollingSigns] &cThat wasn't a sign! Toset a line, please break a sign (you need to run the command again)"));
            }
        }
    }
}

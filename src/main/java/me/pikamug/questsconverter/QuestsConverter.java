package me.pikamug.questsconverter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.blackvein.quests.Quests;
import me.blackvein.quests.storage.StorageType;
import me.pikamug.questsconverter.conversion.Conversion;
import me.pikamug.questsconverter.conversion.ConversionType;

public class QuestsConverter extends JavaPlugin implements CommandExecutor {
    private Quests quests;
    protected boolean lock = false;
    
    @Override
    public void onEnable() {
        quests = (Quests) Bukkit.getServer().getPluginManager().getPlugin("Quests");
    }
    
    @Override
    public void onDisable() {
        if (lock) {
            getLogger().severe("You have disabled QuestsConverter during a conversion! Not cool.");
        }
    }
    
    public Quests getQuests() {
        return quests;
    }
    
    public boolean isConverting() {
        return lock;
    }
    
    public void setConversionLock(final boolean value) {
        lock = value;
    }
    
    @Override
    public boolean onCommand(final CommandSender cs, final Command cmd, final String label, final String[] args) {
        if (cs instanceof Player) {
            if (!((Player)cs).hasPermission("qconvert.all")) {
                cs.sendMessage(ChatColor.RED + "You do not have permission!");
                return false;
            }
        }
        if (cmd.getName().equalsIgnoreCase("qconvert")) {
            qConvertHandler(cs, args);
        }
        return false;
    }
    
    private boolean qConvertHandler(final CommandSender cs, final String[] args) {
        if (args.length <= 2) {
            showUsage(cs);
        } else if (args.length > 2) {
            final ConversionType type = ConversionType.valueOf(args[1].toLowerCase());
            final StorageType source = StorageType.valueOf(args[2].toLowerCase());
            final StorageType target = StorageType.valueOf(args[3].toLowerCase());
            if (type == null || source == null || target == null) {
                showUsage(cs);
            } else if (lock) {
                cs.sendMessage(ChatColor.YELLOW + "Conversion already in progress!");
            } else {
                getLogger().info(cs.getName() + " started conversion " + args[2] + " to " + args[3]);
                if (cs instanceof Player) {
                    cs.sendMessage(ChatColor.YELLOW + "Starting conversion from " + args[2] + " to " + args[3]);
                }
                final Conversion c = new Conversion(this);
                c.beginConversion(type, source, target);
            }
        }
        return false;
    }
    
    private void showUsage(final CommandSender cs) {
        cs.sendMessage(ChatColor.GOLD + "- Quests Converter -");
        cs.sendMessage(ChatColor.YELLOW + "Usage: /qconvert playerdata [source] [target]");
        cs.sendMessage(ChatColor.YELLOW + "Valid sources/targets are: yaml, mysql, custom");
    }
}

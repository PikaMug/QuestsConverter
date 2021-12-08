package me.pikamug.questsconverter;

import java.util.concurrent.CompletableFuture;

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
            return false;
        }
        ConversionType type;
        StorageType source;
        StorageType target;
        try {
            type = ConversionType.valueOf(args[0].toUpperCase());
            source = StorageType.valueOf(args[1].toUpperCase());
            target = StorageType.valueOf(args[2].toUpperCase());
        } catch (final IllegalArgumentException e) {
            showUsage(cs);
            return false;
        }
        if (lock) {
            cs.sendMessage(ChatColor.YELLOW + "Conversion already in progress!");
        } else {
            getLogger().info(cs.getName() + " started conversion from " + args[1] + " to " + args[2]);
            if (cs instanceof Player) {
                cs.sendMessage(ChatColor.YELLOW + "Starting conversion from " + args[1] + " to " + args[2]);
            }
            final Conversion c = new Conversion(this);
            final CompletableFuture<Boolean> p = c.beginConversion(type, source, target);
            p.thenRun(() -> cs.sendMessage("[QuestsConverter]" + ChatColor.GREEN + "Done!"));
        }
        return true;
    }
    
    private void showUsage(final CommandSender cs) {
        cs.sendMessage(ChatColor.YELLOW + "- Quests Converter -");
        cs.sendMessage(ChatColor.YELLOW + "Usage: " + ChatColor.GOLD + "/qconvert playerdata [source] [target]");
        cs.sendMessage(ChatColor.YELLOW + "Valid source or target values are:");
        for (final StorageType type: StorageType.values()) {
            cs.sendMessage(ChatColor.YELLOW + " - " + ChatColor.LIGHT_PURPLE + type.getName());
        }
    }
}

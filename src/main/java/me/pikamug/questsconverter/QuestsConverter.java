/*
 * Copyright (c) 2020 PikaMug. All rights reserved.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
 * NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package me.pikamug.questsconverter;

import java.util.concurrent.CompletableFuture;

import me.pikamug.quests.Quests;
import me.pikamug.quests.enums.StorageType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

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
        if (!cs.hasPermission("qconvert.all")) {
            cs.sendMessage(ChatColor.RED + "You do not have permission!");
            return false;
        }
        if (cmd.getName().equalsIgnoreCase("qconvert")) {
            qConvertHandler(cs, args);
        }
        return false;
    }
    
    private void qConvertHandler(final CommandSender cs, final String[] args) {
        if (args.length <= 2) {
            showUsage(cs);
            return;
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
            return;
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
            p.thenRun(() -> cs.sendMessage("[QuestsConverter] " + ChatColor.GREEN + "Done!"));
        }
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

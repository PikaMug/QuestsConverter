package me.pikamug.questsconverter.conversion;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import me.blackvein.quests.Quester;
import me.blackvein.quests.Quests;
import me.blackvein.quests.storage.StorageFactory;
import me.blackvein.quests.storage.StorageType;
import me.blackvein.quests.storage.implementation.StorageImplementation;
import me.pikamug.questsconverter.QuestsConverter;

public class Conversion {
    private final QuestsConverter plugin;
    
    public Conversion(final QuestsConverter plugin) {
        this.plugin = plugin;
    }
    
    private <T> CompletableFuture<T> makeFuture(final Callable<T> supplier) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return supplier.call();
            } catch (final Exception e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<Boolean> beginConversion(final ConversionType type, final StorageType source, final StorageType target) {
        return makeFuture(() -> {
            if (type.equals(ConversionType.PLAYERDATA)) {
                return convertPlayerData(type, source, target);
            }
            return false;
        });
    }
    
    private boolean convertPlayerData(final ConversionType type, final StorageType source, final StorageType target) throws Exception {
        plugin.setConversionLock(true);
        final Quests quests = plugin.getQuests();
        final StorageFactory factory = new StorageFactory(quests);
        final StorageImplementation entry = factory.createNewImplementation(source);
        entry.init();
        final StorageImplementation exit = factory.createNewImplementation(target);
        exit.init();
        
        for (final UUID uuid : entry.getSavedUniqueIds()) {
            try {
                final Quester quester = entry.loadQuesterData(uuid);
                if (quester != null) {
                    exit.saveQuesterData(quester);
                    plugin.getLogger().info("Successfully transferred data of Quester " + uuid.toString());
                }
            } catch (final Exception e) {
                plugin.getLogger().severe("Failed to transfer data of Quester " + uuid.toString());
                e.printStackTrace();
            }
        }
        entry.close();
        exit.close();
        plugin.setConversionLock(false);
        return true;
    }
}
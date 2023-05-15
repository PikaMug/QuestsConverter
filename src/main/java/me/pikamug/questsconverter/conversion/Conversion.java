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

package me.pikamug.questsconverter.conversion;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import me.blackvein.quests.Quester;
import me.blackvein.quests.Quests;
import me.blackvein.quests.player.IQuester;
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

    public CompletableFuture<Boolean> beginConversion(final ConversionType type,
                                                      final StorageType source, final StorageType target) {
        return makeFuture(() -> {
            if (type.equals(ConversionType.PLAYERDATA)) {
                return convertPlayerData(source, target);
            }
            return false;
        });
    }
    
    private boolean convertPlayerData(final StorageType source, final StorageType target) throws Exception {
        plugin.setConversionLock(true);
        final Quests quests = plugin.getQuests();
        final StorageFactory factory = new StorageFactory(quests);

        plugin.getLogger().info("Any quests-hikari errors below this can be safely ignored.");

        final StorageImplementation entry = factory.createNewImplementation(source);
        entry.init();

        if (entry.getSavedUniqueIds().isEmpty()) {
            plugin.getLogger().warning("No source data found! Are all config values correct?");
            entry.close();
            return false;
        }

        final StorageImplementation exit = factory.createNewImplementation(target);
        exit.init();
        
        for (final UUID uuid : entry.getSavedUniqueIds()) {
            try {
                final IQuester quester = entry.loadQuester(uuid);
                if (quester != null) {
                    exit.saveQuester(quester);
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
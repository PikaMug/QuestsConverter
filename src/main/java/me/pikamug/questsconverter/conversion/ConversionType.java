package me.pikamug.questsconverter.conversion;

import java.util.List;

import com.google.common.collect.ImmutableList;

public enum ConversionType {
    
    PLAYERDATA("PlayerData", "Playerdata", "playerdata");
    
    private final String name;

    private final List<String> identifiers;

    ConversionType(final String name, final String... identifiers) {
        this.name = name;
        this.identifiers = ImmutableList.copyOf(identifiers);
    }

    public static ConversionType parse(final String name, final ConversionType def) {
        for (final ConversionType t : values()) {
            for (final String id : t.getIdentifiers()) {
                if (id.equalsIgnoreCase(name)) {
                    return t;
                }
            }
        }
        return def;
    }

    public String getName() {
        return name;
    }

    public List<String> getIdentifiers() {
        return identifiers;
    }
}
